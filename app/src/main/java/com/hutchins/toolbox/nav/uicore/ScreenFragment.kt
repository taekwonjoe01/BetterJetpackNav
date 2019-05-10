package com.hutchins.toolbox.nav.uicore

import android.content.Context
import androidx.navigation.NavDestination
import com.hutchins.toolbox.nav.core.BaseNavFragment
import com.hutchins.toolbox.nav.core.NavigationConfig

/**
 * Created by jhutchins on 5/10/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
abstract class ScreenFragment : BaseNavFragment() {
    private lateinit var navViewActivity: NavViewActivity
    protected val upButtonOverrideProvider = UpButtonOverrideProvider()

    /**
     * Override to inject an upButtonOverrideProvider
     */
    override fun onSetAsCurrentNavFragment(destination: NavDestination): NavigationConfig {
        val navConfig = super.onSetAsCurrentNavFragment(destination)

        return NavigationViewConfig(navConfig.backButtonOverrideProvider, upButtonOverrideProvider)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavViewActivity) {
            navViewActivity = context
        } else {
            throw RuntimeException("$context must be child class of NavViewActivity!")
        }
    }
}