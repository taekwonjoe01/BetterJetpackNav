package com.hutchins.navuitest.side

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.jetpack.JetpackScreenFragment
import com.hutchins.navuitest.R
import com.hutchins.navuitest.TweakSettingsFragment
import com.hutchins.navuitest.databinding.FragmentSecondRootBinding

class SecondRootFragment: JetpackScreenFragment() {
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as TweakSettingsFragment).setToolbarController(jetpackNavUIController)
    }

    override fun onStartPrimaryNavFragment(destination: NavDestination) {
        super.onStartPrimaryNavFragment(destination)
        Log.e("AppDebug", "SecondRootFragment onCurrentNavFragment")
    }

    override fun onStopPrimaryNavFragment() {
        super.onStopPrimaryNavFragment()
        Log.e("AppDebug", "SecondRootFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSecondRootBinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_secondRootFragment_to_secondAFragment)
            }
            button2.setOnClickListener {
                findNavController().navigate(R.id.action_secondRootFragment_to_secondBFragment)
            }
        }

        return binding.root
    }
}