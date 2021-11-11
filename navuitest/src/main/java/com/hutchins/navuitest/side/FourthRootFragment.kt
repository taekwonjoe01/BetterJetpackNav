package com.hutchins.navuitest.side

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.jetpack.JetpackScreenFragment
import com.hutchins.navuitest.R
import com.hutchins.navuitest.TweakSettingsDelegate
import com.hutchins.navuitest.databinding.FragmentFourthRootBinding

class FourthRootFragment: JetpackScreenFragment() {
    private lateinit var tweakSettingsDelegate: TweakSettingsDelegate
    override fun onStartPrimaryNavFragment(destination: NavDestination) {
        super.onStartPrimaryNavFragment(destination)
        Log.e("AppDebug", "FourthRootFragment onCurrentNavFragment")
    }

    override fun onStopPrimaryNavFragment() {
        super.onStopPrimaryNavFragment()
        Log.e("AppDebug", "FourthRootFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentFourthRootBinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_fourthRootFragment_to_thirdFourthAFragment)
            }
        }
        tweakSettingsDelegate = TweakSettingsDelegate(jetpackNavUIController, binding.root)
        return binding.root
    }
}