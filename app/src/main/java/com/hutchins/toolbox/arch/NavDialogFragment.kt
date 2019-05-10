package com.hutchins.toolbox.arch

import android.content.Context
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.hutchins.toolbox.nav.core.NavigationActivity

abstract class NavDialogFragment : DialogFragment(), BaseFragmentIm {
    internal lateinit var navigationActivity: NavigationActivity
    protected val fragmentDelegate: BaseFragmentDelegate by lazy {
        BaseFragmentDelegate(this)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is NavigationActivity) {
            navigationActivity = context
        } else {
            throw RuntimeException(context!!.toString() + " must be child class of NavigationActivity!")
        }
        fragmentDelegate
    }

    // Don't crash if attempt to show dialog after fragment onSaveInstanceState called, if fragment no longer top of stack or activity paused
    override fun show(manager: FragmentManager, dialogTag: String) {
        try {
            super.show(manager, dialogTag)
        } catch (e: IllegalStateException) {
            Log.e("SafeDialogFragment", "Exception", e)
        }
    }

//    override fun getAndroidFragmentManager(): FragmentManager? {
//        return fragmentManager
//    }
//
//    override fun getAndroidLifecycleOwner(): LifecycleOwner {
//        return this
//    }

    override fun getFragmentLifecycleOwner(): LifecycleOwner {
        return this
    }
}