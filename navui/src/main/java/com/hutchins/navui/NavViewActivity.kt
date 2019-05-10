@file:Suppress("MemberVisibilityCanBePrivate")

package com.hutchins.navui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.hutchins.navui.viewdelegates.BottomNavViewDelegate
import com.hutchins.navui.viewdelegates.NavigationViewDelegate
import com.hutchins.navui.viewdelegates.NoNavViewDelegate
import com.hutchins.navui.viewdelegates.SideNavViewDelegate

abstract class NavViewConfig {
    /**
     * The navigation menu's resourceId. If this activity does not use a nevigation view, return 0.
     * This resource is used to inflate either the Nav Drawer menu or the Bottom Nav Menu.
     */
    abstract val navigationMenuResourceId: Int

    /**
     * The navigation graph's resourceId. This is used by the fragment manager to determine navigation.
     */
    abstract val navigationGraphResourceId: Int
}
abstract class NavViewActivity : com.hutchins.navcore.NavigationActivity() {
    private lateinit var navViewConfig: NavViewConfig
    protected lateinit var navigationViewDelegate: NavigationViewDelegate
    open val navigationType = NavViewType.SIDE

    abstract fun getLotusActivityConfig(): NavViewConfig

    enum class NavViewType {
        SIDE, BOTTOM, NONE
    }

    override fun getNavigationGraphResourceId(): Int {
        return navViewConfig.navigationGraphResourceId
    }

//    /**
//     * LotusPageFragment ONLY should reference this.
//     */
//    internal fun getToolbarDelegate(): ToolbarDelegate {
//        return navigationViewDelegate.toolbarDelegate
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // We must do this BEFORE onCreate, because super.onCreate will ask us for the navGraphResourceId,
        // which we won't know which type we want until we grab it from the Base.
        navViewConfig = getLotusActivityConfig()

        super.onCreate(savedInstanceState)
    }

    override fun onSupportNavigateUp(): Boolean {
        // This is a redundant call just in case something fails to close the keyboard
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)

        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        navigationViewDelegate.onBackPressed()
    }

    /**
     * Called during onCreate of BaseActivity.
     */
    override fun instantiateViewDelegate(): com.hutchins.navcore.ViewDelegate {
        // TODO: Make this a factory pattern so users of this library can implement their own Navigation Views.

        // First, check if the activity is going to use a navigation menu:
        val menuResourceId = navViewConfig.navigationMenuResourceId
        navigationViewDelegate = when (navigationType) {
            NavViewType.SIDE -> {
                SideNavViewDelegate(this, menuResourceId)
            }
            NavViewType.BOTTOM -> {
                BottomNavViewDelegate(this, navController, menuResourceId)
            }
            NavViewType.NONE -> {
                NoNavViewDelegate(this, navController)
            }
        }

        return navigationViewDelegate
    }
}
