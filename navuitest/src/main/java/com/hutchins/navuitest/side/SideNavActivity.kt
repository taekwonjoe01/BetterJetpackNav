package com.hutchins.navuitest.side

import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.sampleviewdelegates.SideNavViewDelegate
import com.hutchins.navuitest.R

class SideNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navigationViewDelegate = SideNavViewDelegate(R.menu.side_nav_menu)
}
