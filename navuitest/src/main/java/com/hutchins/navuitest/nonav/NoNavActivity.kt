package com.hutchins.navuitest.nonav

import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.common.NoNavViewDelegate
import com.hutchins.navuitest.R

class NoNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navigationViewDelegate = NoNavViewDelegate(this)
}
