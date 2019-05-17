package com.hutchins.navuitest.side


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.BaseScreenFragment
import com.hutchins.navuitest.R
import com.hutchins.navuitest.TweakSettingsFragment
import com.hutchins.navuitest.databinding.FragmentThirdABinding

/**
 * Created by jhutchins on 5/15/19.
 * Copyright (c) 2019 Engage FT. All rights reserved.
 */
class ThirdAFragment : BaseScreenFragment() {
    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        (childFragment as TweakSettingsFragment).setToolbarController(navUiController)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentThirdABinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_thirdAFragment_to_thirdBFragment)
            }
        }
        return binding.root
    }
}