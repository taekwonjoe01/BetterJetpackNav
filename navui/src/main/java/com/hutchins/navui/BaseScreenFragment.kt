package com.hutchins.navui

import android.content.Context
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navcore.BaseNavFragment

/**
 * Created by jhutchins on 5/10/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
abstract class BaseScreenFragment : BaseNavFragment() {
    protected lateinit var navViewActivity: NavViewActivity

    protected val toolbarController: ToolbarController = ToolbarController()

    override fun onCurrentNavFragment(destination: NavDestination) {
        this.toolbarController.onActive(navViewActivity.navigationViewDelegate.toolbarDelegate, destination)
    }

    override fun onNotCurrentNavFragment() {
        this.toolbarController.onInactive()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavViewActivity) {
            navViewActivity = context
        } else {
            throw RuntimeException("$context must be child class of NavViewActivity!")
        }
    }

    companion object {
        // Keep these in sync with lotus/values/strings.xml navigation_toolbar_visibility values
        private const val TOOLBAR_VISIBILITY_VISIBLE = 0
        private const val TOOLBAR_VISIBILITY_INVISIBLE = 1
        private const val TOOLBAR_VISIBILITY_GONE = 2
    }

    private abstract class BaseToolbarTransaction
    private class VisibilityToolbarTransaction(val toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) : BaseToolbarTransaction()
    private class TitleToolbarTransaction(val title: CharSequence) : BaseToolbarTransaction()
    private class OverrideUpToolbarTransaction(val override: Boolean) : BaseToolbarTransaction()
    private class NavViewVisibilityToolbarTransaction(val hideNavView: Boolean) : BaseToolbarTransaction()
    private class AnimateVisibilityTransaction(val stateToAnimateTo: ToolbarDelegate.ToolbarVisibilityState, val animationDuration: Long) : BaseToolbarTransaction()

    inner class ToolbarController {
        private var toolbarDelegate: ToolbarDelegate? = null
        private var destination: NavDestination? = null

        private val transactionsQueue = ArrayList<BaseToolbarTransaction>()

        var toolbarVisibilityState: ToolbarDelegate.ToolbarVisibilityState? = null
            private set
            get() = toolbarDelegate?.toolbarState ?: null

        internal fun onActive(toolbarDelegate: ToolbarDelegate, destination: NavDestination) {
            this.toolbarDelegate = toolbarDelegate
            this.destination = destination

            val hideToolbar = destination.defaultArguments.getBoolean(
                navViewActivity.getString(R.string.navigation_hide_toolbar), false)
            val saveToolbarVisibilityState = if (hideToolbar) TOOLBAR_VISIBILITY_GONE else {
                destination.defaultArguments.getInt(
                    navViewActivity.getString(R.string.navigation_toolbar_visibility))
            }

            val desiredState = when (saveToolbarVisibilityState) {
                TOOLBAR_VISIBILITY_GONE -> ToolbarDelegate.ToolbarVisibilityState.GONE
                TOOLBAR_VISIBILITY_VISIBLE -> ToolbarDelegate.ToolbarVisibilityState.VISIBLE
                TOOLBAR_VISIBILITY_INVISIBLE -> ToolbarDelegate.ToolbarVisibilityState.INVISIBLE
                else -> ToolbarDelegate.ToolbarVisibilityState.VISIBLE
            }

            toolbarDelegate.setToolbarVisibilityState(desiredState)

            val title = destination.label ?: ""
            toolbarDelegate.setToolbarTitle(title)

            val overrideUp = destination.defaultArguments.getBoolean(
                navViewActivity.getString(R.string.navigation_override_up), false)
            val isStartDestination = (findNavController().graph.startDestination == destination.id)
            if (isStartDestination) {
                toolbarDelegate.setOverrideUpNavigationToRoot(overrideUp)
            } else {
                toolbarDelegate.setOverrideUpNavigationToRoot(!overrideUp)
            }

            val navViewGone = destination.defaultArguments.getBoolean(
                navViewActivity.getString(R.string.navigation_view_gone), false)
            toolbarDelegate.updateNavViewVisibility(!navViewGone)

            conditionallyProcessNextTransaction()
        }

        internal fun onInactive() {
            this.toolbarDelegate = null
            this.destination = null
        }

        fun setToolbarVisibility(toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) {
            transactionsQueue.add(VisibilityToolbarTransaction(toolbarVisibility))
            conditionallyProcessNextTransaction()
        }

        fun setToolbarTitle(title: CharSequence) {
            transactionsQueue.add(TitleToolbarTransaction(title))
            conditionallyProcessNextTransaction()
        }

        fun setToolbarOverrideUp(overrideUp: Boolean) {
            transactionsQueue.add(OverrideUpToolbarTransaction(overrideUp))
            conditionallyProcessNextTransaction()
        }

        fun setNavViewVisibility(showNavView: Boolean) {
            transactionsQueue.add(NavViewVisibilityToolbarTransaction(!showNavView))
            conditionallyProcessNextTransaction()
        }

        fun animateToolbarVisibility(toVisibilityState: ToolbarDelegate.ToolbarVisibilityState, animationDurationMs: Long) {
            transactionsQueue.add(AnimateVisibilityTransaction(toVisibilityState, animationDurationMs))
            conditionallyProcessNextTransaction()
        }

        private fun conditionallyProcessNextTransaction() {
            toolbarDelegate?.let {tbd ->
                destination?.let { dest ->
                    if (transactionsQueue.isNotEmpty()) {
                        val transaction = transactionsQueue.removeAt(0)
                        val bundle = dest.defaultArguments
                        when (transaction) {
                            is VisibilityToolbarTransaction -> {
                                val visibilityValue = when (transaction.toolbarVisibility) {
                                    ToolbarDelegate.ToolbarVisibilityState.VISIBLE -> TOOLBAR_VISIBILITY_VISIBLE
                                    ToolbarDelegate.ToolbarVisibilityState.INVISIBLE -> TOOLBAR_VISIBILITY_INVISIBLE
                                    ToolbarDelegate.ToolbarVisibilityState.GONE -> TOOLBAR_VISIBILITY_GONE
                                }
                                bundle.putInt(navViewActivity.getString(R.string.navigation_toolbar_visibility), visibilityValue)
                                tbd.setToolbarVisibilityState(transaction.toolbarVisibility)
                                dest.addDefaultArguments(bundle)
                            }
                            is TitleToolbarTransaction -> {
                                // Does this persist? Who knows!
                                dest.label = transaction.title
                                tbd.setToolbarTitle(transaction.title)
                            }
                            is OverrideUpToolbarTransaction -> {
                                bundle.putBoolean(navViewActivity.getString(R.string.navigation_override_up), transaction.override)
                                val isStartDestination = (findNavController().graph.startDestination == dest.id)
                                if (isStartDestination) {
                                    tbd.setOverrideUpNavigationToRoot(transaction.override)
                                } else {
                                    tbd.setOverrideUpNavigationToRoot(!transaction.override)
                                }
                                dest.addDefaultArguments(bundle)
                            }
                            is NavViewVisibilityToolbarTransaction -> {
                                bundle.putBoolean(navViewActivity.getString(R.string.navigation_view_gone), transaction.hideNavView)
                                tbd.updateNavViewVisibility(!transaction.hideNavView)
                                dest.addDefaultArguments(bundle)
                            }
                            is AnimateVisibilityTransaction -> {
                                val visibilityValue = when (transaction.stateToAnimateTo) {
                                    ToolbarDelegate.ToolbarVisibilityState.VISIBLE -> TOOLBAR_VISIBILITY_VISIBLE
                                    ToolbarDelegate.ToolbarVisibilityState.INVISIBLE -> TOOLBAR_VISIBILITY_INVISIBLE
                                    ToolbarDelegate.ToolbarVisibilityState.GONE -> TOOLBAR_VISIBILITY_GONE
                                }
                                bundle.putInt(navViewActivity.getString(R.string.navigation_toolbar_visibility), visibilityValue)
                                tbd.animateToToolbarVisibilityState(transaction.stateToAnimateTo, transaction.animationDuration)
                                dest.addDefaultArguments(bundle)
                            }
                        }

                        conditionallyProcessNextTransaction()
                    }
                }
            }
        }
    }
}