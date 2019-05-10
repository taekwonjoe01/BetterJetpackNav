package com.hutchins.navcoresample

import androidx.navigation.NavController
import com.hutchins.navcore.NavigationActivity
import com.hutchins.navcore.ViewDelegate

class MainActivity : NavigationActivity() {
    override fun getNavigationGraphResourceId() = R.navigation.main_nav_graph
    override fun instantiateViewDelegate(): ViewDelegate {
        return object : ViewDelegate() {
            override val navHostId = R.id.navHost
            override fun setContentView() {
                this@MainActivity.setContentView(R.layout.activity_main)
            }

            override fun initNavigation(navController: NavController) {
                // Not going to do anything here.
            }
        }
    }
}
