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

package com.hutchins.navui.core

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.navigation.NavController
import com.hutchins.navcore.PrimaryNavFragment
import com.hutchins.navcore.NavigationActivity

abstract class NavViewActivity : NavigationActivity() {
    /**
     * Delegate that dictates the Navigation View chosen by this activity.
     */
    abstract val navViewDelegate: NavViewDelegate

    @CallSuper
    override fun onSupportNavigateUp(): Boolean {
        // This is a redundant call just in case something fails to close the keyboard
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)

        val delegateHandled = navViewDelegate.onSupportNavigateUp()
        return if (!delegateHandled) super.onSupportNavigateUp() else true
    }
    @CallSuper
    override fun onPrimaryNavigationInitialized(navController: NavController) {
        super.onPrimaryNavigationInitialized(navController)
        navViewDelegate.setupNavViewWithNavController(navController)
    }

    @CallSuper
    override fun afterPrimaryNavigation(primaryNavFragment: PrimaryNavFragment) {
        super.afterPrimaryNavigation(primaryNavFragment)
        if (primaryNavFragment !is PrimaryScreenFragment) {
            throw IllegalStateException("All PrimaryNavFragments used in the nav graph must inherit from PrimaryScreenFragment")
        }
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navViewDelegate.saveState(outState)
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navViewDelegate.restoreState(savedInstanceState)
    }

    @CallSuper
    override fun onBackPressed() {
        if (!navViewDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
