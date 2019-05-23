package com.hutchins.navui.sampleviewdelegates

import androidx.lifecycle.ViewModel

class NavUIControllerViewmodel : ViewModel() {
    var toolbarTitle: String? = null
    var toolbarVisibilityState: ToolbarDelegate.ToolbarVisibilityState? = null
    var overrideUp: Boolean? = null
    var showNavView: Boolean? = null
}