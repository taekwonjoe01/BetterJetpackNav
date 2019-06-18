/*******************************************************************************
 * Copyright 2019 Joseph Hutchins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

@file:Suppress("MemberVisibilityCanBePrivate")

package com.hutchins.navcore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination

/**
 * BaseNavFragment defines a specific Fragment implementation that receives contextual information about the
 * navigation lifecycle as well as features to interact better with that lifecycle.
 *
 * <p>This Fragment is intended to be used as a Base class for all of your Fragments in a navigation graph provided to a
 * [NavigationActivity]</p>
 *
 * <p>Similar to normal Fragment lifecycle callbacks, two new callbacks are added to this class
 * [onCurrentNavFragment] and [onNotCurrentNavFragment]. These methods define the Fragment's lifecycle within the
 * Navigation engine. </p>
 *
 * <p>Additionally, two features exist for this Fragment providing overridable BACK and UP methods. See
 * [BackButtonOverrideProvider] and [UpButtonOverrideProvider]</p>
 */
abstract class BaseNavFragment : Fragment() {
    protected lateinit var navigationActivity: NavigationActivity
    protected val backButtonOverrideProvider = BackButtonOverrideProvider()
    protected val upButtonOverrideProvider = UpButtonOverrideProvider()

    /**
     * Called when this Fragment is no longer the current [NavDestination].
     *
     * <p>This can be called on a forward navigation or when this Fragment is popped off the backstack.</p>
     */
    open fun onNotCurrentNavFragment() {
    }

    /**
     * Called when this Fragment is the current [NavDestination] in the navigation graph. This can occur during
     * both forward and back navigations.
     *
     * @param destination The [NavDestination] defining this navigation event.
     */
    open fun onCurrentNavFragment(destination: NavDestination) {

    }

    // This flag is used to issue warning to consumers when the Nav state machine might be out of whack.
    private var isCurrentNavFragment = false

    /**
     * Called only by [NavigationActivity] to signal to this Fragment that it is NOT the current navFragment anymore
     */
    internal fun onRemoveAsCurrentNavFragment() {
        onNotCurrentNavFragment()
        isCurrentNavFragment = false
    }

    /**
     * Called by [NavigationActivity] to signal to this Fragment that it is the current [NavDestination] in
     * the navigation graph.
     *
     * @param destination The [NavDestination] object defining this navigation event.
     */
    internal fun onSetAsCurrentNavFragment(destination: NavDestination): NavigationConfig {
        if (isCurrentNavFragment) {
            Log.w(NavigationActivity.TAG, "onSetAsCurrentNavFragment called but it looks like this BaseNavFragment was already set as current screen. " +
                    "Perhaps you have a non-BaseNavFragment in the navigation graph which will cause undefined behavior.")
        }
        isCurrentNavFragment = true
        onCurrentNavFragment(destination)

        return NavigationConfig(backButtonOverrideProvider, upButtonOverrideProvider)
    }

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavigationActivity) {
            navigationActivity = context
        } else {
            throw RuntimeException("$context must be child class of NavigationActivity!")
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // When a BaseNavFragment is built, it needs to register to the NavigationActivity.
        navigationActivity.registerBaseNavFragment(this)
    }
}