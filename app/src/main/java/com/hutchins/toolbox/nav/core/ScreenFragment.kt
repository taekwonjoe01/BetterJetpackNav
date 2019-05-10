package com.hutchins.toolbox.nav.core

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination

/**
 * Created by jhutchins on 5/9/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
abstract class ScreenFragment : BaseFragment() {
    protected val upButtonOverrideProvider = UpButtonOverrideProvider()
    protected val backButtonOverrideProvider = BackButtonOverrideProvider()

    abstract fun onNotCurrentPage()
    abstract fun onCurrentPage(destination: NavDestination)

    /**
     * Called only by NavigationActivity to signal to this Fragment that it is no longer the current Page in
     * control.
     */
    internal fun onRemoveAsCurrentPage() {
        onNotCurrentPage()
    }

    /**
     * Called only by NavigationActivity to signal to this Fragment that it is the current Page in
     * control.
     */
    internal fun onSetAsCurrentPage(destination: NavDestination): NavigationConfig {
        onCurrentPage(destination)

        return NavigationConfig(upButtonOverrideProvider, backButtonOverrideProvider)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initiate the FragmentDelegate when a ScreenFragment is attached.
        fragmentDelegate
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        // This is called when a Fragment has been added to this Fragment's view hierarchy. If this is the case,
        // we need to assert it is an instance of EmbeddedFragment. If a user of this library erroneously tries to
        // use a ScreenFragment in this way, we will have bad, undefined behavior. So we need to prevent that from
        // occurring.
        if (childFragment is EmbeddedFragment) {
            // Notify this so that the EmbeddedFragment knows when it is attached to a lifecycle other than its own.
            childFragment.onAttachedToScreenFragment(this)
        } else {
            throw RuntimeException("$childFragment must be child class of EmbeddedFragment!")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // When a ScreenFragment is built, it needs to register to the NavigationActivity.
        navigationActivity.registerPageFragment(this)
    }
}