package com.hutchins.toolbox.nav.core

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

//    @Deprecated("Use showProgressOverlayDelayed and dismissProgressOverlay instead.")
//    val progressOverlayShownObservable = MutableLiveData<Boolean>()
//
//    private var isShowing = false
//    protected open val delayBeforeDialogShownMs = 50L
//    protected open val minTimeDialogShownMs = 1000L //time in ms
//
//    private val showRunnable = Runnable {
//        if (!isShowing) {
//            progressOverlayShownObservable.postValue(true)
//            isShowing = true
//            showStartTime = System.currentTimeMillis()
//        }
//    }
//
//    private val dismissRunnable = Runnable {
//        if (isShowing) {
//            isShowing = false
//            progressOverlayShownObservable.postValue(false)
//        }
//    }
//
//    private val overlayHandler = Handler()
//
//    private var showStartTime = System.currentTimeMillis()
//
//    fun showProgressOverlayImmediate(){
//        overlayHandler.apply {
//            removeCallbacks(dismissRunnable)
//            post(showRunnable)
//        }
//    }
//
//    fun showProgressOverlayDelayed(){
//        overlayHandler.apply {
//            removeCallbacks(dismissRunnable)
//            postDelayed(showRunnable, delayBeforeDialogShownMs)
//        }
//    }
//
//    fun dismissProgressOverlayImmediate(){
//        overlayHandler.apply{
//            removeCallbacks(showRunnable)
//            post(dismissRunnable)
//        }
//    }
//
//    fun dismissProgressOverlay(){
//        overlayHandler.removeCallbacks(showRunnable)
//        val timeDiff = System.currentTimeMillis() - showStartTime
//        if (timeDiff < minTimeDialogShownMs) {
//            overlayHandler.postDelayed(dismissRunnable, minTimeDialogShownMs - timeDiff)
//        } else {
//            overlayHandler.post(dismissRunnable)
//        }
//    }
}