package com.hutchins.navuitest.side


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.jetpack.JetpackScreenFragment
import com.hutchins.navuitest.R
import com.hutchins.navuitest.TweakSettingsFragment
import com.hutchins.navuitest.databinding.FragmentThirdFourthCBinding

class ThirdFourthCFragment : JetpackScreenFragment() {
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as TweakSettingsFragment).setToolbarController(jetpackNavUIController)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentThirdFourthCBinding.inflate(inflater, container, false).apply {
            button2.setOnClickListener {
                findNavController().navigate(R.id.action_thirdFourthCFragment_to_thirdFourthBFragment)
            }
        }
        return binding.root
    }
}