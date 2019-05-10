package com.hutchins.toolbox.nav.uicore.navigationviews

import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import com.hutchins.toolbox.R
import com.hutchins.toolbox.databinding.ActivityNoNavBinding
import com.hutchins.toolbox.nav.core.NavigationActivity
import com.hutchins.toolbox.nav.uicore.ToolbarDelegate

class NoNavViewDelegate(navigationActivity: NavigationActivity, private val navController: NavController) : NavigationViewDelegate(navigationActivity) {
    private lateinit var binding: ActivityNoNavBinding

    override val navHostId: Int = R.id.navHost

    private var showUp: Boolean = false

    override fun setContentView() {
        binding = DataBindingUtil.setContentView(navigationActivity, R.layout.activity_no_nav)
    }

    override fun initNavigation(navController: NavController) {
        // We are now overriding the default jetpack NavigatedListeners in order to achieve
        // customizable UP navigation.
        //NavigationUI.setupActionBarWithNavController(appCompatActivity, navController)
        //NavigationUI.setupWithNavController(binding.toolbarLayout.toolbar, navController)

        // Set up ActionBar
        navigationActivity.setSupportActionBar(binding.toolbarLayout.toolbar)

        // To keep UI's similar, we are using drawable provided by Material Design Library that
        // animates an arrow to/from a hamburger icon. This view doesn't need the hamburger part
        // so we set it to be the arrow only, and never alter it.
        upDrawable.progress = ToolbarDelegate.PROGRESS_ARROW
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
        // Do nothing because there is no navigation view.
    }

    // TODO for dashboard https://engageft.atlassian.net/browse/SHOW-164
    override fun updateNavigationEnabled(enabled: Boolean) {
        // Do nothing because there is no navigation view.
    }

    override fun getNavigationMenu(): Menu? {
        return null
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbarLayout.toolbar
    }
}