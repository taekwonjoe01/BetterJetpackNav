package com.hutchins.navcoretest

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hutchins.navcore.NavigationActivity
import com.hutchins.navcore.PrimaryNavFragment

class MainActivity : NavigationActivity() {
    var numPrimaryNavInitializedCalls = 0
    var numBeforeNavigatedCalls = 0
    var numAfterNavigatedCalls = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = if (savedInstanceState == null) {
            NavHostFragment.create(R.navigation.main_nav_graph)
        } else {
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        }

        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment) // this is the equivalent to app:defaultNavHost="true"
            .commitNow()
        initPrimaryNavigation()
    }

    fun assertReferenceToNavController() {
        primaryNavController
    }

    override fun beforePrimaryNavigation() {
        super.beforePrimaryNavigation()
        numBeforeNavigatedCalls++
    }

    override fun afterPrimaryNavigation(primaryNavFragment: PrimaryNavFragment) {
        super.afterPrimaryNavigation(primaryNavFragment)
        numAfterNavigatedCalls++
    }

    override fun onPrimaryNavigationInitialized(navController: NavController) {
        super.onPrimaryNavigationInitialized(navController)
        numPrimaryNavInitializedCalls++
    }
}
