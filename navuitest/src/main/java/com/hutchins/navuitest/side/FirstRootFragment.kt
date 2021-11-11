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
import com.hutchins.navuitest.databinding.FragmentFirstRootBinding

class FirstRootFragment : JetpackScreenFragment() {
    private lateinit var tweakSettingsDelegate: TweakSettingsDelegate
    override fun onStartPrimaryNavFragment(destination: NavDestination) {
        super.onStartPrimaryNavFragment(destination)
        Log.e("AppDebug", "FirstRootFragment onStartPrimaryNavFragment")
    }

    override fun onStopPrimaryNavFragment() {
        super.onStopPrimaryNavFragment()
        Log.e("AppDebug", "FirstRootFragment onStopPrimaryNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentFirstRootBinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_firstRootFragment_to_firstAFragment)
            }
        }
        tweakSettingsDelegate = TweakSettingsDelegate(jetpackNavUIController, binding.root)
        return binding.root
    }
}