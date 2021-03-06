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

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination

abstract class BaseNavUIController(private val primaryScreenFragment: PrimaryScreenFragment) {
    private lateinit var navUIControllerViewModel: NavUIControllerViewModel

    private var isInitialized = false

    abstract fun buildSettings(): List<NavUISetting<*>>

    protected fun initController(): List<NavUISetting<*>> {
        isInitialized = true
        navUIControllerViewModel = ViewModelProvider(primaryScreenFragment, NavUIControllerViewModelFactory(this)).get(NavUIControllerViewModel::class.java)
        return navUIControllerViewModel.navUISettings
    }

    internal fun onActive(destination: NavDestination, navViewDelegate: NavViewDelegate) {
        check(isInitialized) { "NavUIController not initialized. Did you forget to call initController?" }
        navUIControllerViewModel.onNavUIActive(destination, navViewDelegate)
    }

    internal fun onInactive() {
        navUIControllerViewModel.onNavUIInactive()
    }
}