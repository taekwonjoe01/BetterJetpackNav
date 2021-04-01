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

package com.hutchins.navui.core

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

/**
 * Delegate interface describing Navigation View functions that must be fulfilled to satisfy the Jetpack
 * Navigation Architecture Component. This interface allows for extensibility and flexibility in creating
 * new custom navigation containers for an Activity.
 */
interface NavViewDelegate {
    val navViewActivity: NavViewActivity

    /**
     * Called after setContentView. Return the instance of NavHostFragment. If the NavHostFragment
     * was declared in xml, simply find it by id and return it here. Can also utilize
     * [NavHostFragment.create] methods, but be mindful of lifecycle.
     */
    fun getNavHostFragment(): NavHostFragment

    /**
     * Using the delegate pattern, we let this object inflate and create a view that will be the activity's content view.
     */
    fun setContentView()

    /**
     * After the navController is instantiated and setup with the navHostFragment, allow the delegate to connect the controller
     * with any view setup it needs.
     */
    fun setupNavViewWithNavController(navController: NavController)

    /**
     * Handle supportNavigateUp calls that the activity gets.
     */
    fun onSupportNavigateUp(): Boolean

    /**
     * Handle back button calls that the activity gets.
     */
    fun onBackPressed(): Boolean

    /**
     * When implementing a custom Navigation View, a [NavViewDelegate] must be defined as well as a
     * [BaseNavUIController]. Certain Fragment/Activity lifecycle events require a re-instantiation
     * of the custom [BaseNavUIController], and this method will be called when this is needed.
     *
     * This method should return a new instance of the custom [BaseNavUIController] for the
     * [PrimaryScreenFragment]s to use at runtime.
     */
    fun newInstanceNavUiController(screenFragment: PrimaryScreenFragment): BaseNavUIController

    /**
     * Called when the [NavViewActivity] is saving state. Deriving classes can save their state.
     */
    fun saveState(bundle: Bundle)

    /**
     * Called when the [NavViewActivity] is restoring state. Deriving classes can restore their state.
     */
    fun restoreState(bundle: Bundle)
}