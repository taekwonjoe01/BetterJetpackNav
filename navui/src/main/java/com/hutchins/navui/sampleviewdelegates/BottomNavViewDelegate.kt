package com.hutchins.navui.sampleviewdelegates

import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.hutchins.navui.BaseNavUIController
import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.NavigationViewDelegate
import com.hutchins.navui.R
import com.hutchins.navui.databinding.ActivityBottomNavBinding

class BottomNavViewDelegate(private val navigationMenuResourceId: Int) : NavigationViewDelegate, SampleNavUIController.TestNavViewDelegate {
    lateinit var navViewActivity: NavViewActivity
    private lateinit var binding: ActivityBottomNavBinding
    private lateinit var navController: NavController

    override val navHostResourceId: Int = R.id.navHost
    private var showUp: Boolean = false

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
        if (show) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    override fun newInstanceNavUiController(): BaseNavUIController {
        return SampleNavUIController(context = navViewActivity)
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
}