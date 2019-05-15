package com.hutchins.navuitest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import com.hutchins.navui.BaseScreenFragment
import com.hutchins.navuitest.databinding.FragmentFifthBinding

class FifthFragment : BaseScreenFragment() {

    override fun onCurrentNavFragment(destination: NavDestination) {
        super.onCurrentNavFragment(destination)
        Log.e("AppDebug", "FifthFragment onCurrentNavFragment")
    }

    override fun onNotCurrentNavFragment() {
        super.onNotCurrentNavFragment()
        Log.e("AppDebug", "FifthFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFifthBinding.inflate(inflater, container, false).apply {
        }
        return binding.root
    }
}