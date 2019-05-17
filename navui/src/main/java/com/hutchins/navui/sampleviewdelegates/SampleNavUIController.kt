package com.hutchins.navui.sampleviewdelegates

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.hutchins.navui.BaseNavUIController
import com.hutchins.navui.NavigationViewDelegate
import com.hutchins.navui.R

/**
 * Created by jhutchins on 5/16/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class SampleNavUIController(context: Context) : BaseNavUIController(context) {
    companion object {
        // Keep these in sync with lotus/values/strings.xml navigation_toolbar_visibility values
        private const val TOOLBAR_VISIBILITY_VISIBLE = 0
        private const val TOOLBAR_VISIBILITY_INVISIBLE = 1
        private const val TOOLBAR_VISIBILITY_GONE = 2
    }

    inner class VisibilityToolbarTransaction(val toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val bundle = destination.defaultArguments
            val visibilityValue = when (toolbarVisibility) {
                ToolbarDelegate.ToolbarVisibilityState.VISIBLE -> TOOLBAR_VISIBILITY_VISIBLE
                ToolbarDelegate.ToolbarVisibilityState.INVISIBLE -> TOOLBAR_VISIBILITY_INVISIBLE
                ToolbarDelegate.ToolbarVisibilityState.GONE -> TOOLBAR_VISIBILITY_GONE
            }
            bundle.putInt(context.getString(R.string.navigation_toolbar_visibility), visibilityValue)
            toolbarDelegate.setToolbarVisibilityState(toolbarVisibility)
            destination.addDefaultArguments(bundle)
        }
    }
    inner class TitleToolbarTransaction(val title: CharSequence) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val bundle = destination.defaultArguments
            // Does this persist? Who knows!
            destination.label = title
            toolbarDelegate.setToolbarTitle(title)
        }
    }
    inner class OverrideUpToolbarTransaction(val override: Boolean) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val bundle = destination.defaultArguments
            bundle.putBoolean(context.getString(R.string.navigation_override_up), override)
            val isStartDestination = (navigationViewDelegate.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination.id)
            if (isStartDestination) {
                toolbarDelegate.setUpNavigationVisible(override)
            } else {
                toolbarDelegate.setUpNavigationVisible(!override)
            }
            destination.addDefaultArguments(bundle)
        }
    }
    inner class NavViewVisibilityToolbarTransaction(val hideNavView: Boolean) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val bundle = destination.defaultArguments
            bundle.putBoolean(context.getString(R.string.navigation_view_gone), hideNavView)
            toolbarDelegate.updateNavViewVisibility(!hideNavView)
            destination.addDefaultArguments(bundle)
        }
    }
    inner class AnimateVisibilityTransaction(val stateToAnimateTo: ToolbarDelegate.ToolbarVisibilityState, val animationDuration: Long) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val bundle = destination.defaultArguments
            val visibilityValue = when (stateToAnimateTo) {
                ToolbarDelegate.ToolbarVisibilityState.VISIBLE -> TOOLBAR_VISIBILITY_VISIBLE
                ToolbarDelegate.ToolbarVisibilityState.INVISIBLE -> TOOLBAR_VISIBILITY_INVISIBLE
                ToolbarDelegate.ToolbarVisibilityState.GONE -> TOOLBAR_VISIBILITY_GONE
            }
            bundle.putInt(context.getString(R.string.navigation_toolbar_visibility), visibilityValue)
            toolbarDelegate.animateToToolbarVisibilityState(stateToAnimateTo, animationDuration)
            destination.addDefaultArguments(bundle)
        }
    }

    override fun onNavUIActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()

        val hideToolbar = destination.defaultArguments.getBoolean(
            context.getString(R.string.navigation_hide_toolbar), false)
        val saveToolbarVisibilityState = if (hideToolbar) TOOLBAR_VISIBILITY_GONE else {
            destination.defaultArguments.getInt(
                context.getString(R.string.navigation_toolbar_visibility))
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
            context.getString(R.string.navigation_override_up), false)
        val isStartDestination = (navigationViewDelegate.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination.id)
        if (isStartDestination) {
            toolbarDelegate.setUpNavigationVisible(overrideUp)
        } else {
            toolbarDelegate.setUpNavigationVisible(!overrideUp)
        }

        val navViewGone = destination.defaultArguments.getBoolean(
            context.getString(R.string.navigation_view_gone), false)
        toolbarDelegate.updateNavViewVisibility(!navViewGone)
    }

    fun setToolbarVisibility(toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) {
        addTransaction(VisibilityToolbarTransaction(toolbarVisibility))
    }

    fun setToolbarTitle(title: CharSequence) {
        addTransaction(TitleToolbarTransaction(title))
    }

    fun setToolbarOverrideUp(overrideUp: Boolean) {
        addTransaction(OverrideUpToolbarTransaction(overrideUp))
    }

    fun setNavViewVisibility(showNavView: Boolean) {
        addTransaction(NavViewVisibilityToolbarTransaction(!showNavView))
    }

    fun animateToolbarVisibility(toVisibilityState: ToolbarDelegate.ToolbarVisibilityState, animationDurationMs: Long) {
        addTransaction(
            AnimateVisibilityTransaction(
                toVisibilityState,
                animationDurationMs
            )
        )
    }

    fun NavigationViewDelegate.asTestNavViewDelegate() : TestNavViewDelegate {
        return this as TestNavViewDelegate
    }

    interface TestNavViewDelegate {
        fun getNavigationController(): NavController
        fun getNavUiToolbarDelegate(): ToolbarDelegate
    }
}

