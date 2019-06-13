package com.hutchins.navui.jetpack

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hutchins.navui.R
import com.hutchins.navui.core.BaseNavUIController
import com.hutchins.navui.core.BaseScreenFragment
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.core.NavViewDelegate

open class JetpackNoNavDelegate(navViewActivity: NavViewActivity) : NavViewDelegate, JetpackNavUIController.TestNavViewDelegate, JetpackToolbarDelegate.UpVisibilityHandler {
    companion object {
        const val BUNDLE_KEY_UP_STATE = "BUNDLE_KEY_UP_STATE"
    }

    open val activityLayoutRedId: Int = R.layout.activity_no_nav
    open val constraintLayoutResId: Int = R.id.constraintActivityContentLayout
    open val appBarLayoutResId: Int = R.id.toolbarLayout
    open val toolbarResId: Int = R.id.toolbar

    override val navViewActivity = navViewActivity

    private lateinit var navController: NavController
    lateinit var constraintLayout: ConstraintLayout
    lateinit var toolbar: Toolbar
    lateinit var appBarLayout: AppBarLayout
    lateinit var bottomNavigationView: BottomNavigationView

    override val navHostResourceId: Int = R.id.navHost

    private var showUp: Boolean = false

    internal val jetpackToolbarDelegate: JetpackToolbarDelegate by lazy {
        JetpackToolbarDelegate(
            constraintLayout,
            appBarLayout,
            toolbar,
            this, this
        )
    }

    val upDrawable: DrawerArrowDrawable by lazy {
        val arrow = DrawerArrowDrawable(navViewActivity)
        arrow
    }

    override fun getNavUiToolbarDelegate(): JetpackToolbarDelegate {
        return jetpackToolbarDelegate
    }

    override fun getNavigationController(): NavController {
        return navController
    }

    override fun setContentView() {
        navViewActivity.setContentView(activityLayoutRedId)
        constraintLayout = navViewActivity.findViewById(constraintLayoutResId)
        toolbar = navViewActivity.findViewById(toolbarResId)
        appBarLayout = navViewActivity.findViewById(appBarLayoutResId)
    }

    override fun setupNavViewWithNavController(navController: NavController) {
        this.navController = navController
        NavigationUI.setupWithNavController(toolbar, navController)

        // To keep UI's similar, we are using drawable provided by Material Design Library that
        // animates an arrow to/from a hamburger icon. This view doesn't need the hamburger part
        // so we set it to be the arrow only, and never alter it.
        upDrawable.progress = JetpackToolbarDelegate.PROGRESS_ARROW
    }

    override fun onSupportNavigateUp(): Boolean {
        var handled = false
        if (navController.currentDestination!!.id == navController.graph.startDestination) {
            if (showUp) {
                handled = navViewActivity.maybeDoNavigateUpOverride()
                if (!handled) {
                    navViewActivity.finish()
                    handled = true
                }
            }
        } else {
            handled = navViewActivity.maybeDoNavigateUpOverride()
            if (!handled) {
                handled = navController.navigateUp()
            }
        }
        return handled
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun setUpNavigationVisible(showUp: Boolean) {
        this.showUp = showUp
        if (showUp) {
            setNavigationIcon(upDrawable)
        } else {
            setNavigationIcon(null)
        }
    }

    override fun setNavViewVisible(show: Boolean) {
        // Do nothing because there is no navigation view.
    }

    override fun newInstanceNavUiController(screenFragment: BaseScreenFragment): BaseNavUIController {
        return JetpackNavUIController(screenFragment)
    }

    private fun setNavigationIcon(icon: Drawable?) {
        if (icon == null) {
            toolbar.navigationIcon = null
        } else {
            toolbar.navigationIcon = icon
        }
    }

    override fun saveState(bundle: Bundle) {
        bundle.putBoolean(BUNDLE_KEY_UP_STATE, showUp)

        jetpackToolbarDelegate.saveState(bundle)
    }

    override fun restoreState(bundle: Bundle) {
        showUp = bundle.getBoolean(BUNDLE_KEY_UP_STATE)

        jetpackToolbarDelegate.restoreState(bundle)
    }
}