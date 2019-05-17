package com.hutchins.navuitest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hutchins.navui.BaseNavUIController
import com.hutchins.navui.sampleviewdelegates.SampleNavUIController
import com.hutchins.navui.sampleviewdelegates.ToolbarDelegate
import com.hutchins.navuitest.databinding.FragmentTweakSettingsBinding

class TweakSettingsFragment : Fragment() {
    private lateinit var sampleNavUIController: SampleNavUIController
    internal fun setToolbarController(navUiController: BaseNavUIController) {
        this.sampleNavUIController = navUiController as SampleNavUIController
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTweakSettingsBinding.inflate(inflater, container, false).apply {
            buttonChangeTitle.setOnClickListener {
                sampleNavUIController.setToolbarTitle("Tweaked Title")
            }
            buttonSetVisible.setOnClickListener {
                sampleNavUIController.setToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.VISIBLE)
            }
            buttonSetGone.setOnClickListener {
                sampleNavUIController.setToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.GONE)
            }
            buttonSetInvisible.setOnClickListener {
                sampleNavUIController.setToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.INVISIBLE)
            }
            buttonAnimateVisible.setOnClickListener {
                sampleNavUIController.animateToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.VISIBLE, 1000L)
            }
            buttonAnimateGone.setOnClickListener {
                sampleNavUIController.animateToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.GONE, 1000L)
            }
            buttonAnimateInvisible.setOnClickListener {
                sampleNavUIController.animateToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.INVISIBLE, 1000L)
            }
            buttonSetOverrideUp.setOnClickListener {
                sampleNavUIController.setToolbarOverrideUp(true)
            }
            buttonRemoveOverrideUp.setOnClickListener {
                sampleNavUIController.setToolbarOverrideUp(false)
            }
            buttonSetNavViewVisibile.setOnClickListener {
                sampleNavUIController.setNavViewVisibility(true)
            }
            buttonSetNavViewGone.setOnClickListener {
                sampleNavUIController.setNavViewVisibility(false)
            }
        }
        return binding.root
    }
}