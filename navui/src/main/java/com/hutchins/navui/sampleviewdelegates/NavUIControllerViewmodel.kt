package com.hutchins.navui.sampleviewdelegates

import androidx.lifecycle.ViewModel

/**
 * Created by jhutchins on 5/23/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class NavUIControllerViewmodel : ViewModel() {
    var toolbarTitle: String? = null
    var toolbarVisibilityState: ToolbarDelegate.ToolbarVisibilityState? = null
    var overrideUp: Boolean? = null
    var navViewGone: Boolean? = null
}