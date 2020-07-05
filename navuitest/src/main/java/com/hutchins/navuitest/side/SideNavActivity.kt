package com.hutchins.navuitest.side

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.hutchins.navui.jetpack.JetpackSideNavDelegate
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navuitest.R

class SideNavActivity : NavViewActivity() {
    override val navViewDelegate = JetpackSideNavDelegate(this, R.menu.side_nav_menu)

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
            .setPrimaryNavigationFragment(navHostFragment)
            .commitNow()
        initPrimaryNavigation()

        val toolbar = navViewDelegate.toolbar
        toolbar.setTitleTextAppearance(this, R.style.SideToolbarTitleTextAppearance)
        toolbar.setSubtitleTextAppearance(this, R.style.SideToolbarSubTitleTextAppearance)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        navViewDelegate.upDrawable.color = ContextCompat.getColor(this, android.R.color.holo_blue_light)
    }
}
