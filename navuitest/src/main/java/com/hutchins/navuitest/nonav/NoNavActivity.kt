package com.hutchins.navuitest.nonav

import com.hutchins.navui.core.NavViewActivity
import com.hutchins.navui.jetpack.JetpackNoNavDelegate
import com.hutchins.navuitest.R

class NoNavActivity : NavViewActivity() {
    override val navigationGraphResourceId = R.navigation.side_nav_graph
    override val navViewDelegate = JetpackNoNavDelegate(this)
}
