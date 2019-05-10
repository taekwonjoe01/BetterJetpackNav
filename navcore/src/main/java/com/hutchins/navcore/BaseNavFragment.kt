@file:Suppress("MemberVisibilityCanBePrivate")

package com.hutchins.navcore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination

abstract class BaseNavFragment : Fragment() {
    protected lateinit var navigationActivity: NavigationActivity
    protected val backButtonOverrideProvider = BackButtonOverrideProvider()
    protected val upButtonOverrideProvider = UpButtonOverrideProvider()

    abstract fun onNotCurrentNavFragment()
    abstract fun onCurrentNavFragment(destination: NavDestination)

    // This flag is used to issue warning to consumers when the Nav state machine might be out of whack.
    private var isCurrentNavFragment = false

    /**
     * Called only by NavigationActivity to signal to this Fragment that it is NOT the current navFragment anymore
     */
    internal fun onRemoveAsCurrentNavFragment() {
        onNotCurrentNavFragment()
        isCurrentNavFragment = false
    }

    /**
     * Called only by NavigationActivity to signal to this Fragment that it is the current navFragment and provides it's NavDestination info.
     */
    internal fun onSetAsCurrentNavFragment(destination: NavDestination): NavigationConfig {
        if (isCurrentNavFragment) {
            Log.w("BaseNavFragment", "onSetAsCurrentNavFragment called but it looks like this BaseNavFragment was already set as current screen. " +
                    "Perhaps you have a non-BaseNavFragment in the navigation graph which will cause undefined behavior.")
        }
        isCurrentNavFragment = true
        onCurrentNavFragment(destination)

        return NavigationConfig(backButtonOverrideProvider, upButtonOverrideProvider)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavigationActivity) {
            navigationActivity = context
        } else {
            throw RuntimeException("$context must be child class of NavigationActivity!")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // When a BaseNavFragment is built, it needs to register to the NavigationActivity.
        navigationActivity.registerBaseNavFragment(this)
    }
}