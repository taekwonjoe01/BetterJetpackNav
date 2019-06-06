package com.hutchins.navui.core

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController

interface NavigationViewDelegate {
    val navViewActivity: NavViewActivity

    /**
     * The library requires knowing what the root view that will hold the NavHostFragment will be.
     */
    val navHostResourceId: Int

    /**
     * Using the delegate pattern, we let this object inflate and create a view that will be the activity's content view.
     */
    fun onCreateContentView(): View

    /**
     * After the navController is instantiated and setup with the navHostFragment, allow the delegate to connect the controller
     * with any view setup it needs.
     */
    fun setupNavViewWithNavController(navController: NavController)

    /**
     * Handle supportNavigateUp calls that the activity gets.
     */
    fun onSupportNavigateUp(): Boolean

    /**
     * Handle back button calls that the activity gets.
     */
    fun onBackPressed(): Boolean

    fun newInstanceNavUiController(screenFragment: BaseScreenFragment): BaseNavUIController

    fun saveState(bundle: Bundle)
    fun restoreState(bundle: Bundle)
}