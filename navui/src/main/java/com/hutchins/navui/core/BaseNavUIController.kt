package com.hutchins.navui.core

import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDestination

abstract class BaseNavUIController(private val baseScreenFragment: BaseScreenFragment) {
    private lateinit var navUIControllerViewModel: NavUIControllerViewModel

    private var isInitialized = false

    abstract fun buildSettings(): List<NavUISetting<*>>

    protected fun initController(): List<NavUISetting<*>> {
        isInitialized = true
        navUIControllerViewModel = ViewModelProviders.of(baseScreenFragment, NavUIControllerViewModelFactory(this)).get(NavUIControllerViewModel::class.java)
        return navUIControllerViewModel.navUISettings
    }

    internal fun onActive(destination: NavDestination, navViewDelegate: NavViewDelegate) {
        if (!isInitialized) throw IllegalStateException("NavUIController not initialized. Did you forget to call initController?")
        navUIControllerViewModel.onNavUIActive(destination, navViewDelegate)
    }

    internal fun onInactive() {
        navUIControllerViewModel.onNavUIInactive()
    }
}