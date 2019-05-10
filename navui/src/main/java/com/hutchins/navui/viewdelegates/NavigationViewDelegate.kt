package com.hutchins.navui.viewdelegates

import android.graphics.drawable.Drawable
import android.view.Menu
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar

abstract class NavigationViewDelegate(protected val navigationActivity: com.hutchins.navcore.NavigationActivity) : com.hutchins.navcore.ViewDelegate() {
    abstract fun onSupportNavigateUp(): Boolean
    abstract fun onBackPressed()

    abstract fun updateUpNavigation(showUp: Boolean)
    abstract fun updateNavViewVisibility(show: Boolean)
    abstract fun updateNavigationEnabled(enabled: Boolean)
    abstract fun getNavigationMenu(): Menu?

    abstract fun getToolbar(): Toolbar

    internal val toolbarDelegate: com.hutchins.navui.ToolbarDelegate by lazy {
        com.hutchins.navui.ToolbarDelegate(this, navigationActivity.supportActionBar!!)
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