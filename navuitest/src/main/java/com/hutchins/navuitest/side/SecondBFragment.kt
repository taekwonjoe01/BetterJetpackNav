package com.hutchins.navuitest.side


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.jetpack.JetpackScreenFragment
import com.hutchins.navuitest.R
import com.hutchins.navuitest.TweakSettingsDelegate
import com.hutchins.navuitest.databinding.FragmentSecondBBinding

class SecondBFragment : JetpackScreenFragment() {
    private lateinit var tweakSettingsDelegate: TweakSettingsDelegate
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSecondBBinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_secondBFragment_to_secondCFragment)
            }
        }
        tweakSettingsDelegate = TweakSettingsDelegate(jetpackNavUIController, binding.root)
        return binding.root
    }
}