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

import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.hutchins.navui.core.PrimaryScreenFragment
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.core.NavViewDelegate


/**
 * Example implementation of a [BottomNavigationView] layout for an activity. The [BottomNavigationView] and [Toolbar]
 * belong to the activity layout, and each "screen" belongs to the BaseScreenFragment.
 *
 * This class can be used as-is as a [NavViewDelegate] for any activity.
 */
@Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")
open class JetpackBottomNavDelegate(
    override val navViewActivity: NavViewActivity,
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

    private lateinit var navController: NavController
    lateinit var constraintLayout: ConstraintLayout
    lateinit var toolbar: Toolbar
    lateinit var appBarLayout: AppBarLayout
    lateinit var bottomNavigationView: BottomNavigationView

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

    override fun setUpNavigationVisible(visible: Boolean) {
        this.showUp = visible
        if (visible) {
            setNavigationIcon(upDrawable)
        } else {
            setNavigationIcon(null)
        }
    }

    override fun setNavViewVisible(visible: Boolean) {
        this.navViewVisible = visible
        if (visible) {
            bottomNavigationView.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.GONE
        }
    }

    override fun newInstanceNavUiController(screenFragment: PrimaryScreenFragment): BaseNavUIController {
        return JetpackNavUIController(screenFragment as JetpackScreenFragment)
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