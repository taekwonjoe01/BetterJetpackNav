package com.hutchins.navuitest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hutchins.navui.BaseScreenFragment
import com.hutchins.navui.ToolbarDelegate
import com.hutchins.navuitest.databinding.FragmentTweakSettingsBinding

/**
 * Created by jhutchins on 5/15/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class TweakSettingsFragment : Fragment() {
    private lateinit var toolbarController: BaseScreenFragment.ToolbarController
    internal fun setToolbarController(toolbarController: BaseScreenFragment.ToolbarController) {
        this.toolbarController = toolbarController
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTweakSettingsBinding.inflate(inflater, container, false).apply {
            buttonChangeTitle.setOnClickListener {
                toolbarController.setToolbarTitle("Tweaked Title")
            }
            buttonSetVisible.setOnClickListener {
                toolbarController.setToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.VISIBLE)
            }
            buttonSetGone.setOnClickListener {
                toolbarController.setToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.GONE)
            }
            buttonSetInvisible.setOnClickListener {
                toolbarController.setToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.INVISIBLE)
            }
            buttonAnimateVisible.setOnClickListener {
                toolbarController.animateToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.VISIBLE, 1000L)
            }
            buttonAnimateGone.setOnClickListener {
                toolbarController.animateToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.GONE, 1000L)
            }
            buttonAnimateInvisible.setOnClickListener {
                toolbarController.animateToolbarVisibility(ToolbarDelegate.ToolbarVisibilityState.INVISIBLE, 1000L)
            }
            buttonSetOverrideUp.setOnClickListener {
                toolbarController.setToolbarOverrideUp(true)
            }
            buttonRemoveOverrideUp.setOnClickListener {
                toolbarController.setToolbarOverrideUp(false)
            }
            buttonSetNavViewVisibile.setOnClickListener {
                toolbarController.setNavViewVisibility(true)
            }
            buttonSetNavViewGone.setOnClickListener {
                toolbarController.setNavViewVisibility(false)
            }
        }
        return binding.root
    }
}