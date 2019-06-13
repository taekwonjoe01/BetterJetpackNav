package com.hutchins.navuitest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hutchins.navui.core.BaseNavUIController
import com.hutchins.navui.jetpack.JetpackNavUIController
import com.hutchins.navui.jetpack.JetpackToolbarDelegate
import com.hutchins.navuitest.databinding.FragmentTweakSettingsBinding

class TweakSettingsFragment : Fragment() {
    private lateinit var jetpackNavUIController: JetpackNavUIController
    internal fun setToolbarController(navUiController: BaseNavUIController) {
        this.jetpackNavUIController = navUiController as JetpackNavUIController
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTweakSettingsBinding.inflate(inflater, container, false).apply {
            buttonChangeTitle.setOnClickListener {
                jetpackNavUIController.setToolbarTitle("Tweaked Title")
            }
            buttonSetVisible.setOnClickListener {
                jetpackNavUIController.setToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.VISIBLE)
            }
            buttonSetGone.setOnClickListener {
                jetpackNavUIController.setToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.GONE)
            }
            buttonSetInvisible.setOnClickListener {
                jetpackNavUIController.setToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.INVISIBLE)
            }
            buttonAnimateVisible.setOnClickListener {
                jetpackNavUIController.animateToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.VISIBLE, 1000L)
            }
            buttonAnimateGone.setOnClickListener {
                jetpackNavUIController.animateToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.GONE, 1000L)
            }
            buttonAnimateInvisible.setOnClickListener {
                jetpackNavUIController.animateToolbarVisibility(JetpackToolbarDelegate.ToolbarVisibilityState.INVISIBLE, 1000L)
            }
            buttonSetOverrideUp.setOnClickListener {
                jetpackNavUIController.setToolbarOverrideUp(true)
            }
            buttonRemoveOverrideUp.setOnClickListener {
                jetpackNavUIController.setToolbarOverrideUp(false)
            }
            buttonSetNavViewVisibile.setOnClickListener {
                jetpackNavUIController.setNavViewVisibility(true)
            }
            buttonSetNavViewGone.setOnClickListener {
                jetpackNavUIController.setNavViewVisibility(false)
            }
        }
        return binding.root
    }
}