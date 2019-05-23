package com.hutchins.navui.sampleviewdelegates

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.hutchins.navui.BaseNavUIController
import com.hutchins.navui.BaseScreenFragment
import com.hutchins.navui.NavigationViewDelegate
import com.hutchins.navui.R

class SampleNavUIController(screenFragment: BaseScreenFragment) : BaseNavUIController() {
    companion object {
        // Keep these in sync with lotus/values/strings.xml navigation_toolbar_visibility values
        private const val TOOLBAR_VISIBILITY_VISIBLE = 0
        private const val TOOLBAR_VISIBILITY_INVISIBLE = 1
        private const val TOOLBAR_VISIBILITY_GONE = 2
    }
    private val navUIControllerViewmodel = ViewModelProviders.of(screenFragment).get(NavUIControllerViewmodel::class.java)
    private val context: Context = screenFragment.context!!

    inner class VisibilityToolbarTransaction(val toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.setToolbarVisibilityState(toolbarVisibility)
        }
    }
    inner class TitleToolbarTransaction(val title: String) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.setToolbarTitle(title)
        }
    }
    inner class OverrideUpToolbarTransaction(val override: Boolean) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val isStartDestination = (navigationViewDelegate.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination.id)
            if (isStartDestination) {
                toolbarDelegate.setUpNavigationVisible(override)
            } else {
                toolbarDelegate.setUpNavigationVisible(!override)
            }
        }
    }
    inner class NavViewVisibilityToolbarTransaction(val hideNavView: Boolean) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.updateNavViewVisibility(!hideNavView)
        }
    }
    inner class AnimateVisibilityTransaction(val stateToAnimateTo: ToolbarDelegate.ToolbarVisibilityState, val animationDuration: Long) :
        BaseNavUIControllerTransaction {
        override fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            toolbarDelegate.animateToToolbarVisibilityState(stateToAnimateTo, animationDuration)
        }
    }

    override fun onNavUIActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()

        // For all features, we first defer to the state in the viewModel if it is set. Otherwise, we use the navDirections
        // defaultArguments values.

        // Toolbar visibility state initialization:
        val toolbarVisibilityState = navUIControllerViewmodel.toolbarVisibilityState ?: kotlin.run {
            val intState = destination.defaultArguments.getInt(context.getString(R.string.navigation_toolbar_visibility))

            val desiredState = when (intState) {
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


        // Toolbar up override initialization:
        val overrideUp = navUIControllerViewmodel.overrideUp ?: destination.defaultArguments.getBoolean(
            context.getString(R.string.navigation_override_up), false)
        val isStartDestination = (navigationViewDelegate.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination.id)
        if (isStartDestination) {
            toolbarDelegate.setUpNavigationVisible(overrideUp)
        } else {
            toolbarDelegate.setUpNavigationVisible(!overrideUp)
        }


        // Nav view visibility initialization:
        val navViewGone = navUIControllerViewmodel.navViewGone ?: destination.defaultArguments.getBoolean(
            context.getString(R.string.navigation_view_gone), false)
        toolbarDelegate.updateNavViewVisibility(!navViewGone)
    }

    fun setToolbarVisibility(toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) {
        navUIControllerViewmodel.toolbarVisibilityState = toolbarVisibility
        addTransaction(VisibilityToolbarTransaction(toolbarVisibility))
    }

    fun setToolbarTitle(title: String) {
        navUIControllerViewmodel.toolbarTitle = title
        addTransaction(TitleToolbarTransaction(title))
    }

    fun setToolbarOverrideUp(overrideUp: Boolean) {
        navUIControllerViewmodel.overrideUp = overrideUp
        addTransaction(OverrideUpToolbarTransaction(overrideUp))
    }

    fun setNavViewVisibility(showNavView: Boolean) {
        navUIControllerViewmodel.navViewGone = !showNavView
        addTransaction(NavViewVisibilityToolbarTransaction(!showNavView))
    }

    fun animateToolbarVisibility(toVisibilityState: ToolbarDelegate.ToolbarVisibilityState, animationDurationMs: Long) {
        navUIControllerViewmodel.toolbarVisibilityState = toVisibilityState
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

