package com.hutchins.navui.core

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
        applySetting(destination, navViewDelegate, false, setting)
    }

    abstract fun applySetting(destination: NavDestination, navViewDelegate: NavViewDelegate, isNavigationTransition: Boolean, setting: T?)
}