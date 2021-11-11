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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination

class NavUIControllerViewModel(val navUISettings: List<NavUISetting<*>>) : ViewModel(), NavUISettingListener {
    init {
        for (setting in navUISettings) {
            setting.listener = this
        }
    }

    private var destination: NavDestination? = null
    private var navViewDelegate: NavViewDelegate? = null
    fun onNavUIActive(destination: NavDestination, navViewDelegate: NavViewDelegate) {
        this.destination = destination
        this.navViewDelegate = navViewDelegate

        for (navUIPersistentSetting in navUISettings) {
            Log.e("Joey", "calling onActive")
            navUIPersistentSetting.onActive(destination, navViewDelegate)
        }
    }

    fun onNavUIInactive() {
        this.destination = null
        this.navViewDelegate = null
    }

    override fun onValueSet(navUISetting: NavUISetting<*>) {
        destination?.let { dest ->
            navViewDelegate?.let { viewDelegate ->
                navUISetting.onSettingSetWhileActive(dest, viewDelegate)
            }
        }
    }
}

class NavUIControllerViewModelFactory(private val baseNavUIController: BaseNavUIController) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NavUIControllerViewModel(baseNavUIController.buildSettings()) as T
    }
}

internal interface NavUISettingListener {
    fun onValueSet(navUISetting: NavUISetting<*>)
}

abstract class NavUISetting<T : Any?> {
    internal lateinit var listener: NavUISettingListener
    var setting: T? = null
        set(value) {
            field = value
            listener.onValueSet(this)
        }

    internal fun onActive(destination: NavDestination, navViewDelegate: NavViewDelegate) {
        applySetting(destination, navViewDelegate, true, setting)
    }

    internal fun onSettingSetWhileActive(destination: NavDestination, navViewDelegate: NavViewDelegate) {
        Log.e("Joey", "onSettingSetWhileActive")
        applySetting(destination, navViewDelegate, false, setting)
    }

    abstract fun applySetting(destination: NavDestination, navViewDelegate: NavViewDelegate, isNavigationTransition: Boolean, setting: T?)
}