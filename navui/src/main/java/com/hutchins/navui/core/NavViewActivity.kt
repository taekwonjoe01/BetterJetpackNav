@file:Suppress("MemberVisibilityCanBePrivate")

package com.hutchins.navui.core

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.navigation.NavController
import com.hutchins.navcore.BaseNavFragment
import com.hutchins.navcore.NavigationActivity

abstract class NavViewActivity : NavigationActivity() {
    abstract val navViewDelegate: NavViewDelegate

    override val navigationHostResourceId: Int
        get() = navViewDelegate.navHostResourceId

    override fun onSupportNavigateUp(): Boolean {
        // This is a redundant call just in case something fails to close the keyboard
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)

        val delegateHandled = navViewDelegate.onSupportNavigateUp()
        return if (!delegateHandled) super.onSupportNavigateUp() else true
    }

    override fun onSetContentView() {
        navViewDelegate.setContentView()
    }

    override fun onNavigationInitialized(navController: NavController) {
        super.onNavigationInitialized(navController)
        navViewDelegate.setupNavViewWithNavController(navController)
    }

    override fun onNavigated(baseNavFragment: BaseNavFragment) {
        super.onNavigated(baseNavFragment)
        if (baseNavFragment !is BaseScreenFragment) {
            throw IllegalStateException("All Fragments used in the nav graph must inherit from BaseScreenFragment")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navViewDelegate.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navViewDelegate.restoreState(savedInstanceState)
    }

    override fun onBackPressed() {
        if (!navViewDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
