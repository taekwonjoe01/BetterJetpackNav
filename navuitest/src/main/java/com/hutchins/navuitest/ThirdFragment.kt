package com.hutchins.navuitest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.BaseScreenFragment
import com.hutchins.navuitest.databinding.FragmentThirdBinding

class ThirdFragment : BaseScreenFragment() {

    override fun onCurrentNavFragment(destination: NavDestination) {
        super.onCurrentNavFragment(destination)
        Log.e("AppDebug", "ThirdFragment onCurrentNavFragment")
    }

    override fun onNotCurrentNavFragment() {
        super.onNotCurrentNavFragment()
        Log.e("AppDebug", "ThirdFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentThirdBinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_thirdFragment_to_fourthFragment)
            }
        }
        return binding.root
    }
}