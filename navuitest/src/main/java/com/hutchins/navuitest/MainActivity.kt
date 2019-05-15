package com.hutchins.navuitest

import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.viewdelegates.SideNavViewDelegate

class MainActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.main_nav_graph
    override val navigationViewDelegate = SideNavViewDelegate(this, R.menu.test_menu)
}
