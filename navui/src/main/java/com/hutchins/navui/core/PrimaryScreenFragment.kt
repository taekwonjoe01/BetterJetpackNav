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

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.navigation.NavDestination
import com.hutchins.navcore.PrimaryNavFragment

@Suppress("MemberVisibilityCanBePrivate")
abstract class PrimaryScreenFragment : PrimaryNavFragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int ) : super(contentLayoutId)

    /**
     * The [NavViewActivity] that this [PrimaryScreenFragment] lives in.
     */
    protected lateinit var navViewActivity: NavViewActivity

    /**
     * The navUIController provides an API for users of the [PrimaryScreenFragment] to interact at runtime with the
     * [NavViewDelegate].
     */
    protected lateinit var navUiController: BaseNavUIController

    override fun onStartPrimaryNavFragment(destination: NavDestination) {
        this.navUiController.onActive(destination, navViewActivity.navViewDelegate)
    }

    override fun onStopPrimaryNavFragment() {
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