package com.hutchins.navuitest.side


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hutchins.navui.jetpack.JetpackScreenFragment
import com.hutchins.navuitest.TweakSettingsFragment
import com.hutchins.navuitest.databinding.FragmentFirstBBinding

class FirstBFragment : JetpackScreenFragment() {
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as TweakSettingsFragment).setToolbarController(jetpackNavUIController)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentFirstBBinding.inflate(inflater, container, false).apply {

        }
        return binding.root
    }
}