package com.hutchins.navuitest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hutchins.navui.BaseScreenFragment
import com.hutchins.navuitest.databinding.FragmentFourthBinding

class FourthFragment: BaseScreenFragment() {

    override fun onCurrentNavFragment(destination: NavDestination) {
        super.onCurrentNavFragment(destination)
        Log.e("AppDebug", "FourthFragment onCurrentNavFragment")
    }

    override fun onNotCurrentNavFragment() {
        super.onNotCurrentNavFragment()
        Log.e("AppDebug", "FourthFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFourthBinding.inflate(inflater, container, false).apply {
            button.setOnClickListener {
                findNavController().navigate(R.id.action_fourthFragment_to_fifthFragment)
            }
        }
        return binding.root
    }
}