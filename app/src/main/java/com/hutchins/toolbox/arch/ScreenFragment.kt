package com.hutchins.toolbox.arch

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.hutchins.archcore.fragment.BaseFragmentDelegate
import com.hutchins.archcore.fragment.BaseFragmentIm
import com.hutchins.navui.core.BaseScreenFragment

abstract class ScreenFragment : BaseScreenFragment(), BaseFragmentIm {
    protected val fragmentDelegate: BaseFragmentDelegate by lazy {
        BaseFragmentDelegate(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

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



//    override fun onAttachFragment(childFragment: Fragment) {
//        super.onAttachFragment(childFragment)
//        // This is called when a Fragment has been added to this Fragment's view hierarchy. If this is the case,
//        // we need to assert it is an instance of EmbeddedFragment. If a user of this library erroneously tries to
//        // use a BaseNavFragment in this way, we will have bad, undefined behavior. So we need to prevent that from
//        // occurring.
//        if (childFragment is EmbeddedFragment) {
//            // Notify this so that the EmbeddedFragment knows when it is attached to a lifecycle other than its own.
//            childFragment.onAttachedToScreenFragment(this)
//        } else {
//            throw RuntimeException("$childFragment must be child class of EmbeddedFragment!")
//        }
//    }
}