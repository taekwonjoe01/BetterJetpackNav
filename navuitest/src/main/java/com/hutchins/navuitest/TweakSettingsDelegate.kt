package com.hutchins.navuitest

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.hutchins.navui.jetpack.JetpackNavUIController
import com.hutchins.navui.jetpack.JetpackToolbarDelegate

class TweakSettingsDelegate(
    private val jetpackNavUIController: JetpackNavUIController,
    private val view: View
) {
    init {
        view.apply {
            findViewById<AppCompatButton>(R.id.buttonChangeTitle)?.setOnClickListener {
                jetpackNavUIController.setToolbarTitle("Tweaked Title")
            }
            findViewById<AppCompatButton>(R.id.buttonSetVisible)?.setOnClickListener {
                jetpackNavUIController.setToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.VISIBLE)
            }
            findViewById<AppCompatButton>(R.id.buttonSetGone)?.setOnClickListener {
                jetpackNavUIController.setToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.GONE)
            }
            findViewById<AppCompatButton>(R.id.buttonSetInvisible)?.setOnClickListener {
                jetpackNavUIController.setToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.INVISIBLE)
            }
            findViewById<AppCompatButton>(R.id.buttonAnimateVisible)?.setOnClickListener {
                jetpackNavUIController.animateToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.VISIBLE, 500L)
            }
            findViewById<AppCompatButton>(R.id.buttonAnimateGone)?.setOnClickListener {
                jetpackNavUIController.animateToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.GONE, 500L)
            }
            findViewById<AppCompatButton>(R.id.buttonAnimateInvisible)?.setOnClickListener {
                jetpackNavUIController.animateToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.INVISIBLE, 500L)
            }
            findViewById<AppCompatButton>(R.id.buttonSetOverrideUp)?.setOnClickListener {
                jetpackNavUIController.setToolbarOverrideUp(true)
            }
            findViewById<AppCompatButton>(R.id.buttonRemoveOverrideUp)?.setOnClickListener {
                jetpackNavUIController.setToolbarOverrideUp(false)
            }
            findViewById<AppCompatButton>(R.id.buttonSetNavViewVisibile)?.setOnClickListener {
                jetpackNavUIController.setNavViewVisibility(true)
            }
            findViewById<AppCompatButton>(R.id.buttonSetNavViewGone)?.setOnClickListener {
                jetpackNavUIController.setNavViewVisibility(false)
            }
        }
    }
}