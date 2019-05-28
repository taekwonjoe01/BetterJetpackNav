package com.hutchins.navuitest.side

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.hutchins.navui.common.SideNavViewDelegate
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navuitest.R

class SideNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navigationViewDelegate = SideNavViewDelegate(this, R.menu.side_nav_menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = navigationViewDelegate.binding.toolbarLayout.toolbar
        toolbar.setTitleTextAppearance(this, R.style.SideToolbarTitleTextAppearance)
        toolbar.setSubtitleTextAppearance(this, R.style.SideToolbarSubTitleTextAppearance)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        navigationViewDelegate.upDrawable.color = ContextCompat.getColor(this, android.R.color.holo_blue_light)
    }
}
