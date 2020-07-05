package com.hutchins.navuitest.bottom

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.jetpack.JetpackBottomNavDelegate
import com.hutchins.navuitest.R

class BottomNavActivity : NavViewActivity() {
    override val navViewDelegate = JetpackBottomNavDelegate(this, R.menu.side_nav_menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment = if (savedInstanceState == null) {
            NavHostFragment.create(R.navigation.side_nav_graph)
        } else {
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        }

        navViewDelegate.setContentView()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment) // this is the equivalent to app:defaultNavHost="true"
            .commitNow()
        initPrimaryNavigation()

        val toolbar = navViewDelegate.toolbar
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
    }
}
