package com.hutchins.toolbox.arch

import com.hutchins.toolbox.nav.core.BaseNavFragment

abstract class EmbeddedFragment : TODOFragment() {
    abstract fun onAttachedToScreenFragment(baseNavFragment: BaseNavFragment)
}