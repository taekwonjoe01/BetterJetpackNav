package com.hutchins.navui.viewdelegates

import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.hutchins.navui.R
import com.hutchins.navui.databinding.ActivityBottomNavBinding

/**
 * Created by jhutchins on 5/9/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class BottomNavViewDelegate(
    navigationActivity: com.hutchins.navcore.NavigationActivity,
    private val navController: NavController,
    private val navigationMenuResourceId: Int
        ) : NavigationViewDelegate(navigationActivity) {
    private lateinit var binding: ActivityBottomNavBinding

    override val navHostId: Int = R.id.navHost
    private var showUp: Boolean = false

    override fun setContentView() {
        binding = DataBindingUtil.setContentView(navigationActivity, R.layout.activity_bottom_nav)
        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(navigationMenuResourceId)
    }

    override fun initNavigation(navController: NavController) {
        // We are now overriding the default jetpack NavigatedListeners in order to achieve
        // customizable UP navigation.
        //NavigationUI.setupActionBarWithNavController(appCompatActivity, navController)

        navigationActivity.setSupportActionBar(binding.toolbarLayout.toolbar)

        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        // To keep UI's similar, we are using drawable provided by Material Design Library that
        // animates an arrow to/from a hamburger icon. This view doesn't need the hamburger part
        // so we set it to be the arrow only, and never alter it.
        upDrawable.progress = com.hutchins.navui.ToolbarDelegate.PROGRESS_ARROW
    }

    override fun onSupportNavigateUp(): Boolean {
        var handled = false
        if (navController.currentDestination!!.id == navController.graph.startDestination) {
            if (showUp) {
                handled = navigationActivity.maybeDoNavigateUpOverride()
                if (!handled) {
                    navigationActivity.finish()
                    handled = true
                }
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
        navigationActivity.onBackPressed()
    }

    override fun updateUpNavigation(showUp: Boolean) {
        this.showUp = showUp
        if (showUp) {
            setNavigationIcon(upDrawable)
        } else {
            setNavigationIcon(null)
        }
    }

    override fun updateNavViewVisibility(show: Boolean) {
        if (show) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    // TODO for dashboard https://engageft.atlassian.net/browse/SHOW-164
    override fun updateNavigationEnabled(enabled: Boolean) {
        binding.bottomNav.isEnabled = enabled
    }

    override fun getNavigationMenu(): Menu {
        return binding.bottomNav.menu
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbarLayout.toolbar
    }
}