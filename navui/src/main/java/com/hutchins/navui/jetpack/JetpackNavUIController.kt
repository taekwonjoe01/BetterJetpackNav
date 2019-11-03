/*******************************************************************************
 * Copyright 2019 Joseph Hutchins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.hutchins.navui.jetpack

import android.content.Context
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.hutchins.navui.R
import com.hutchins.navui.core.BaseNavUIController
import com.hutchins.navui.core.BaseScreenFragment
import com.hutchins.navui.core.NavUISetting
import com.hutchins.navui.core.NavViewDelegate

/**
 * Implementation of a [BaseNavUIController] that is used by the [JetpackBottomNavDelegate], [JetpackNoNavDelegate], and
 * [JetpackSideNavDelegate]. This provides and API and state management for [BaseScreenFragment]'s to provide a configuration
 * of the [NavViewActivity]'s view state when their screens are active in the Navigation lifecycle.
 *
 * This class maintains each [BaseScreenFragment]'s state view a [ViewModel] and works with respect to screen rotations.
 */
@Suppress("unused")
class JetpackNavUIController(private val screenFragment: JetpackScreenFragment) : BaseNavUIController(screenFragment) {
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

    /**
     * @param jetpackToolbarVisibility Immediately set the visibility of the toolbar.
     *
     * If this controller is not currently active, the action will be queued to run immediately
     * when it is made active. If a subsequent call to this method is made before the controller
     * becomes active, it will override any previous calls.
     *
     * This state will be maintained for as long as the [BaseScreenFragment] that holds this controller is alive.
     *
     * @param jetpackToolbarVisibility The visibility to set this toolbar, or null if you want to fallback
     *          to the navigation graph arguments.
     */
    fun setToolbarVisibility(jetpackToolbarVisibility: JetpackToolbarDelegate.ToolbarVisibilityState?) {
        jetpackToolbarVisibility?.let {
            toolbarVisibilitySetting.setting = ToolbarVisibilityTransition.InstantTransition(it)
        } ?: kotlin.run {
            toolbarVisibilitySetting.setting = null
        }
    }

    /**
     * @param title Immediately set the title of the toolbar.
     *
     * If this controller is not currently
     * active, the action will be queued to run immediately when it is made active. If a subsequent call to this method is
     * made before the controller becomes active, it will override any previous calls.
     *
     * This state will be maintained for as long as the [BaseScreenFragment] that holds this controller is alive.
     *
     * @param title The title to set this toolbar, or null if you want to fallback
     *          to the navigation graph arguments.
     */
    fun setToolbarTitle(title: String?) {
        titleSetting.setting = title
    }

    /**
     * @param subtitle Immediately set the subtitle of the toolbar.
     *
     * If this controller is not currently
     * active, the action will be queued to run immediately when it is made active. If a subsequent call to this method is
     * made before the controller becomes active, it will override any previous calls.
     *
     * This state will be maintained for as long as the [BaseScreenFragment] that holds this controller is alive.
     *
     * @param subtitle The subtitle to set this toolbar, or null if you want to fallback
     *          to the navigation graph arguments.
     */
    fun setToolbarSubtitle(subtitle: String?) {
        subTitleSetting.setting = subtitle
    }

    /**
     * @param overrideUp Immediately set the overrideUp state of the toolbar.
     *
     * If this controller is not currently
     * active, the action will be queued to run immediately when it is made active. If a subsequent call to this method is
     * made before the controller becomes active, it will override any previous calls.
     *
     * This state will be maintained for as long as the [BaseScreenFragment] that holds this controller is alive.
     *
     * @param overrideUp Whether or not to override the default up behavior of the nav graph. Use null to
     *          fall back to the nav graph argument for this destination.
     */
    fun setToolbarOverrideUp(overrideUp: Boolean?) {
        overrideUpSetting.setting = overrideUp
    }

    /**
     * @param showNavView Immediately set the showNavView state of the toolbar.
     *
     * If this controller is not currently
     * active, the action will be queued to run immediately when it is made active. If a subsequent call to this method is
     * made before the controller becomes active, it will override any previous calls.
     *
     * This state will be maintained for as long as the [BaseScreenFragment] that holds this controller is alive.
     *
     * @param showNavView Whether or not to show the nav view. Use null to
     *          fall back to the nav graph argument for this destination.
     */
    fun setNavViewVisibility(showNavView: Boolean?) {
        navVisibilitySetting.setting = showNavView
    }

    /**
     * @param toVisibilityState Animate set the visibility of the toolbar.
     * @param animationDurationMs Duration for the animation to run.
     *
     * If this controller is not currently
     * active, the action will be queued to run immediately when it is made active. If a subsequent call to this method is
     * made before the controller becomes active, it will override any previous calls.
     *
     * This state will be maintained for as long as the [BaseScreenFragment] that holds this controller is alive.
     */
    fun animateToolbarVisibility(toVisibilityState: JetpackToolbarDelegate.ToolbarVisibilityState, animationDurationMs: Long) {
        toolbarVisibilitySetting.setting = ToolbarVisibilityTransition.AnimateTransition(toVisibilityState, animationDurationMs)
    }

