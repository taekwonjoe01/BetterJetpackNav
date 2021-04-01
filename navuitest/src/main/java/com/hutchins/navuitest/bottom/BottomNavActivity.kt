package com.hutchins.navuitest.bottom

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.jetpack.JetpackBottomNavDelegate
import com.hutchins.navuitest.R

class BottomNavActivity : NavViewActivity() {
    override val navViewDelegate = JetpackBottomNavDelegate(this, R.menu.side_nav_menu, R.navigation.side_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = navViewDelegate.toolbar
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
    }
}
