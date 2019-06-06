package com.hutchins.navui.common

import android.content.Context
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.hutchins.navui.R
import com.hutchins.navui.core.BaseNavUIController
import com.hutchins.navui.core.BaseScreenFragment
import com.hutchins.navui.core.NavUISetting
import com.hutchins.navui.core.NavigationViewDelegate

class SampleNavUIController(private val screenFragment: BaseScreenFragment) : BaseNavUIController(screenFragment) {
    companion object {
        // Keep these in sync with lotus/values/strings.xml navigation_toolbar_visibility values
        private const val TOOLBAR_VISIBILITY_VISIBLE = 0
        private const val TOOLBAR_VISIBILITY_INVISIBLE = 1
        private const val TOOLBAR_VISIBILITY_GONE = 2
    }
    private val context: Context = screenFragment.context!!

    private val titleSetting: ToolbarTitleSetting
    private val subTitleSetting: ToolbarSubTitleSetting
    private val overrideUpSetting: OverrideUpSetting
    private val toolbarVisibilitySetting: ToolbarVisibilitySetting
    private val navVisibilitySetting: NavViewVisibilitySetting
    private val actionMenuSetting: ActionMenuSetting

    init {
        val settings = initController()
        toolbarVisibilitySetting = settings[0] as ToolbarVisibilitySetting
        titleSetting = settings[1] as ToolbarTitleSetting
        subTitleSetting = settings[2] as ToolbarSubTitleSetting
        overrideUpSetting = settings[3] as OverrideUpSetting
        navVisibilitySetting = settings[4] as NavViewVisibilitySetting
        actionMenuSetting = settings[5] as ActionMenuSetting
    }

    override fun buildSettings(): List<NavUISetting<*>> {
        // The order of these settings matter, as the abstraction iterates over the list to apply the settings.
        // We want visibility to take precedence over all others, so it should be first.
        return arrayListOf(ToolbarVisibilitySetting(), ToolbarTitleSetting(), ToolbarSubTitleSetting(), OverrideUpSetting(), NavViewVisibilitySetting(), ActionMenuSetting())
    }

    fun setToolbarVisibility(toolbarVisibility: ToolbarDelegate.ToolbarVisibilityState) {
        toolbarVisibilitySetting.setting = ToolbarVisibilityTransition.InstantTransition(toolbarVisibility)
    }

    fun setToolbarTitle(title: String) {
        titleSetting.setting = title
    }

    fun setToolbarSubtitle(subtitle: String) {
        subTitleSetting.setting = subtitle
    }

    fun setToolbarOverrideUp(overrideUp: Boolean) {
        overrideUpSetting.setting = overrideUp
    }

    fun setNavViewVisibility(showNavView: Boolean) {
        navVisibilitySetting.setting = showNavView
    }

    fun animateToolbarVisibility(toVisibilityState: ToolbarDelegate.ToolbarVisibilityState, animationDurationMs: Long) {
        toolbarVisibilitySetting.setting = ToolbarVisibilityTransition.AnimateTransition(toVisibilityState, animationDurationMs)
    }

    fun setToolbarActionMenu(actionMenuResId: Int) {
        actionMenuSetting.setting = actionMenuResId
    }

    private fun NavigationViewDelegate.asTestNavViewDelegate() : TestNavViewDelegate {
        return this as TestNavViewDelegate
    }

    interface TestNavViewDelegate {
        fun getNavigationController(): NavController
        fun getNavUiToolbarDelegate(): ToolbarDelegate
        fun setNavViewVisible(visible: Boolean)
    }

