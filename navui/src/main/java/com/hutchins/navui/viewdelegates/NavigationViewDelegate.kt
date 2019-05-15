package com.hutchins.navui.viewdelegates

import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.ToolbarDelegate

abstract class NavigationViewDelegate(protected val navigationActivity: NavViewActivity) {
    /**
     * The library requires knowing what the root view that will hold the NavHostFragment will be.
     */
    abstract val navHostResourceId: Int

    /**
     * Using the delegate pattern, we let this object inflate and create a view that will be the activity's content view.
     */
    abstract fun onCreateContentView(): View

    /**
     * After the navController is instantiated and setup with the navHostFragment, allow the delegate to connect the controller
     * with any view setup it needs.
     */
    abstract fun setupNavViewWithNavController(navController: NavController)

    /**
     * Handle supportNavigateUp calls that the activity gets.
     */
    abstract fun onSupportNavigateUp(): Boolean

    /**
     * Handle back button calls that the activity gets.
     */
    abstract fun onBackPressed(): Boolean

    abstract fun updateUpNavigation(showUp: Boolean)
    abstract fun updateNavViewVisibility(show: Boolean)
    abstract fun updateNavigationEnabled(enabled: Boolean)
    abstract fun getNavigationMenu(): Menu?

    abstract fun getToolbar(): Toolbar

    internal val toolbarDelegate: ToolbarDelegate by lazy {
        ToolbarDelegate(getToolbar(), this, navigationActivity.supportActionBar!!)
    }

    protected val upDrawable: DrawerArrowDrawable by lazy {
        val arrow = DrawerArrowDrawable(navigationActivity)
//        arrow.arrowHeadLength = resources.getDimension(R.dimen.toolbarUpArrowHeadLength)
//        arrow.arrowShaftLength = resources.getDimension(R.dimen.toolbarUpArrowShaftLength)
//        arrow.barThickness = resources.getDimension(R.dimen.toolbarUpArrowThickness)
//        arrow.barLength = resources.getDimension(R.dimen.toolbarHamburgerBarLength)
//        arrow.gapSize = resources.getDimension(R.dimen.toolbarHamburgerGapSize)
//        arrow.color = ContextCompat.getColor(this@LotusActivity, R.color.structure6)
        arrow
    }

    protected fun setNavigationIcon(icon: Drawable?) {
        val actionBar = navigationActivity.supportActionBar!!
        if (icon == null) {
            actionBar.setDisplayHomeAsUpEnabled(false)
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true)
            val delegate = navigationActivity.drawerToggleDelegate
            delegate!!.setActionBarUpIndicator(icon, 0)
        }
    }
}