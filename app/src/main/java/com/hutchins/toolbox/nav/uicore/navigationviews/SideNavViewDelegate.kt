package com.hutchins.toolbox.nav.uicore.navigationviews

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.view.Menu
import androidx.annotation.StyleRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.hutchins.toolbox.R
import com.hutchins.toolbox.databinding.ActivityDrawerNavBinding
import com.hutchins.toolbox.nav.core.NavigationActivity
import com.hutchins.toolbox.nav.uicore.ToolbarDelegate

/**
 * Created by jhutchins on 5/9/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class SideNavViewDelegate(navigationActivity: NavigationActivity,
                          private val navigationMenuResourceId: Int
        ) : NavigationViewDelegate(navigationActivity) {
    private lateinit var binding: ActivityDrawerNavBinding
    private lateinit var navController: NavController

    private var valueAnimator: ValueAnimator? = null

    private var currentState = ToolbarDelegate.PROGRESS_HAMBURGER
    private var showUp: Boolean = false

    private var navigationEnabled = true

    override val navHostId: Int = R.id.navHost

    override fun setContentView() {
        binding = DataBindingUtil.setContentView(navigationActivity,
            R.layout.activity_drawer_nav)
        binding.navigationView.menu.clear()
        binding.navigationView.inflateMenu(navigationMenuResourceId)

        binding.navigationView.addHeaderView(navigationActivity.layoutInflater.inflate(R.layout.layout_nav_header, null, false))
//        setSideNavigationStyle(
//            ContextCompat.getColor(navigationActivity, R.color.primary),
//            ContextCompat.getColorStateList(navigationActivity, R.color.side_navigation_text)!!,
//            resources.getDimension(R.dimen.sideNavigationHorizontalPadding).toInt(), R.style.Body)
    }

    override fun initNavigation(navController: NavController) {
        this.navController = navController

        // We are now overriding the default jetpack NavigatedListeners in order to achieve
        // customizable UP navigation.
        //NavigationUI.setupActionBarWithNavController(appCompatActivity, navController, binding.drawerLayout)

        // Set up ActionBar
        navigationActivity.setSupportActionBar(binding.toolbarLayout.toolbar)

        // Set up navigation menu
        NavigationUI.setupWithNavController(binding.navigationView, navController)

        // Initialize the icon.
        upDrawable.progress = ToolbarDelegate.PROGRESS_HAMBURGER
        navigationActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val delegate = navigationActivity.drawerToggleDelegate
        delegate!!.setActionBarUpIndicator(upDrawable, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        /*return NavigationUI.navigateUp(binding.activityContainer,
                navController)*/
        // Must override default:
        var handled = false
        if (navController.currentDestination!!.id == navController.graph.startDestination) {
            if (showUp) {
                handled = navigationActivity.maybeDoNavigateUpOverride()
                if (!handled) {
                    navigationActivity.finish()
                    handled = true
                }
            } else {
                if (navigationEnabled) {
                    binding.activityContainer.openDrawer(GravityCompat.START)
                    handled = true
                }
            }
        } else if (!showUp) {
            if (navigationEnabled) {
                binding.activityContainer.openDrawer(GravityCompat.START)
                handled = true
            }
        } else {
            handled = navigationActivity.maybeDoNavigateUpOverride()
            if (!handled) {
                handled = navController.navigateUp()
            }
        }
        return handled
    }

    override fun onBackPressed() {
        if (binding.activityContainer.isDrawerOpen(GravityCompat.START)) {
            binding.activityContainer.closeDrawer(GravityCompat.START)
        } else {
            if (!navigationActivity.maybeDoBackButtonOverride()) {
                navigationActivity.onBackPressed()
            }
        }
    }

    override fun updateUpNavigation(showUp: Boolean) {
        this.showUp = showUp
    }

    protected fun setActionBarUpIndicator(showDrawer: Boolean, shouldAnimate: Boolean = true) {
        val desiredState = if (showDrawer) ToolbarDelegate.PROGRESS_HAMBURGER else ToolbarDelegate.PROGRESS_ARROW
        val stateChanged = (desiredState != currentState)

        //setNavigationIcon(upDrawable)
        val endValue = if (showDrawer) ToolbarDelegate.PROGRESS_HAMBURGER else ToolbarDelegate.PROGRESS_ARROW
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

    override fun updateNavViewVisibility(show: Boolean) {
        if (show) {
            setNavigationIcon(upDrawable)
            setActionBarUpIndicator(!showUp)
        } else {
            if (!showUp) {
                setNavigationIcon(null)
            } else {
                setNavigationIcon(upDrawable)
                setActionBarUpIndicator(!showUp, false)
            }
        }

        updateNavigationEnabled(show)
    }

    // TODO for dashboard https://engageft.atlassian.net/browse/SHOW-164
    override fun updateNavigationEnabled(enabled: Boolean) {
        navigationEnabled = enabled
        if (enabled) {
            binding.activityContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.activityContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    fun setSideNavigationStyle(color: Int, textColorStateList: ColorStateList, itemHorizontalPadding: Int,
                               @StyleRes textAppearance: Int, showNavHeader: Boolean = false) {
        // TODO(jhutchins): Some other alterable things if needed?
//            binding.navigationView.itemBackground
//            binding.navigationView.itemIconPadding
//            binding.navigationView.itemBackground
//            binding.navigationView.itemIconTintList
        binding.navigationView.itemHorizontalPadding = itemHorizontalPadding
        binding.navigationView.setItemTextAppearance(textAppearance)
        binding.navigationView.itemTextColor = textColorStateList
        binding.navigationView.setBackgroundColor(color)
        if (!showNavHeader) {
            binding.navigationView.removeHeaderView(binding.navigationView.getHeaderView(0))
        }
    }

    override fun getNavigationMenu(): Menu? {
        return binding.navigationView.menu
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbarLayout.toolbar
    }
}