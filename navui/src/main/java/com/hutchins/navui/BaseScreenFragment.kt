package com.hutchins.navui

import android.content.Context
import androidx.navigation.NavDestination
import com.hutchins.navcore.BaseNavFragment

abstract class BaseScreenFragment : BaseNavFragment() {
    protected lateinit var navViewActivity: NavViewActivity

    protected lateinit var navUiController: BaseNavUIController

    override fun onCurrentNavFragment(destination: NavDestination) {
        this.navUiController.onActive(destination, navViewActivity.navigationViewDelegate)
    }

    override fun onNotCurrentNavFragment() {
        this.navUiController.onInactive()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavViewActivity) {
            navViewActivity = context
            navUiController = navViewActivity.navigationViewDelegate.newInstanceNavUiController()
        } else {
            throw RuntimeException("$context must be child class of NavViewActivity!")
        }
    }

}