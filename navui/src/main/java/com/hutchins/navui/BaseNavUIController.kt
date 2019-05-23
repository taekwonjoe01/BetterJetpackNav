package com.hutchins.navui

import androidx.navigation.NavDestination

abstract class BaseNavUIController {
    protected var destination: NavDestination? = null
        private set
    protected var navigationViewDelegate: NavigationViewDelegate? = null
        private set

    abstract fun onNavUIActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate)

    internal fun onActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        this.destination = destination
        this.navigationViewDelegate = navigationViewDelegate

        onNavUIActive(destination, navigationViewDelegate)
    }

    internal fun onInactive() {
        this.destination = null
        this.navigationViewDelegate = null
    }
}