package com.hutchins.navuitest.side

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.jetpack.JetpackScreenFragment
import com.hutchins.navuitest.R
import com.hutchins.navuitest.TweakSettingsFragment
import com.hutchins.navuitest.databinding.FragmentFirstABinding

class FirstAFragment : JetpackScreenFragment() {
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as TweakSettingsFragment).setToolbarController(jetpackNavUIController)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFirstABinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_firstAFragment_to_firstBFragment)
            }
        }
        jetpackNavUIController.setToolbarActionMenu(R.menu.first_a_action_menu)
        return binding.root
    }

    override fun onActionItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.clickme -> {
                jetpackNavUIController.setToolbarSubtitle("Look at this cool subtitle!")
                return true
            }
        }
        return super.onActionItemSelected(item)
    }
}