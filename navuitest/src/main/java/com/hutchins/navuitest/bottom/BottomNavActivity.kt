package com.hutchins.navuitest.bottom

import android.os.Bundle
import android.util.Log
import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.common.BottomNavViewDelegate
import com.hutchins.navuitest.R

class BottomNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navigationViewDelegate = BottomNavViewDelegate(this, R.menu.side_nav_menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("Joey", "BottomNavActivity")
    }
}
