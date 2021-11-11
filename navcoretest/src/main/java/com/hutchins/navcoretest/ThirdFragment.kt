package com.hutchins.navcoretest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navcore.NavigationOverrideClickListener
import com.hutchins.navcoretest.databinding.FragmentThirdBinding

class ThirdFragment : TestablePrimaryNavFragment() {

    fun setOverrideBack() {
        backButtonOverrideProvider.setBackButtonOverride(object : NavigationOverrideClickListener {
            override fun onClick(): Boolean {
                return true
            }
        })
    }

    fun clearBackOverride() {
        backButtonOverrideProvider.setBackButtonOverride(null)
    }

    override fun onStartPrimaryNavFragment(destination: NavDestination) {
        super.onStartPrimaryNavFragment(destination)
        Log.e("AppDebug", "ThirdFragment onCurrentNavFragment")
    }

    override fun onStopPrimaryNavFragment() {
        super.onStopPrimaryNavFragment()
        Log.e("AppDebug", "ThirdFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentThirdBinding.inflate(inflater, container, false).apply {
            thirdFragmentButton.setOnClickListener {
                findNavController().navigate(R.id.action_thirdFragment_to_fourthFragment)
            }
        }
        return binding.root
    }
}