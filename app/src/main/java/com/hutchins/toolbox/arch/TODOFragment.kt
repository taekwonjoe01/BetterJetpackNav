package com.hutchins.toolbox.arch

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.hutchins.toolbox.nav.core.NavigationActivity

abstract class TODOFragment : Fragment(), BaseFragmentIm {
    internal lateinit var navigationActivity: NavigationActivity
    protected val fragmentDelegate: BaseFragmentDelegate by lazy {
        BaseFragmentDelegate(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavigationActivity) {
            navigationActivity = context
        } else {
            throw RuntimeException(context.toString() + " must be child class of NavigationActivity!")
        }
        fragmentDelegate
    }

//    override fun getFragmentManager(): FragmentManager? {
//        return fragmentManager
//    }

    override fun getFragmentLifecycleOwner(): LifecycleOwner {
        return this
    }

//    override fun getFragmentLifecycle(): Lifecycle {
//        return lifecycle
//    }
}