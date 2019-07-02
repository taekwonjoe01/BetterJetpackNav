package com.hutchins.navcoresample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDestination
import com.hutchins.navcore.BaseNavFragment
import com.hutchins.navcoresample.databinding.FragmentFifthBinding

class FifthFragment : BaseNavFragment() {

    override fun onCurrentNavFragment(destination: NavDestination) {
        Log.e("AppDebug", "FifthFragment onCurrentNavFragment")
    }

    override fun onNotCurrentNavFragment() {
        Log.e("AppDebug", "FifthFragment onNotCurrentNavFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = .inflate(inflater, container, false).apply {
        }
        return binding.root
    }
}