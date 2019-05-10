package com.hutchins.toolbox.arch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class BaseFragmentDelegate(private val fragmentIm: BaseFragmentIm) {
//    companion object {
//        private const val PROGRESS_OVERLAY_TAG = "PROGRESS_OVERLAY_TAG"
//    }
//    val progressOverlay by lazy {
//        LoadingOverlayDialogFragment.newInstance(fragmentIm.progressOverlayStyle)
//    }
    var viewModel: BaseViewModel? = null
//    private var informationDialogFragment: InformationDialogFragment? = null
    private val lifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStartListener() {
//            viewModel?.progressOverlayShownObservable?.observe(fragmentIm.getFragmentLifecycleOwner(), Observer { show ->
//                show?.let { shouldShow ->
//                    if (shouldShow) {
//                        showProgressOverlay()
//                    } else {
//                        dismissProgressOverlay()
//                    }
//                }
//            })
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStopListener() {
//            dismissProgressOverlay()
//            dismissInformationDialog()
//            viewModel?.progressOverlayShownObservable?.removeObservers(fragmentIm.getFragmentLifecycleOwner())
        }
    }

    init {
        viewModel = fragmentIm.createViewModel()
//        fragmentIm.getFragmentLifecycleOwner().lifecycle.addObserver(lifecycleObserver)
    }

//    fun showProgressOverlay() {
//        progressOverlay.apply {
//            displayProgressOverlay()
//            clearMessage()
//        }
//    }
//
//    fun showProgressOverlay(message : String, @StyleRes style: Int) {
//        progressOverlay.apply {
//            displayProgressOverlay()
//            setMessage(message, style)
//        }
//    }
//
//    fun showProgressOverlay(message : String){
//        progressOverlay.apply {
//            displayProgressOverlay()
//            setMessage(message)
//        }
//    }
//
//    fun dismissProgressOverlay() {
//        if (progressOverlay.isAdded) {
//            progressOverlay.dismiss()
//        }
//    }
//
//    fun dismissInformationDialog() {
//        informationDialogFragment?.let {
//            if (it.isAdded) {
//                it.dismiss()
//            }
//        }
//    }
//
//    private fun displayProgressOverlay(){
//        progressOverlay.show(fragmentIm.getFragmentManager(), PROGRESS_OVERLAY_TAG)
//    }
//
//    fun showDialog(newInfoDialogFragment: InformationDialogFragment) {
//        informationDialogFragment?.let { displayedInfoDialogFragment ->
//            if (displayedInfoDialogFragment.isResumed) {
//                displayedInfoDialogFragment.dismiss()
//            }
//        }
//        informationDialogFragment = newInfoDialogFragment
//        informationDialogFragment?.show(fragmentIm.getFragmentManager()!!, "errorDialog")
//    }
}

interface BaseFragmentIm {
    fun createViewModel(): BaseViewModel?
    fun getFragmentLifecycleOwner(): LifecycleOwner
//    fun getViewLifecycleOwner(): LifecycleOwner
//    fun getFragmentManager(): FragmentManager?
}