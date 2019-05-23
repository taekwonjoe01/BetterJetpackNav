package com.hutchins.navui.sampleviewdelegates

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.hutchins.navui.*
import com.hutchins.navui.databinding.ActivityBottomNavBinding

class BottomNavViewDelegate(navViewActivity: NavViewActivity, private val navigationMenuResourceId: Int) : NavigationViewDelegate, SampleNavUIController.TestNavViewDelegate {
    companion object {
        const val BUNDLE_KEY_TOOLBAR_STATE = "BUNDLE_KEY_TOOLBAR_STATE"
        const val BUNDLE_KEY_UP_STATE = "BUNDLE_KEY_UP_STATE"
        const val BUNDLE_KEY_NAV_STATE= "BUNDLE_KEY_NAV_STATE"
    }

    override val navViewActivity = navViewActivity
    private lateinit var binding: ActivityBottomNavBinding
    private lateinit var navController: NavController

    override val navHostResourceId: Int = R.id.navHost
    private var showUp: Boolean = false
    private var navViewVisible: Boolean = true

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

    override fun onCreateContentView(): View {
        binding = DataBindingUtil.setContentView(navViewActivity, R.layout.activity_bottom_nav)
        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(navigationMenuResourceId)
        return binding.root
    }

    override fun setupNavViewWithNavController(navController: NavController) {
        this.navController = navController
        // We are now overriding the default jetpack NavigatedListeners in order to achieve
        // customizable UP navigation.
        //NavigationUI.setupActionBarWithNavController(appCompatActivity, navController)

        navViewActivity.setSupportActionBar(binding.toolbarLayout.toolbar)

        NavigationUI.setupWithNavController(binding.bottomNav, navController)

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
        this.navViewVisible = show
        if (show) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    override fun newInstanceNavUiController(screenFragment: BaseScreenFragment): BaseNavUIController {
        return SampleNavUIController(screenFragment)
    }

    fun getNavigationMenu(): Menu {
        return binding.bottomNav.menu
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
        bundle.putBoolean(BUNDLE_KEY_NAV_STATE, navViewVisible)

        toolbarDelegate.saveState(bundle)
    }

    override fun restoreState(bundle: Bundle) {
        val navViewVisible = bundle.getBoolean(BUNDLE_KEY_NAV_STATE)
        val showUp = bundle.getBoolean(BUNDLE_KEY_UP_STATE)

        setUpNavigationVisible(showUp)
        setNavViewVisible(navViewVisible)

        toolbarDelegate.restoreState(bundle)
    }
}