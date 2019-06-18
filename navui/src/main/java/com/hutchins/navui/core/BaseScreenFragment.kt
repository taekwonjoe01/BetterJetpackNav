package com.hutchins.navui.core

import android.content.Context
import androidx.navigation.NavDestination
import com.hutchins.navcore.BaseNavFragment

abstract class BaseScreenFragment : BaseNavFragment() {
    /**
     * The [NavViewActivity] that this [BaseScreenFragment] lives in.
     */
    protected lateinit var navViewActivity: NavViewActivity

    /**
     * The navUIController provides an API for users of the [BaseScreenFragment] to interact at runtime with the
     * [NavViewDelegate].
     */
    protected lateinit var navUiController: BaseNavUIController

    override fun onCurrentNavFragment(destination: NavDestination) {
        this.navUiController.onActive(destination, navViewActivity.navViewDelegate)
    }

    override fun onNotCurrentNavFragment() {
        this.navUiController.onInactive()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavViewActivity) {
            navViewActivity = context
            navUiController = navViewActivity.navViewDelegate.newInstanceNavUiController(this)
        } else {
            throw RuntimeException("$context must be child class of NavViewActivity!")
        }
    }
}