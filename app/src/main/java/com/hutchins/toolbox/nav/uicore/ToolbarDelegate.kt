package com.hutchins.toolbox.nav.uicore

import android.animation.ObjectAnimator
import androidx.appcompat.app.ActionBar
import com.hutchins.toolbox.nav.uicore.navigationviews.NavigationViewDelegate

/**
 * This class manages the Toolbar and maintaints the state of it with respect to LotusPageFragments.
 * Only ONE LotusPageFragment can use this manager at a time (per activity).
 */
class ToolbarDelegate(private val navigationViewDelegate: NavigationViewDelegate, private val supportActionBar: ActionBar) {
    companion object {
        const val PROGRESS_HAMBURGER = 0.0f
        const val PROGRESS_ARROW = 1.0f
    }

    var toolbarState: ToolbarVisibilityState =
        ToolbarVisibilityState.VISIBLE
        private set
    private val invisibleAnimator = ObjectAnimator.ofFloat(navigationViewDelegate.getToolbar(), "alpha", 0.0F)
    private val visibleAnimator = ObjectAnimator.ofFloat(navigationViewDelegate.getToolbar(), "alpha", 1.0F)

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
        navigationViewDelegate.getToolbar().clearAnimation()
        invisibleAnimator.cancel()
        visibleAnimator.cancel()
        when (toolbarState) {
            ToolbarVisibilityState.VISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    supportActionBar.hide()
                    navigationViewDelegate.getToolbar().translationY = -navigationViewDelegate.getToolbar().height.toFloat()
                    navigationViewDelegate.getToolbar().alpha = 1.0F
                    navigationViewDelegate.getToolbar().isEnabled = true
                }
                else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    navigationViewDelegate.getToolbar().alpha = 0.0F
                    navigationViewDelegate.getToolbar().isEnabled = false
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().translationY = 0F
                }
            }
            ToolbarVisibilityState.GONE -> {
                if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    navigationViewDelegate.getToolbar().alpha = 1.0F
                    navigationViewDelegate.getToolbar().isEnabled = true
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().translationY = 0F
                } else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    navigationViewDelegate.getToolbar().alpha = 0.0F
                    navigationViewDelegate.getToolbar().isEnabled = false
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().translationY = 0F
                }
            }
            ToolbarVisibilityState.INVISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    navigationViewDelegate.getToolbar().isEnabled = true
                    supportActionBar.hide()
                    navigationViewDelegate.getToolbar().translationY = -navigationViewDelegate.getToolbar().height.toFloat()
                    navigationViewDelegate.getToolbar().alpha = 1.0F
                } else if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    navigationViewDelegate.getToolbar().isEnabled = true
                    navigationViewDelegate.getToolbar().alpha = 1.0F
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().translationY = 0F
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
                    navigationViewDelegate.getToolbar().animate().setDuration(animationDurationMS).translationY(-navigationViewDelegate.getToolbar().height.toFloat()).withEndAction {
                        navigationViewDelegate.getToolbar().alpha = 1.0F
                        navigationViewDelegate.getToolbar().isEnabled = true
                        supportActionBar.hide()
                    }
                }
                else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    navigationViewDelegate.getToolbar().isEnabled = false
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().translationY = 0F
                    invisibleAnimator.duration = animationDurationMS
                    invisibleAnimator.start()
                }
            }
            ToolbarVisibilityState.GONE -> {
                if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    navigationViewDelegate.getToolbar().alpha = 1.0F
                    navigationViewDelegate.getToolbar().isEnabled = true
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().animate().translationY(0F)
                } else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    navigationViewDelegate.getToolbar().alpha = 0.0F
                    navigationViewDelegate.getToolbar().isEnabled = false
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().animate().translationY(0F)
                }
            }
            ToolbarVisibilityState.INVISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    navigationViewDelegate.getToolbar().animate().setDuration(animationDurationMS).translationY(-navigationViewDelegate.getToolbar().height.toFloat()).withEndAction {
                        navigationViewDelegate.getToolbar().isEnabled = true
                        navigationViewDelegate.getToolbar().alpha = 1.0F
                        supportActionBar.hide()
                    }
                } else if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    navigationViewDelegate.getToolbar().isEnabled = true
                    supportActionBar.show()
                    navigationViewDelegate.getToolbar().translationY = 0F
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