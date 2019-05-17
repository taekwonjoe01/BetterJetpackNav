package com.hutchins.navuitest.nonav

import com.hutchins.navui.NavViewActivity
import com.hutchins.navui.sampleviewdelegates.NoNavViewDelegate
import com.hutchins.navuitest.R

class NoNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navigationViewDelegate = NoNavViewDelegate()
}
