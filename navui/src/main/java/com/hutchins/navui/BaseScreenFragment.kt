package com.hutchins.navui

import android.content.Context
import androidx.navigation.NavDestination

/**
 * Created by jhutchins on 5/10/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
abstract class BaseScreenFragment : com.hutchins.navcore.BaseNavFragment() {
    protected lateinit var navViewActivity: NavViewActivity

    abstract fun onCurrentScreenFragment()
    abstract fun onNotCurrentScreenFragment()

    override fun onCurrentNavFragment(destination: NavDestination) {
        onCurrentScreenFragment()
    }

    override fun onNotCurrentNavFragment() {
        onNotCurrentScreenFragment()
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