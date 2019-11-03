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

package com.hutchins.navui.jetpack

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import com.hutchins.navui.core.BaseScreenFragment

/**
 * Convenient extension of [BaseScreenFragment] to provide references for child Fragments to have a direct reference to
 * the [JetpackNavUIController].
 */
open class JetpackScreenFragment : BaseScreenFragment() {
    /**
     * Reference to the [JetpackNavUIController] that provides an API to interact with the Navigation Views.
     */
    protected lateinit var jetpackNavUIController: JetpackNavUIController

    open fun onActionMenuCreated(menu: Menu) {

    }

    open fun onActionItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        jetpackNavUIController = navUiController as JetpackNavUIController
    }
}