    inner class ToolbarTitleSetting : NavUISetting<String>() {
        override fun applySetting(
            destination: NavDestination,
            navigationViewDelegate: NavigationViewDelegate,
            isNavigationTransition: Boolean,
            setting: String?
        ) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            setting?.let {
                toolbarDelegate.setToolbarTitle(setting)
            } ?: run {
                toolbarDelegate.setToolbarTitle(destination.label ?: "")
            }
        }
    }

    inner class ToolbarSubTitleSetting : NavUISetting<String>() {
        override fun applySetting(
            destination: NavDestination,
            navigationViewDelegate: NavigationViewDelegate,
            isNavigationTransition: Boolean,
            setting: String?
        ) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            setting?.let {
                toolbarDelegate.setToolbarSubtitle(it)
            } ?: run {
                toolbarDelegate.setToolbarSubtitle(destination.arguments[context.getString(R.string.navigation_toolbar_subtitle)]?.defaultValue as? String ?:  "")
            }
        }
    }

    inner class OverrideUpSetting : NavUISetting<Boolean>() {
        override fun applySetting(
            destination: NavDestination,
            navigationViewDelegate: NavigationViewDelegate,
            isNavigationTransition: Boolean,
            setting: Boolean?
        ) {
            setting?.let {
                setUpNavigationVisible(destination, navigationViewDelegate, it)
            } ?: run {
                setUpNavigationVisible(destination, navigationViewDelegate, destination.arguments[context.getString(R.string.navigation_override_up)]?.defaultValue as? Boolean ?: false)
            }
        }

        private fun setUpNavigationVisible(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate, overrideUp: Boolean) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val isStartDestination = (navigationViewDelegate.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination.id)
            if (isStartDestination) {
                toolbarDelegate.setUpNavigationVisible(overrideUp)
            } else {
                toolbarDelegate.setUpNavigationVisible(!overrideUp)
            }
        }
    }

    inner class ToolbarVisibilitySetting : NavUISetting<ToolbarVisibilityTransition>() {

        override fun applySetting(
            destination: NavDestination,
            navigationViewDelegate: NavigationViewDelegate,
            isNavigationTransition: Boolean,
            setting: ToolbarVisibilityTransition?
        ) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            setting?.let {
                when (it) {
                    is ToolbarVisibilityTransition.InstantTransition -> {
                        toolbarDelegate.setToolbarVisibilityState(it.toState)
                    }
                    is ToolbarVisibilityTransition.AnimateTransition -> {
                        if (isNavigationTransition) {
                            toolbarDelegate.setToolbarVisibilityState(it.toState)
                        } else {
                            toolbarDelegate.animateToToolbarVisibilityState(it.toState, it.animationDurationMs)
                        }
                    }
                }
            } ?: run {
                val intReference: Int? = destination.arguments[context.getString(R.string.navigation_toolbar_visibility)]?.defaultValue as? Int

                val desiredState = when (intReference?.let { context.resources.getInteger(it) } ?: run { TOOLBAR_VISIBILITY_VISIBLE }) {
                    TOOLBAR_VISIBILITY_GONE -> ToolbarDelegate.ToolbarVisibilityState.GONE
                    TOOLBAR_VISIBILITY_VISIBLE -> ToolbarDelegate.ToolbarVisibilityState.VISIBLE
                    TOOLBAR_VISIBILITY_INVISIBLE -> ToolbarDelegate.ToolbarVisibilityState.INVISIBLE
                    else -> ToolbarDelegate.ToolbarVisibilityState.VISIBLE
                }
                toolbarDelegate.setToolbarVisibilityState(desiredState)
            }
        }
    }

    inner class NavViewVisibilitySetting : NavUISetting<Boolean>() {
        override fun applySetting(
            destination: NavDestination,
            navigationViewDelegate: NavigationViewDelegate,
            isNavigationTransition: Boolean,
            setting: Boolean?
        ) {
            setting?.let {
                val showNavView = it
                navigationViewDelegate.asTestNavViewDelegate().setNavViewVisible(showNavView)
            } ?: run {
                val showNavView = !(destination.arguments[context.getString(R.string.navigation_view_gone)]?.defaultValue as? Boolean ?: false)
                navigationViewDelegate.asTestNavViewDelegate().setNavViewVisible(showNavView)
            }
        }
    }

    inner class ActionMenuSetting : NavUISetting<Int>() {
        override fun applySetting(
            destination: NavDestination,
            navigationViewDelegate: NavigationViewDelegate,
            isNavigationTransition: Boolean,
            setting: Int?
        ) {
            val toolbarDelegate = navigationViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            setting?.let {
                val actionMenuResId = it
                if (actionMenuResId == -1) {
                    toolbarDelegate.clearToolbarActionMenu()
                } else {
                    toolbarDelegate.setToolbarActionMenu(
                        actionMenuResId,
                        Toolbar.OnMenuItemClickListener { item -> screenFragment.onOptionsItemSelected(item) })
                }
            } ?: run {
                val actionMenuResId = destination.arguments[
                        context.getString(R.string.navigation_toolbar_action_menu)]?.defaultValue as? Int ?: -1
                if (actionMenuResId == -1) {
                    toolbarDelegate.clearToolbarActionMenu()
                } else {
                    toolbarDelegate.setToolbarActionMenu(
                        actionMenuResId,
                        Toolbar.OnMenuItemClickListener { item -> screenFragment.onOptionsItemSelected(item) })
                }
            }
        }
    }

    sealed class ToolbarVisibilityTransition {
        data class InstantTransition(val toState: ToolbarDelegate.ToolbarVisibilityState) : ToolbarVisibilityTransition()
        data class AnimateTransition(val toState: ToolbarDelegate.ToolbarVisibilityState, val animationDurationMs: Long) : ToolbarVisibilityTransition()
    }
}

