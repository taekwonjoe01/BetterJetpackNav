package com.hutchins.navui.jetpack

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.View
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


/**
 * Example implementation of a [BottomNavigationView] layout for an activity. The [BottomNavigationView] and [Toolbar]
 * belong to the activity layout, and each "screen" belongs to the BaseScreenFragment.
 *
 * This class can be used as-is as a [NavViewDelegate] for any activity.
 */
open class JetpackBottomNavDelegate(
    navViewActivity: NavViewActivity,
    private val navigationMenuResourceId: Int
) : NavViewDelegate, JetpackNavUIController.TestNavViewDelegate, JetpackToolbarDelegate.UpVisibilityHandler {
    companion object {
        const val BUNDLE_KEY_UP_STATE = "BUNDLE_KEY_UP_STATE"
        const val BUNDLE_KEY_NAV_STATE= "BUNDLE_KEY_NAV_STATE"
    }

    /**
     * Set the res Id of the activity layout. This defaults to a prepackaged layout provided by this library.
     *
     * For usage by this [JetpackBottomNavDelegate], there must be a reference to
     * a [ConstraintLayout] that represents the view containing the [FrameLayout] (That the [NavHostFragment] will use) and
     * the [Toolbar]. This is to manage visibility state.
     *
     * There must also be a reference to the [AppBarLayout] that holds the [Toolbar].
     *
     * There must also be a reference to the [Toolbar].
     *
     * There must also be a reference to the [BottomNavigationView]
     */
    open val activityLayoutRedId: Int = R.layout.activity_bottom_nav

    /**
     * The reference to the [ConstraintLayout] required by this [NavViewDelegate].
     */
    open val constraintLayoutResId: Int = R.id.constraintActivityContentLayout

    /**
     * The reference to the [AppBarLayout] required by this [NavViewDelegate].
     */
    open val appBarLayoutResId: Int = R.id.toolbarLayout

    /**
     * The reference to the [Toolbar] required by this [NavViewDelegate].
     */
    open val toolbarResId: Int = R.id.toolbar

    /**
     * The reference to the [BottomNavigationView] required by this [NavViewDelegate].
     */
    open val bottomNavResId: Int = R.id.bottomNav

    override val navViewActivity = navViewActivity

    private lateinit var navController: NavController
    lateinit var constraintLayout: ConstraintLayout
    lateinit var toolbar: Toolbar
    lateinit var appBarLayout: AppBarLayout
    lateinit var bottomNavigationView: BottomNavigationView

    /**
     * The reference to the [FrameLayout] that will host the [NavHostFragment]. Required by the [NavigationActivity].
     */
    override val navHostResourceId: Int = R.id.navHost

    private var showUp: Boolean = false
    private var navViewVisible: Boolean = true

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
        bottomNavigationView = navViewActivity.findViewById(bottomNavResId)

        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(navigationMenuResourceId)
    }

    override fun setupNavViewWithNavController(navController: NavController) {
        this.navController = navController
        NavigationUI.setupWithNavController(toolbar, navController)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

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
        this.navViewVisible = show
        if (show) {
            bottomNavigationView.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.GONE
        }
    }

    override fun newInstanceNavUiController(screenFragment: BaseScreenFragment): BaseNavUIController {
        return JetpackNavUIController(screenFragment)
    }

    fun getNavigationMenu(): Menu {
        return bottomNavigationView.menu
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
        bundle.putBoolean(BUNDLE_KEY_NAV_STATE, navViewVisible)

        jetpackToolbarDelegate.saveState(bundle)
    }

    override fun restoreState(bundle: Bundle) {
        val navViewVisible = bundle.getBoolean(BUNDLE_KEY_NAV_STATE)
        val showUp = bundle.getBoolean(BUNDLE_KEY_UP_STATE)

        setUpNavigationVisible(showUp)
        setNavViewVisible(navViewVisible)

        jetpackToolbarDelegate.restoreState(bundle)
    }
}