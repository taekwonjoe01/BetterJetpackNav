package com.hutchins.navuitest.side


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hutchins.navui.jetpack.JetpackScreenFragment
import com.hutchins.navuitest.TweakSettingsFragment
import com.hutchins.navuitest.databinding.FragmentSecondABinding

class SecondAFragment : JetpackScreenFragment() {
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as TweakSettingsFragment).setToolbarController(jetpackNavUIController)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSecondABinding.inflate(inflater, container, false).apply {

        }
        return binding.root
    }
}