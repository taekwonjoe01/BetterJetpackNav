package com.hutchins.navui.common

import androidx.lifecycle.ViewModel

class NavUIControllerViewmodel : ViewModel() {
    var toolbarTitle: String? = null
    var toolbarSubtitle: String? = null
    var toolbarVisibilityState: ToolbarDelegate.ToolbarVisibilityState? = null
    var overrideUp: Boolean? = null
    var showNavView: Boolean? = null
    var actionMenuResourceId: Int? = null
}