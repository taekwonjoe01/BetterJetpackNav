package com.hutchins.navcoretest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navcore.PrimaryNavFragment
import com.hutchins.navcoretest.databinding.FragmentSecondBinding

class SecondFragment : TestablePrimaryNavFragment() {

    override fun onStartPrimaryNavFragment(destination: NavDestination) {
        super.onStartPrimaryNavFragment(destination)
        Log.e("AppDebug", "SecondFragment onCurrentNavFragment")
    }

    override fun onStopPrimaryNavFragment() {
        super.onStopPrimaryNavFragment()
        Log.e("AppDebug", "SecondFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSecondBinding.inflate(inflater, container, false).apply {
            secondFragmentButton.setOnClickListener {
                findNavController().navigate(R.id.action_secondFragment_to_thirdFragment)
            }
            secondFragmentButton2.setOnClickListener {
                findNavController().navigate(R.id.action_secondFragment_to_fifthFragment)
            }
        }
        return binding.root
    }
}