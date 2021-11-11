package com.hutchins.navcoretest.setup

import android.os.Bundle
import com.hutchins.navcore.NavigationActivity
import com.hutchins.navcoretest.R
import com.hutchins.navcoretest.TestFragment

/**
 * This activity is intended to be used with BadActivity2Tests to show what could occur if the
 * [navigationGraphResourceId] is improperly instantiated.
 */
@Suppress("KDocUnresolvedReference")
class BadActivity2 : NavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Not using the right Fragment as Primary.
        val fragment = TestFragment()

        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, fragment)
            .setPrimaryNavigationFragment(fragment) // this is the equivalent to app:defaultNavHost="true"
            .commitNow()
    }
}
