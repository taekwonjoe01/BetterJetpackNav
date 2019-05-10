package com.hutchins.toolbox.nav.core

abstract class EmbeddedFragment : BaseFragment() {
    abstract fun onAttachedToScreenFragment(screenFragment: ScreenFragment)
}