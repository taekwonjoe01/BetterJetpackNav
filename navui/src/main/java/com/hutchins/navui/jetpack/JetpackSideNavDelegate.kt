package com.hutchins.navui.jetpack

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.hutchins.navui.R
import com.hutchins.navui.core.BaseNavUIController
import com.hutchins.navui.core.BaseScreenFragment
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.core.NavViewDelegate

open class JetpackSideNavDelegate(
    override val navViewActivity: NavViewActivity, private val navigationMenuResourceId: Int
        ) : NavViewDelegate, JetpackNavUIController.TestNavViewDelegate, JetpackToolbarDelegate.UpVisibilityHandler{
    companion object {
         const val BUNDLE_KEY_TOOLBAR_STATE = "BUNDLE_KEY_TOOLBAR_STATE"
         const val BUNDLE_KEY_UP_STATE = "BUNDLE_KEY_UP_STATE"
         const val BUNDLE_KEY_NAV_STATE= "BUNDLE_KEY_NAV_STATE"
    }

    /**
     * Set the res Id of the activity layout. This defaults to a prepackaged layout provided by this library.
     *
     * For usage by this [JetpackSideNavDelegate], there must be a reference to
     * a [ConstraintLayout] that represents the view containing the [FrameLayout] (That the [NavHostFragment] will use) and
     * the [Toolbar]. This is to manage visibility state.
     *
     * There must also be a reference to the [AppBarLayout] that holds the [Toolbar].
     *
     * There must also be a reference to the [Toolbar].
     *
     * There must also be a reference to the [BottomNavigationView].
     *
     * There must also be a reference to the [DrawerLayout].
     *
     * There must also be a reference to the [NavigationView]
     */
    open val activityLayoutRedId: Int = R.layout.activity_drawer_nav

    /**
     * The reference to the [ConstraintLayout] required by this [NavViewDelegate].
     */
    open val constraintLayoutResId: Int = R.id.constraintActivityContentLayout

    /**
     * The reference to the [DrawerLayout] required by this [NavViewDelegate].
     */
    open val drawerLayoutResId: Int = R.id.activityContainer

    /**
     * The reference to the [AppBarLayout] required by this [NavViewDelegate].
     */
    open val appBarLayoutResId: Int = R.id.toolbarLayout

    /**
     * The reference to the [Toolbar] required by this [NavViewDelegate].
     */
    open val toolbarResId: Int = R.id.toolbar

    /**
     * The reference to the [NavigationView] required by this [NavViewDelegate].
     */
    open val navigationViewResId: Int = R.id.navigationView

    private lateinit var navController: NavController
    lateinit var constraintLayout: ConstraintLayout
    lateinit var toolbar: Toolbar
    lateinit var appBarLayout: AppBarLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    private var valueAnimator: ValueAnimator? = null

    private var currentState = JetpackToolbarDelegate.PROGRESS_HAMBURGER
    private var showUp: Boolean = false

    private var navigationEnabled = true

    /**
     * The reference to the [FrameLayout] that will host the [NavHostFragment]. Required by the [NavigationActivity].
     */
    override val navHostResourceId: Int = R.id.navHost

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
        drawerLayout = navViewActivity.findViewById(drawerLayoutResId)
        navigationView = navViewActivity.findViewById(navigationViewResId)

        navigationView.menu.clear()
        navigationView.inflateMenu(navigationMenuResourceId)
    }

    override fun setupNavViewWithNavController(navController: NavController) {
        this.navController = navController

        NavigationUI.setupWithNavController(toolbar, navController, drawerLayout)
        NavigationUI.setupWithNavController(navigationView, navController)
        // Immediately override the navigation click listener. We do this because we want to provide overridable functionality
        // for the up button presses.
        toolbar.setNavigationOnClickListener {
            navViewActivity.onSupportNavigateUp()
        }

        // Initialize the icon.
        upDrawable.progress = JetpackToolbarDelegate.PROGRESS_HAMBURGER
        toolbar.navigationIcon = upDrawable
    }

    override fun onSupportNavigateUp(): Boolean {
        // Must override default so we can have custom press functionality:
        var handled = false
        if (navController.currentDestination!!.id == navController.graph.startDestination) {
            if (showUp) {
                handled = navViewActivity.maybeDoNavigateUpOverride()
                if (!handled) {
                    navViewActivity.finish()
                    handled = true
                }
            } else {
                if (navigationEnabled) {
                    drawerLayout.openDrawer(GravityCompat.START)
                    handled = true
                }
            }
        } else if (!showUp) {
            if (navigationEnabled) {
                drawerLayout.openDrawer(GravityCompat.START)
                handled = true
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
        return if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        } else {
            false
        }
    }

    override fun setUpNavigationVisible(showUp: Boolean) {
        this.showUp = showUp
        setNavViewVisible(this.navigationEnabled)
    }

    private  fun setToolbarUpIndicator(showDrawer: Boolean, shouldAnimate: Boolean = true) {
        val desiredState = if (showDrawer) JetpackToolbarDelegate.PROGRESS_HAMBURGER else JetpackToolbarDelegate.PROGRESS_ARROW
        val stateChanged = (desiredState != currentState)

        val endValue = if (showDrawer) JetpackToolbarDelegate.PROGRESS_HAMBURGER else JetpackToolbarDelegate.PROGRESS_ARROW
        if (stateChanged) {
            if (shouldAnimate) {
                val startValue = upDrawable.progress

                var animator = valueAnimator
                animator?.cancel()

                animator = ObjectAnimator.ofFloat(upDrawable, "progress", startValue, endValue)
                animator?.start()

                valueAnimator = animator
            } else {
                upDrawable.progress = desiredState
            }

            currentState = desiredState
        }
    }

    override fun setNavViewVisible(show: Boolean) {
        if (show) {
            setNavigationIcon(upDrawable)
            setToolbarUpIndicator(!showUp)
        } else {
            if (!showUp) {
                setNavigationIcon(null)
            } else {
                setNavigationIcon(upDrawable)
                setToolbarUpIndicator(!showUp, false)
            }
        }

        updateNavigationEnabled(show)
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

    private fun updateNavigationEnabled(enabled: Boolean) {
        navigationEnabled = enabled
        if (enabled) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun saveState(bundle: Bundle) {
        bundle.putBoolean(BUNDLE_KEY_TOOLBAR_STATE, currentState == JetpackToolbarDelegate.PROGRESS_HAMBURGER)
        bundle.putBoolean(BUNDLE_KEY_UP_STATE, showUp)
        bundle.putBoolean(BUNDLE_KEY_NAV_STATE, navigationEnabled)

        jetpackToolbarDelegate.saveState(bundle)
    }

    override fun restoreState(bundle: Bundle) {
        navigationEnabled = bundle.getBoolean(BUNDLE_KEY_NAV_STATE)
        showUp = bundle.getBoolean(BUNDLE_KEY_UP_STATE)
        val toolbarState = bundle.getBoolean(BUNDLE_KEY_TOOLBAR_STATE)

        setUpNavigationVisible(showUp)
        setNavViewVisible(navigationEnabled)
        setToolbarUpIndicator(toolbarState, false)

        jetpackToolbarDelegate.restoreState(bundle)
    }
}