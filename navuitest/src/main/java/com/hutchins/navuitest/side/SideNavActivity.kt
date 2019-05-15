package com.hutchins.navuitest.side

import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.viewdelegates.SideNavViewDelegate
import com.hutchins.navuitest.R

class SideNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navigationViewDelegate = SideNavViewDelegate(this, R.menu.side_nav_menu)
}
