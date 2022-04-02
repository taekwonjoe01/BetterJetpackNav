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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.hutchins.navcore

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import java.lang.Exception

/**
 * NavigationActivity defines a Navigation pattern that allows for safe access to Navigation lifecycle events to a Fragment.
 * This Activity maintains a state machine that keeps track of Fragment transactions within the [NavController] and provides
 * a lifecycle of these navigation transitions through methods calls [PrimaryNavFragment.onStartPrimaryNavFragment] and
 * [PrimaryNavFragment.onStopPrimaryNavFragment].
 *
 * <p>This Activity must be used with the Navigation Architecture component and children must provide the resource Id to
 * the navigation graph. All [NavDestination]s within the navigation graph must be children of [PrimaryNavFragment] as that is
 * the only way we can keep track of the navigation back-stack state machine.</p>
 *
 * <p> The Navigation lifecycle guarantees that only one [PrimaryNavFragment] will be inside the [PrimaryNavFragment.onStartPrimaryNavFragment] lifecycle
 * at a time. This is useful to know for many reasons, one common one is synchronization around a Fragment's access to a
 * "Navigation View" held by the [AppCompatActivity]. Generally, a toolbar is held at the Activity level, but a Fragment may want
 * to change the title or other features. Common bugs can occur if the Fragment attempts to do this at invalid times close to
 * Lifecycle transitions. This pattern explicitly defines lifecycle for a Fragment to know when it is safe to do these operations.</p>
 *
 * <p> This Navigation pattern also provides convenient means for a [PrimaryNavFragment] to register for callbacks on common
 * navigation events like [onBackPressed] and [onSupportNavigateUp] and optionally handle those events themselves. See
 * [UpButtonOverrideProvider] and [BackButtonOverrideProvider].</p>
 */
abstract class NavigationActivity : AppCompatActivity() {
    companion object {
        const val TAG = "NavCore"
    }
    protected lateinit var primaryNavController: NavController

    /**
     * Called during [AppCompatActivity].[onCreate]. This will be called at the end of [onCreate] providing
     * a reference to the [NavController] this activity will use during its lifetime.
     */
    protected open fun onPrimaryNavigationInitialized(navController: NavController) {

    }

    /**
     * Called when a [PrimaryNavFragment] navigation exchange is about to occur.
     */
    protected open fun beforePrimaryNavigation() {

    }

    /**
     * Called after the [PrimaryNavFragment] has swapped and the view created.
     *
     * @param primaryNavFragment The currently active BaseNavFragment.
     */
    protected open fun afterPrimaryNavigation(primaryNavFragment: PrimaryNavFragment) {

    }

    private var firstFragmentSet = false
    private lateinit var currentPrimaryNavFragmentReference: PrimaryNavFragment
    private lateinit var currentPrimaryNavigationConfig: PrimaryNavigationConfig

    /**
     * Initializer function the implementing activity should call when the [getSupportFragmentManager]
     * has been committed with
     */
    fun initPrimaryNavigation() {
        val navHostFragment = supportFragmentManager.primaryNavigationFragment?.let {
            try {
                it as NavHostFragment
            } catch (e: Exception) {
                throw IllegalStateException("Cannot init Primary Navigation. No navHostFragment set as " +
                        "primaryNavigationFragment on supportFragmentManager.")
            }
        } ?: throw IllegalStateException("Cannot init Primary Navigation. No navHostFragment set as " +
                "primaryNavigationFragment on supportFragmentManager.")
        this.primaryNavController = navHostFragment.navController
        // If the view Delegate wants to connect the Navigation Controller to its view, it can do so
        // in this call. Generally a side nav will want to connect, also an ActionBar.
        onPrimaryNavigationInitialized(primaryNavController)
    }

    /**
     * Should ONLY be called by a BaseNavFragment after the view is created.
     *
     * The synchronization of Fragment lifecycles with navigation lifecycles is delicate. A Fragment might be created and attached
     * before or after the onDestinationChanged callback occurs in the NavController. Empirically, I have gathered this behavior as it differs
     * between forward, back, and up navigation events. Not all events are equal.
     */
    internal fun registerPrimaryNavFragment(primaryNavFragment: PrimaryNavFragment) {
        firstFragmentSet.apply {
            beforePrimaryNavigation()

            if (this) {
                currentPrimaryNavFragmentReference.onRemoveAsCurrentNavFragment()
            }
            currentPrimaryNavFragmentReference = primaryNavFragment

            val navConfig = primaryNavFragment.onSetAsCurrentNavFragment(primaryNavController.currentDestination!!)
            currentPrimaryNavigationConfig = navConfig

            afterPrimaryNavigation(primaryNavFragment)

            firstFragmentSet = true
        }
    }

    override fun onBackPressed() {
        if (!maybeDoBackButtonOverride()) {
            // Distinguish this navigation.
            // isHardwareBackNavigation = true
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (maybeDoNavigateUpOverride()) false else super.onSupportNavigateUp()
    }

    fun maybeDoBackButtonOverride(): Boolean {
        return if (firstFragmentSet) currentPrimaryNavigationConfig.backButtonOverrideProvider.onBackPressed() else false
    }

    fun maybeDoNavigateUpOverride(): Boolean {
        return if (firstFragmentSet) currentPrimaryNavigationConfig.upButtonOverrideProvider.onSupportNavigateUp() else false
    }
}

/**
 * Config class returned by [PrimaryNavFragment] when the [NavigationActivity] has notified it that it is the active
 * Fragment in the navigation.
 */
open class PrimaryNavigationConfig(val backButtonOverrideProvider: BackButtonOverrideProvider, val upButtonOverrideProvider: UpButtonOverrideProvider)

/**
 * Listener callback when a Navigation override occurs.
 */
interface NavigationOverrideClickListener {
    fun onClick(): Boolean
}

/**
 * Delegate class that manages setting an override for the Back Button press events while this [PrimaryNavFragment] is active.
 */
class BackButtonOverrideProvider {
    private var backButtonOverride: NavigationOverrideClickListener? = null

    /**
     * @return true if this is going to be handled, otherwise false. If false, the Activity will proceed as usual with back
     * events.
     */
    internal fun onBackPressed(): Boolean {
        return if (backButtonOverride != null) {
            backButtonOverride?.onClick() ?: false
        } else {
            false
        }
    }

    fun setBackButtonOverride(listener: NavigationOverrideClickListener?) {
        backButtonOverride = listener
    }
}

/**
 * Delegate class that manages setting an override for the Up Button press events while this [PrimaryNavFragment] is active.
 */
class UpButtonOverrideProvider {
    private var upButtonOverride: NavigationOverrideClickListener? = null

    /**
     * @return true if this is going to be handled, otherwise false. If false, the Activity will proceed as usual with up events.
     */
    internal fun onSupportNavigateUp(): Boolean {
        return if (upButtonOverride != null) {
            upButtonOverride?.onClick() ?: false
        } else {
            false
        }
    }

    fun setUpButtonOverride(listener: NavigationOverrideClickListener?) {
        upButtonOverride = listener
    }
}
