package com.hutchins.navui.common

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.hutchins.navui.R
import com.hutchins.navui.core.BaseNavUIController
import com.hutchins.navui.core.BaseScreenFragment
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.core.NavigationViewDelegate
import com.hutchins.navui.databinding.ActivityNoNavBinding

class NoNavViewDelegate(navViewActivity: NavViewActivity) : NavigationViewDelegate, SampleNavUIController.TestNavViewDelegate, ToolbarDelegate.UpVisibilityHandler {
    companion object {
        const val BUNDLE_KEY_UP_STATE = "BUNDLE_KEY_UP_STATE"
    }

    override val navViewActivity = navViewActivity
    private lateinit var binding: ActivityNoNavBinding
    private lateinit var navController: NavController

    override val navHostResourceId: Int = R.id.navHost

    private var showUp: Boolean = false

    internal val toolbarDelegate: ToolbarDelegate by lazy {
        ToolbarDelegate(
            binding.constraintActivityContentLayout,
            binding.toolbarLayout.appbar,
            binding.toolbarLayout.toolbar,
            this, this
        )
    }

    val upDrawable: DrawerArrowDrawable by lazy {
        val arrow = DrawerArrowDrawable(navViewActivity)
        arrow
    }

    override fun getNavUiToolbarDelegate(): ToolbarDelegate {
        return toolbarDelegate
    }

    override fun getNavigationController(): NavController {
        return navController
    }

    override fun onCreateContentView(): View {
        binding = DataBindingUtil.setContentView(navViewActivity, R.layout.activity_no_nav)
        return binding.root
    }

    override fun setupNavViewWithNavController(navController: NavController) {
        this.navController = navController
        NavigationUI.setupWithNavController(binding.toolbarLayout.toolbar, navController)

        // To keep UI's similar, we are using drawable provided by Material Design Library that
        // animates an arrow to/from a hamburger icon. This view doesn't need the hamburger part
        // so we set it to be the arrow only, and never alter it.
        upDrawable.progress = ToolbarDelegate.PROGRESS_ARROW
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
        return SampleNavUIController(screenFragment)
    }

    private fun setNavigationIcon(icon: Drawable?) {
        if (icon == null) {
            binding.toolbarLayout.toolbar.setNavigationIcon(null)
        } else {
            binding.toolbarLayout.toolbar.setNavigationIcon(icon)
        }
    }

    override fun saveState(bundle: Bundle) {
        bundle.putBoolean(BUNDLE_KEY_UP_STATE, showUp)

        toolbarDelegate.saveState(bundle)
    }

    override fun restoreState(bundle: Bundle) {
        showUp = bundle.getBoolean(BUNDLE_KEY_UP_STATE)

        toolbarDelegate.restoreState(bundle)
    }
}