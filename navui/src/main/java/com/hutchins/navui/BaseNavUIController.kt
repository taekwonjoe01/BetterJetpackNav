package com.hutchins.navui

import android.content.Context
import androidx.navigation.NavDestination

abstract class BaseNavUIController(protected val context: Context) {

    private var destination: NavDestination? = null
    private var navigationViewDelegate: NavigationViewDelegate? = null

    private val transactionsQueue = ArrayList<BaseNavUIControllerTransaction>()

    abstract fun onNavUIActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate)

    protected fun addTransaction(transaction: BaseNavUIControllerTransaction) {
        transactionsQueue.add(transaction)
        conditionallyProcessNextTransaction()
    }

    internal fun onActive(destination: NavDestination, navigationViewDelegate: NavigationViewDelegate) {
        this.destination = destination
        this.navigationViewDelegate = navigationViewDelegate

        onNavUIActive(destination, navigationViewDelegate)
        conditionallyProcessNextTransaction()
    }

    internal fun onInactive() {
        this.destination = null
        this.navigationViewDelegate = null
    }

    private fun conditionallyProcessNextTransaction() {
        navigationViewDelegate?.let {navigationDelegate ->
            destination?.let { dest ->
                if (transactionsQueue.isNotEmpty()) {
                    val transaction = transactionsQueue.removeAt(0)
                    transaction.doTransaction(navigationDelegate, dest)

                    conditionallyProcessNextTransaction()
                }
            }
        }
    }

    interface BaseNavUIControllerTransaction {
        fun doTransaction(navigationViewDelegate: NavigationViewDelegate, destination: NavDestination)
    }
}