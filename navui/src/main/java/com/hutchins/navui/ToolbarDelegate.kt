@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.hutchins.navui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.hutchins.navui.viewdelegates.NavigationViewDelegate

/**
 * This class manages the Toolbar and maintaints the state of it with respect to LotusPageFragments.
 * Only ONE LotusPageFragment can use this manager at a time (per activity).
 */
class ToolbarDelegate(private val toolbar: Toolbar, private val navigationViewDelegate: NavigationViewDelegate, private val supportActionBar: ActionBar) {
    companion object {
        const val PROGRESS_HAMBURGER = 0.0f
        const val PROGRESS_ARROW = 1.0f
    }

    var toolbarState: ToolbarVisibilityState =
        ToolbarVisibilityState.VISIBLE
        private set
    private val invisibleAnimator = ObjectAnimator.ofFloat(toolbar, "alpha", 0.0F)
    private val visibleAnimator = ObjectAnimator.ofFloat(toolbar, "alpha", 1.0F)

    /**
     * GONE - The Toolbar is off the top of the screen and not accessible to a user.
     * INVISIBLE - The toolbar still holds the space it occupies, but the user cannot access it (alpha 0)
     * VISIBLE - Normal toolbar.
     */
    enum class ToolbarVisibilityState {
        GONE, VISIBLE, INVISIBLE
    }

    /**
     * Immediately cancel any animations and set the toolbar state.
     */
    internal fun setToolbarVisibilityState(desiredState: ToolbarVisibilityState) {
        toolbar.clearAnimation()
        invisibleAnimator.cancel()
        visibleAnimator.cancel()
        when (toolbarState) {
            ToolbarVisibilityState.VISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    supportActionBar.hide()
                    toolbar.translationY = -toolbar.height.toFloat()
                    toolbar.alpha = 1.0F
                }
                else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    toolbar.alpha = 0.0F
                    supportActionBar.show()
                    toolbar.visibility = View.INVISIBLE
                    toolbar.translationY = 0F
                }
            }
            ToolbarVisibilityState.GONE -> {
                if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    toolbar.alpha = 1.0F
                    supportActionBar.show()
                    toolbar.translationY = 0F
                } else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    toolbar.alpha = 0.0F
                    supportActionBar.show()
                    toolbar.visibility = View.INVISIBLE
                    toolbar.translationY = 0F
                }
            }
            ToolbarVisibilityState.INVISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    supportActionBar.hide()
                    toolbar.translationY = -toolbar.height.toFloat()
                    toolbar.visibility = View.VISIBLE
                    toolbar.alpha = 1.0F
                } else if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    toolbar.alpha = 1.0F
                    toolbar.visibility = View.VISIBLE
                    supportActionBar.show()
                    toolbar.translationY = 0F
                }
            }
        }
        toolbarState = desiredState
    }

    /**
     * Immediately set the toolbar state to the desired state, and start an animation.
     */
    internal fun animateToToolbarVisibilityState(desiredState: ToolbarVisibilityState, animationDurationMS: Long) {
        when (toolbarState) {
            ToolbarVisibilityState.VISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    toolbar.animate().setDuration(animationDurationMS).translationY(-toolbar.height.toFloat()).withEndAction {
                        toolbar.alpha = 1.0F
                        supportActionBar.hide()
                    }
                }
                else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    supportActionBar.show()
                    toolbar.translationY = 0F
                    invisibleAnimator.duration = animationDurationMS
                    invisibleAnimator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            toolbar.visibility = View.INVISIBLE
                            invisibleAnimator.removeListener(this)
                        }
                    })
                    invisibleAnimator.start()
                }
            }
            ToolbarVisibilityState.GONE -> {
                if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    toolbar.alpha = 1.0F
                    supportActionBar.show()
                    toolbar.animate().translationY(0F)
                } else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    toolbar.alpha = 0.0F
                    supportActionBar.show()
                    toolbar.visibility = View.INVISIBLE
                    toolbar.animate().translationY(0F)
                }
            }
            ToolbarVisibilityState.INVISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    toolbar.animate().setDuration(animationDurationMS).translationY(-toolbar.height.toFloat()).withEndAction {
                        toolbar.alpha = 1.0F
                        toolbar.visibility = View.VISIBLE
                        supportActionBar.hide()
                    }
                } else if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    supportActionBar.show()
                    toolbar.translationY = 0F
                    toolbar.visibility = View.VISIBLE
                    visibleAnimator.duration = animationDurationMS
                    visibleAnimator.start()
                }
            }
        }
        toolbarState = desiredState
    }

    internal fun setToolbarTitle(title: CharSequence) {
        supportActionBar.title = title
    }

    internal fun setOverrideUpNavigationToRoot(overrideUp: Boolean) {
        navigationViewDelegate.updateUpNavigation(overrideUp)
    }

    internal fun updateNavViewVisibility(visible: Boolean) {
        navigationViewDelegate.updateNavViewVisibility(visible)
    }
}