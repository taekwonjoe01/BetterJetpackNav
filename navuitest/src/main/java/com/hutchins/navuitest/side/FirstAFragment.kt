package com.hutchins.navuitest.side

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.common.SampleNavUIController
import com.hutchins.navui.core.BaseScreenFragment
import com.hutchins.navuitest.R
import com.hutchins.navuitest.TweakSettingsFragment
import com.hutchins.navuitest.databinding.FragmentFirstABinding

class FirstAFragment : BaseScreenFragment() {
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as TweakSettingsFragment).setToolbarController(navUiController)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFirstABinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_firstAFragment_to_firstBFragment)
            }
        }
        (navUiController as SampleNavUIController).setToolbarActionMenu(R.menu.first_a_action_menu)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.clickme -> {
                (navUiController as SampleNavUIController).setToolbarSubtitle("Look at this cool subtitle!")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}