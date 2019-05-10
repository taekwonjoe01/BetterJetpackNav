@file:Suppress("MemberVisibilityCanBePrivate")

package com.hutchins.toolbox.nav.core

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination

abstract class BaseNavFragment : Fragment() {
    private lateinit var navigationActivity: NavigationActivity
    protected val backButtonOverrideProvider = BackButtonOverrideProvider()

    abstract fun onNotCurrentNavFragment()
    abstract fun onCurrentNavFragment(destination: NavDestination)

    // This flag is used to issue warning to consumers when the Nav state machine might be out of whack.
    private var isCurrentNavFragment = false

    /**
     * Called only by NavigationActivity to signal to this Fragment that it is no longer the current Page in
     * control.
     */
    internal open fun onRemoveAsCurrentNavFragment() {
        onNotCurrentNavFragment()
        isCurrentNavFragment = false
    }

    /**
     * Called only by NavigationActivity to signal to this Fragment that it is the current Page in
     * control.
     */
    internal open fun onSetAsCurrentNavFragment(destination: NavDestination): NavigationConfig {
        if (isCurrentNavFragment) {
            Log.w("BaseNavFragment", "onSetAsCurrentNavFragment called but it looks like this BaseNavFragment was already set as current screen. " +
                    "Perhaps you have a non-BaseNavFragment in the navigation graph which will cause undefined behavior.")
        }
        isCurrentNavFragment = true
        onCurrentNavFragment(destination)

        return NavigationConfig(backButtonOverrideProvider)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavigationActivity) {
            navigationActivity = context
        } else {
            throw RuntimeException("$context must be child class of NavigationActivity!")
        }

//        // Initiate the FragmentDelegate when a BaseNavFragment is attached.
//        fragmentDelegate
    }

//    override fun onAttachFragment(childFragment: Fragment) {
//        super.onAttachFragment(childFragment)
//        // This is called when a Fragment has been added to this Fragment's view hierarchy. If this is the case,
//        // we need to assert it is an instance of EmbeddedFragment. If a user of this library erroneously tries to
//        // use a BaseNavFragment in this way, we will have bad, undefined behavior. So we need to prevent that from
//        // occurring.
//        if (childFragment is EmbeddedFragment) {
//            // Notify this so that the EmbeddedFragment knows when it is attached to a lifecycle other than its own.
//            childFragment.onAttachedToScreenFragment(this)
//        } else {
//            throw RuntimeException("$childFragment must be child class of EmbeddedFragment!")
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // When a BaseNavFragment is built, it needs to register to the NavigationActivity.
        navigationActivity.registerBaseNavFragment(this)
    }
}