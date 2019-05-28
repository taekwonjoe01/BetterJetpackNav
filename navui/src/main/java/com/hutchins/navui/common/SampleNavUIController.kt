package com.hutchins.navui.common

import android.content.Context
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.hutchins.navui.R
import com.hutchins.navui.core.BaseNavUIController
import com.hutchins.navui.core.BaseScreenFragment
import com.hutchins.navui.core.NavigationViewDelegate

class SampleNavUIController(private val screenFragment: BaseScreenFragment) : BaseNavUIController() {
    companion object {
        // Keep these in sync with lotus/values/strings.xml navigation_toolbar_visibility values
        private const val TOOLBAR_VISIBILITY_VISIBLE = 0
        private const val TOOLBAR_VISIBILITY_INVISIBLE = 1
        private const val TOOLBAR_VISIBILITY_GONE = 2
    }
    private val navUIControllerViewmodel = ViewModelProviders.of(screenFragment).get(NavUIControllerViewmodel::class.java)
    private val context: Context = screenFragment.context!!

    override fun onNavUIActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()

        // For all features, we first defer to the state in the viewModel if it is set. Otherwise, we use the navDirections
        // defaultArguments values.

        // Toolbar visibility state initialization:
        val toolbarVisibilityState = navUIControllerViewmodel.toolbarVisibilityState ?: run {
            val intReference: Int? = destination.arguments[context.getString(R.string.navigation_toolbar_visibility)]?.defaultValue as? Int

            val desiredState = when (intReference?.let { context.resources.getInteger(it) } ?: run { TOOLBAR_VISIBILITY_VISIBLE }) {
                TOOLBAR_VISIBILITY_GONE -> ToolbarDelegate.ToolbarVisibilityState.GONE
                TOOLBAR_VISIBILITY_VISIBLE -> ToolbarDelegate.ToolbarVisibilityState.VISIBLE
                TOOLBAR_VISIBILITY_INVISIBLE -> ToolbarDelegate.ToolbarVisibilityState.INVISIBLE
                else -> ToolbarDelegate.ToolbarVisibilityState.VISIBLE
            }
            desiredState
        }
        toolbarDelegate.setToolbarVisibilityState(toolbarVisibilityState)


        // Toolbar title string initialization:
        val title = navUIControllerViewmodel.toolbarTitle ?: destination.label ?: ""
        toolbarDelegate.setToolbarTitle(title)

        // Subtitle initialization:
        val subtitle = navUIControllerViewmodel.toolbarSubtitle ?: destination.arguments[context.getString(R.string.navigation_toolbar_subtitle)]?.defaultValue as? String ?:  ""
        toolbarDelegate.setToolbarSubtitle(subtitle)


        // Toolbar up override initialization:
        val overrideUp = navUIControllerViewmodel.overrideUp ?: destination.arguments[context.getString(R.string.navigation_override_up)]?.defaultValue as? Boolean ?: false
        val isStartDestination = (navigationViewDelegate.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination.id)
        if (isStartDestination) {
            toolbarDelegate.setUpNavigationVisible(overrideUp)
        } else {
            toolbarDelegate.setUpNavigationVisible(!overrideUp)
        }

        // Nav view visibility initialization:
        val showNavView = navUIControllerViewmodel.showNavView ?: !(destination.arguments[
            context.getString(R.string.navigation_view_gone)]?.defaultValue as? Boolean ?: false)
        toolbarDelegate.updateNavViewVisibility(showNavView)

        // Action menu initialization:
        val actionMenuResId = navUIControllerViewmodel.actionMenuResourceId ?: destination.arguments[
            context.getString(R.string.navigation_toolbar_action_menu)]?.defaultValue as? Int ?: -1
        if (actionMenuResId == -1) {
            toolbarDelegate.clearToolbarActionMenu()
        } else {
            toolbarDelegate.setToolbarActionMenu(
                actionMenuResId,
                Toolbar.OnMenuItemClickListener { item -> screenFragment.onOptionsItemSelected(item) })
        }
    }

    fun setToolbarVisibility(toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) {
        navUIControllerViewmodel.toolbarVisibilityState = toolbarVisibility
        navigationViewDelegate?.let {
            val toolbarDelegate = it.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.setToolbarVisibilityState(toolbarVisibility)
        }
    }

    fun setToolbarTitle(title: String) {
        navUIControllerViewmodel.toolbarTitle = title
        navigationViewDelegate?.let {
            val toolbarDelegate = it.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.setToolbarTitle(title)
        }
    }

    fun setToolbarSubtitle(subtitle: String) {
        navUIControllerViewmodel.toolbarSubtitle = subtitle
        navigationViewDelegate?.let {
            val toolbarDelegate = it.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.setToolbarSubtitle(subtitle)
        }
    }

    fun setToolbarOverrideUp(overrideUp: Boolean) {
        navUIControllerViewmodel.overrideUp = overrideUp
        navigationViewDelegate?.let {
            val toolbarDelegate = it.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val isStartDestination = (it.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination!!.id)
            if (isStartDestination) {
                toolbarDelegate.setUpNavigationVisible(overrideUp)
            } else {
                toolbarDelegate.setUpNavigationVisible(!overrideUp)
            }
        }
    }

    fun setNavViewVisibility(showNavView: Boolean) {
        navUIControllerViewmodel.showNavView = showNavView
        navigationViewDelegate?.let {
            val toolbarDelegate = it.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.updateNavViewVisibility(showNavView)
        }
    }

    fun animateToolbarVisibility(toVisibilityState: ToolbarDelegate.ToolbarVisibilityState, animationDurationMs: Long) {
        navUIControllerViewmodel.toolbarVisibilityState = toVisibilityState
        navigationViewDelegate?.let {
            val toolbarDelegate = it.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.animateToToolbarVisibilityState(toVisibilityState, animationDurationMs)
        }
    }

    fun setToolbarActionMenu(actionMenuResId: Int) {
        navUIControllerViewmodel.actionMenuResourceId = actionMenuResId
        navigationViewDelegate?.let {
            val toolbarDelegate = it.asTestNavViewDelegate().getNavUiToolbarDelegate()
            if (actionMenuResId == -1) {
                toolbarDelegate.clearToolbarActionMenu()
            } else {
                toolbarDelegate.setToolbarActionMenu(
                    actionMenuResId,
                    Toolbar.OnMenuItemClickListener { item -> screenFragment.onOptionsItemSelected(item) })
            }
        }
    }

    private fun NavigationViewDelegate.asTestNavViewDelegate() : TestNavViewDelegate {
        return this as TestNavViewDelegate
    }

    interface TestNavViewDelegate {
        fun getNavigationController(): NavController
        fun getNavUiToolbarDelegate(): ToolbarDelegate
    }
}