    /**
     * @param actionMenuResId Immediately set the actionMenuResId of the toolbar. This will inflate a menu that is visible
     * for this screen's toolbar.
     *
     * If this controller is not currently
     * active, the action will be queued to run immediately when it is made active. If a subsequent call to this method is
     * made before the controller becomes active, it will override any previous calls.
     *
     * This state will be maintained for as long as the [BaseScreenFragment] that holds this controller is alive.
     *
     * @param actionMenuResId  Use null to
     *          fall back to the nav graph argument for this destination.
     */
    fun setToolbarActionMenu(actionMenuResId: Int?) {
        actionMenuSetting.setting = actionMenuResId
    }

    private fun NavViewDelegate.asTestNavViewDelegate() : TestNavViewDelegate {
        return this as TestNavViewDelegate
    }

    interface TestNavViewDelegate {
        fun getNavigationController(): NavController
        fun getNavUiToolbarDelegate(): JetpackToolbarDelegate
        fun setNavViewVisible(visible: Boolean)
    }

    inner class ToolbarTitleSetting : NavUISetting<String>() {
        override fun applySetting(
            destination: NavDestination,
            navViewDelegate: NavViewDelegate,
            isNavigationTransition: Boolean,
            setting: String?
        ) {
            val toolbarDelegate = navViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
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
            navViewDelegate: NavViewDelegate,
            isNavigationTransition: Boolean,
            setting: String?
        ) {
            val toolbarDelegate = navViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
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
            navViewDelegate: NavViewDelegate,
            isNavigationTransition: Boolean,
            setting: Boolean?
        ) {
            setting?.let {
                setUpNavigationVisible(destination, navViewDelegate, it)
            } ?: run {
                setUpNavigationVisible(destination, navViewDelegate, destination.arguments[context.getString(R.string.navigation_override_up)]?.defaultValue as? Boolean ?: false)
            }
        }

        private fun setUpNavigationVisible(destination: NavDestination, navViewDelegate: NavViewDelegate, overrideUp: Boolean) {
            val toolbarDelegate = navViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            val isStartDestination = (navViewDelegate.asTestNavViewDelegate().getNavigationController().graph.startDestination == destination.id)
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
            navViewDelegate: NavViewDelegate,
            isNavigationTransition: Boolean,
            setting: ToolbarVisibilityTransition?
        ) {
            val toolbarDelegate = navViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
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
                    TOOLBAR_VISIBILITY_GONE -> JetpackToolbarDelegate.ToolbarVisibilityState.GONE
                    TOOLBAR_VISIBILITY_VISIBLE -> JetpackToolbarDelegate.ToolbarVisibilityState.VISIBLE
                    TOOLBAR_VISIBILITY_INVISIBLE -> JetpackToolbarDelegate.ToolbarVisibilityState.INVISIBLE
                    else -> JetpackToolbarDelegate.ToolbarVisibilityState.VISIBLE
                }
                toolbarDelegate.setToolbarVisibilityState(desiredState)
            }
        }
    }

    inner class NavViewVisibilitySetting : NavUISetting<Boolean>() {
        override fun applySetting(
            destination: NavDestination,
            navViewDelegate: NavViewDelegate,
            isNavigationTransition: Boolean,
            setting: Boolean?
        ) {
            setting?.let {
                val showNavView = it
                navViewDelegate.asTestNavViewDelegate().setNavViewVisible(showNavView)
            } ?: run {
                val showNavView = !(destination.arguments[context.getString(R.string.navigation_view_gone)]?.defaultValue as? Boolean ?: false)
                navViewDelegate.asTestNavViewDelegate().setNavViewVisible(showNavView)
            }
        }
    }

    inner class ActionMenuSetting : NavUISetting<Int>() {
        override fun applySetting(
            destination: NavDestination,
            navViewDelegate: NavViewDelegate,
            isNavigationTransition: Boolean,
            setting: Int?
        ) {
            val toolbarDelegate = navViewDelegate.asTestNavViewDelegate().getNavUiToolbarDelegate()
            setting?.let {
                val actionMenuResId = it
                if (actionMenuResId == -1) {
                    toolbarDelegate.clearToolbarActionMenu()
                } else {
                    val menu = toolbarDelegate.setToolbarActionMenu(
                        actionMenuResId,
                        Toolbar.OnMenuItemClickListener { item -> screenFragment.onActionItemSelected(item) })
                    screenFragment.onActionMenuCreated(menu)
                }
            } ?: run {
                val actionMenuResId = destination.arguments[
                        context.getString(R.string.navigation_toolbar_action_menu)]?.defaultValue as? Int ?: -1
                if (actionMenuResId == -1) {
                    toolbarDelegate.clearToolbarActionMenu()
                } else {
                    val menu = toolbarDelegate.setToolbarActionMenu(
                        actionMenuResId,
                        Toolbar.OnMenuItemClickListener { item -> screenFragment.onActionItemSelected(item) })
                    screenFragment.onActionMenuCreated(menu)
                }
            }
        }
    }

    sealed class ToolbarVisibilityTransition {
        data class InstantTransition(val toState: JetpackToolbarDelegate.ToolbarVisibilityState) : ToolbarVisibilityTransition()
        data class AnimateTransition(val toState: JetpackToolbarDelegate.ToolbarVisibilityState, val animationDurationMs: Long) : ToolbarVisibilityTransition()
    }
}

