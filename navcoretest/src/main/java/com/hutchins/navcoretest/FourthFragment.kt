package com.hutchins.navcoretest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navcore.NavigationOverrideClickListener
import com.hutchins.navcoretest.databinding.FragmentFourthBinding

class FourthFragment : TestablePrimaryNavFragment() {

    fun setOverrideUp() {
        upButtonOverrideProvider.setUpButtonOverride(object : NavigationOverrideClickListener {
            override fun onClick(): Boolean {
                return true
            }
        })
    }

    override fun onStartPrimaryNavFragment(destination: NavDestination) {
        super.onStartPrimaryNavFragment(destination)
        Log.e("AppDebug", "FourthFragment onCurrentNavFragment")
    }

    override fun onStopPrimaryNavFragment() {
        super.onStopPrimaryNavFragment()
        Log.e("AppDebug", "FourthFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentFourthBinding.inflate(inflater, container, false).apply {
            fourthFragmentButton.setOnClickListener {
                findNavController().navigate(R.id.action_fourthFragment_to_fifthFragment)
            }
        }
        return binding.root
    }
}