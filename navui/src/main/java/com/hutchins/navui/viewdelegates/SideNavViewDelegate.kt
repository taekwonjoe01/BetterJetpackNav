package com.hutchins.navui.viewdelegates

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.annotation.StyleRes
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.R
import com.hutchins.navui.ToolbarDelegate
import com.hutchins.navui.databinding.ActivityDrawerNavBinding

/**
 * Created by jhutchins on 5/9/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class SideNavViewDelegate(navViewActivity: NavViewActivity,
                          private val navigationMenuResourceId: Int
        ) : NavigationViewDelegate(navViewActivity) {
    lateinit var binding: ActivityDrawerNavBinding
    private lateinit var navController: NavController

    private var valueAnimator: ValueAnimator? = null

    private var currentState = ToolbarDelegate.PROGRESS_HAMBURGER
    private var showUp: Boolean = false

    private var navigationEnabled = true

    override val navHostResourceId: Int = R.id.navHost

    override fun onCreateContentView(): View {
        binding = DataBindingUtil.setContentView(navigationActivity,
            R.layout.activity_drawer_nav)
        binding.navigationView.menu.clear()
        binding.navigationView.inflateMenu(navigationMenuResourceId)

        binding.navigationView.addHeaderView(navigationActivity.layoutInflater.inflate(R.layout.layout_nav_header, null, false))
//        setSideNavigationStyle(
//            ContextCompat.getColor(navigationActivity, R.color.primary),
//            ContextCompat.getColorStateList(navigationActivity, R.color.side_navigation_text)!!,
//            resources.getDimension(R.dimen.sideNavigationHorizontalPadding).toInt(), R.style.Body)
        return binding.root
    }

    override fun setupNavViewWithNavController(navController: NavController) {
        this.navController = navController

        NavigationUI.setupWithNavController(binding.toolbarLayout.toolbar, navController, binding.activityContainer)
        // Immediately override the navigation click listener. We do this because we want to provide overridable functionality
        // for the up button presses.
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            navigationActivity.onSupportNavigateUp()
        }

        // Initialize the icon.
        upDrawable.progress = ToolbarDelegate.PROGRESS_HAMBURGER
        binding.toolbarLayout.toolbar.navigationIcon = upDrawable
    }

    override fun onSupportNavigateUp(): Boolean {
        /*return NavigationUI.navigateUp(binding.activityContainer,
                navController)*/
        // Must override default so we can have custom press functionality:
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

    override fun onBackPressed(): Boolean {
        Log.e("Joey", "onBackPressed navEnabled? $navigationEnabled showUp? $showUp")
        return if (binding.activityContainer.isDrawerOpen(GravityCompat.START)) {
            binding.activityContainer.closeDrawer(GravityCompat.START)
            true
        } else {
            false
        }
    }

    override fun setUpNavigationVisible(showUp: Boolean) {
        this.showUp = showUp
        setNavViewVisible(this.navigationEnabled)
    }

    protected fun setToolbarUpIndicator(showDrawer: Boolean, shouldAnimate: Boolean = true) {
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

    private fun updateNavigationEnabled(enabled: Boolean) {
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

    fun getNavigationMenu(): Menu? {
        return binding.navigationView.menu
    }

    override val toolbar: Toolbar
        get() = binding.toolbarLayout.toolbar

    override val contentConstraintLayout: ConstraintLayout
        get() = binding.constraintActivityContentLayout

    override val toolbarLayout: View
        get() = binding.toolbarLayout.appbar
}