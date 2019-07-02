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

interface NavViewDelegate {
    val navViewActivity: NavViewActivity

    /**
     * The [NavViewDelegate] requires knowing what the root view that will hold the [NavHostFragment] will be.
     */
    val navHostResourceId: Int

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

    fun newInstanceNavUiController(screenFragment: BaseScreenFragment): BaseNavUIController

    fun saveState(bundle: Bundle)
    fun restoreState(bundle: Bundle)
}