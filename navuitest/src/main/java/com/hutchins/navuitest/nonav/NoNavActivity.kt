package com.hutchins.navuitest.nonav

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.jetpack.JetpackNoNavDelegate
import com.hutchins.navuitest.R

class NoNavActivity : NavViewActivity() {
    override val navViewDelegate = JetpackNoNavDelegate(this)

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
    }
}
