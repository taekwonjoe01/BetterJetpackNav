package com.hutchins.navui.sampleviewdelegates

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.hutchins.navui.BaseNavUIController
import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.NavigationViewDelegate
import com.hutchins.navui.R
import com.hutchins.navui.databinding.ActivityDrawerNavBinding

/**
 * Created by jhutchins on 5/9/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class SideNavViewDelegate(private val navigationMenuResourceId: Int
        ) : NavigationViewDelegate, SampleNavUIController.TestNavViewDelegate {
    lateinit var navViewActivity: NavViewActivity
    lateinit var binding: ActivityDrawerNavBinding
    lateinit var navController: NavController

    private var valueAnimator: ValueAnimator? = null

    private var currentState = ToolbarDelegate.PROGRESS_HAMBURGER
    private var showUp: Boolean = false

    private var navigationEnabled = true

    override val navHostResourceId: Int = R.id.navHost

    internal val toolbarDelegate: ToolbarDelegate by lazy {
        ToolbarDelegate(
            binding.constraintActivityContentLayout,
            binding.toolbarLayout.appbar,
            binding.toolbarLayout.toolbar,
            this
        )
    }

    val upDrawable: DrawerArrowDrawable by lazy {
        val arrow = DrawerArrowDrawable(navViewActivity)
//        arrow.arrowHeadLength = resources.getDimension(R.dimen.toolbarUpArrowHeadLength)
//        arrow.arrowShaftLength = resources.getDimension(R.dimen.toolbarUpArrowShaftLength)
//        arrow.barThickness = resources.getDimension(R.dimen.toolbarUpArrowThickness)
//        arrow.barLength = resources.getDimension(R.dimen.toolbarHamburgerBarLength)
//        arrow.gapSize = resources.getDimension(R.dimen.toolbarHamburgerGapSize)
//        arrow.color = ContextCompat.getColor(this@LotusActivity, R.color.structure6)
        arrow
    }

    override fun getNavUiToolbarDelegate(): ToolbarDelegate {
        return toolbarDelegate
    }

    override fun getNavigationController(): NavController {
        return navController
    }

    override fun onCreateContentView(navViewActivity: NavViewActivity): View {
        this.navViewActivity = navViewActivity
        binding = DataBindingUtil.setContentView(navViewActivity,
            R.layout.activity_drawer_nav)
        binding.navigationView.menu.clear()
        binding.navigationView.inflateMenu(navigationMenuResourceId)

        return binding.root
    }

    override fun setupNavViewWithNavController(navController: NavController) {
        this.navController = navController

        NavigationUI.setupWithNavController(binding.toolbarLayout.toolbar, navController, binding.activityContainer)
        // Immediately override the navigation click listener. We do this because we want to provide overridable functionality
        // for the up button presses.
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            navViewActivity.onSupportNavigateUp()
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
                handled = navViewActivity.maybeDoNavigateUpOverride()
                if (!handled) {
                    navViewActivity.finish()
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
            handled = navViewActivity.maybeDoNavigateUpOverride()
            if (!handled) {
                handled = navController.navigateUp()
            }
        }
        return handled
    }

    override fun onBackPressed(): Boolean {
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

    override fun newInstanceNavUiController(): BaseNavUIController {
        return SampleNavUIController(context = navViewActivity)
    }

    private fun setNavigationIcon(icon: Drawable?) {
        if (icon == null) {
            binding.toolbarLayout.toolbar.setNavigationIcon(null)
        } else {
            binding.toolbarLayout.toolbar.setNavigationIcon(icon)
        }
    }

    private fun updateNavigationEnabled(enabled: Boolean) {
        navigationEnabled = enabled
        if (enabled) {
            binding.activityContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.activityContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }
}