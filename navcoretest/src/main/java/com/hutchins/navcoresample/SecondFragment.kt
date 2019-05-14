package com.hutchins.navcoresample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navcore.BaseNavFragment
import com.hutchins.navcoresample.databinding.FragmentSecondBinding

class SecondFragment : BaseNavFragment() {

    override fun onCurrentNavFragment(destination: NavDestination) {
        Log.e("AppDebug", "SecondFragment onCurrentNavFragment")
    }

    override fun onNotCurrentNavFragment() {
        Log.e("AppDebug", "SecondFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSecondBinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_secondFragment_to_thirdFragment)
            }
            button2.setOnClickListener {
                findNavController().navigate(R.id.action_secondFragment_to_fifthFragment)
            }
        }
        return binding.root
    }
}