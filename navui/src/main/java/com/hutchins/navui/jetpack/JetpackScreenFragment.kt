package com.hutchins.navui.jetpack

import android.content.Context
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        jetpackNavUIController = navUiController as JetpackNavUIController
    }
}