package com.hutchins.navcoresample

import com.hutchins.navcore.NavigationActivity

class MainActivity : NavigationActivity() {
    override val navigationGraphResourceId = R.navigation.main_nav_graph
    override val navigationHostResourceId = R.id.navHost

    override fun onSetContentView() {
        setContentView(R.layout.activity_main)
    }
}
