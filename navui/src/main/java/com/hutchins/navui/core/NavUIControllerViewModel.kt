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
    private var navigationViewDelegate: NavigationViewDelegate? = null
    fun onNavUIActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        this.destination = destination
        this.navigationViewDelegate = navigationViewDelegate

        for (navUIPersistentSetting in navUISettings) {
            navUIPersistentSetting.onActive(destination, navigationViewDelegate)
        }
    }

    fun onNavUIInactive() {
        this.destination = null
        this.navigationViewDelegate = null
    }

    override fun onValueSet(navUISetting: NavUISetting<*>) {
        destination?.let { dest ->
            navigationViewDelegate?.let { viewDelegate ->
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

    internal fun onActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        applySetting(destination, navigationViewDelegate, true, setting)
    }

    internal fun onSettingSetWhileActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        applySetting(destination, navigationViewDelegate, false, setting)
    }

    abstract fun applySetting(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate, isNavigationTransition: Boolean, setting: T?)
}