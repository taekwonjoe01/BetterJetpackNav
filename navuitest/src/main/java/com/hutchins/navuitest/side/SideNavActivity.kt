package com.hutchins.navuitest.side

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.hutchins.navui.jetpack.JetpackSideNavDelegate
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navuitest.R

class SideNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navViewDelegate = JetpackSideNavDelegate(this, R.menu.side_nav_menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = navViewDelegate.toolbar
        toolbar.setTitleTextAppearance(this, R.style.SideToolbarTitleTextAppearance)
        toolbar.setSubtitleTextAppearance(this, R.style.SideToolbarSubTitleTextAppearance)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        navViewDelegate.upDrawable.color = ContextCompat.getColor(this, android.R.color.holo_blue_light)
    }
}
