@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.hutchins.navui.jetpack

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.hutchins.navui.R
import com.hutchins.navui.core.NavViewDelegate

/**
 * This class manages the Toolbar and maintains the state of it with respect to ScreenFragments.
 */
class JetpackToolbarDelegate(
    private val constraintLayout: ConstraintLayout,
    private val toolbarContainer: View,
    private val toolbar: Toolbar,
    private val navViewDelegate: NavViewDelegate,
    private val upVisibilityHandler: UpVisibilityHandler
) {
    companion object {
        const val PROGRESS_HAMBURGER = 0.0f
        const val PROGRESS_ARROW = 1.0f

        private const val BUNDLE_KEY_TOOLBAR_VISIBILITY_STATE = "BUNDLE_KEY_TOOLBAR_VISIBILITY_STATE"
        private const val BUNDLE_KEY_TOOLBAR_TITLE = "BUNDLE_KEY_TOOLBAR_TITLE"
        private const val BUNDLE_KEY_TOOLBAR_SUBTITLE = "BUNDLE_KEY_TOOLBAR_SUBTITLE"

        private const val BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_VISIBLE = 0
        private const val BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_INVISIBLE = 1
        private const val BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_GONE = 2
    }

    internal fun saveState(bundle: Bundle) {
        bundle.putString(BUNDLE_KEY_TOOLBAR_TITLE, toolbar.title.toString())
        bundle.putString(BUNDLE_KEY_TOOLBAR_SUBTITLE, toolbar.subtitle.toString())
        val intState = when (toolbarState) {
            ToolbarVisibilityState.VISIBLE -> BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_VISIBLE
            ToolbarVisibilityState.INVISIBLE -> BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_INVISIBLE
            ToolbarVisibilityState.GONE -> BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_GONE
        }
        bundle.putInt(BUNDLE_KEY_TOOLBAR_VISIBILITY_STATE, intState)
    }

    internal fun restoreState(bundle: Bundle) {
        toolbar.title = bundle.getString(BUNDLE_KEY_TOOLBAR_TITLE)
        toolbar.subtitle = bundle.getString(BUNDLE_KEY_TOOLBAR_SUBTITLE)
        val toolbarState = when (bundle.getInt(BUNDLE_KEY_TOOLBAR_VISIBILITY_STATE)) {
            BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_VISIBLE -> ToolbarVisibilityState.VISIBLE
            BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_INVISIBLE -> ToolbarVisibilityState.INVISIBLE
            BUNDLE_VAL_TOOLBAR_VISIBILITY_STATE_GONE -> ToolbarVisibilityState.GONE
            else -> throw IllegalStateException("Unknown toolbar visibility state restored!")
        }
        setToolbarVisibilityState(toolbarState)
    }

    var toolbarState: ToolbarVisibilityState =
        ToolbarVisibilityState.VISIBLE
        private set
    private val invisibleAnimator = ObjectAnimator.ofFloat(toolbarContainer, "alpha", 0.0F)
    private val visibleAnimator = ObjectAnimator.ofFloat(toolbarContainer, "alpha", 1.0F)

    private val constraintSetToolbarVisible: ConstraintSet = ConstraintSet().apply {
        clone(constraintLayout)
        connect(
            R.id.toolbarLayout, ConstraintSet.TOP,
            R.id.constraintActivityContentLayout, ConstraintSet.TOP, 0)
        connect(
            R.id.toolbarLayout, ConstraintSet.BOTTOM,
            R.id.navHost, ConstraintSet.TOP, 0)
        setVisibility(R.id.toolbarLayout, View.VISIBLE)
    }
    private val constraintSetToolbarGone: ConstraintSet = ConstraintSet().apply {
        clone(constraintLayout)
        clear(R.id.toolbarLayout, ConstraintSet.TOP)
        connect(
            R.id.toolbarLayout, ConstraintSet.BOTTOM,
            R.id.constraintActivityContentLayout, ConstraintSet.TOP, 0)
        // We don't use View.GONE because it has undesirable animations by default. It first sets invisible THEN goes to GONE.
        // We want this to be a more or less instant transition.
        //setVisibility(R.id.toolbarLayout, View.GONE)
    }
    private val constraintSetToolbarInvisible: ConstraintSet = ConstraintSet().apply {
        clone(constraintLayout)
        connect(
            R.id.toolbarLayout, ConstraintSet.TOP,
            R.id.constraintActivityContentLayout, ConstraintSet.TOP, 0)
        connect(
            R.id.toolbarLayout, ConstraintSet.BOTTOM,
            R.id.navHost, ConstraintSet.TOP, 0)
        setVisibility(R.id.toolbarLayout, View.INVISIBLE)
    }

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
        when (toolbarState) {
            ToolbarVisibilityState.VISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    animateConstraintSet(0L, constraintSetToolbarGone)
                }
                else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    animateConstraintSet(0L, constraintSetToolbarInvisible)
                }
            }
            ToolbarVisibilityState.GONE -> {
                if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    animateConstraintSet(0L, constraintSetToolbarVisible)
                } else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    animateConstraintSet(0L, constraintSetToolbarInvisible)
                }
            }
            ToolbarVisibilityState.INVISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    animateConstraintSet(0L, constraintSetToolbarGone)
                } else if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    animateConstraintSet(0L, constraintSetToolbarVisible)
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
                    animateConstraintSet(animationDurationMS, constraintSetToolbarGone)
                }
                else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    animateConstraintSet(animationDurationMS, constraintSetToolbarInvisible)
                }
            }
            ToolbarVisibilityState.GONE -> {
                if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    animateConstraintSet(animationDurationMS, constraintSetToolbarVisible)
                } else if (desiredState == ToolbarVisibilityState.INVISIBLE) {
                    // This is a workaround to speed up the animation. The constraint layout wants to do them one at a time.
                    toolbarContainer.visibility = View.INVISIBLE
                    animateConstraintSet(animationDurationMS, constraintSetToolbarInvisible)
                }
            }
            ToolbarVisibilityState.INVISIBLE -> {
                if (desiredState == ToolbarVisibilityState.GONE) {
                    animateConstraintSet(animationDurationMS, constraintSetToolbarGone)
                } else if (desiredState == ToolbarVisibilityState.VISIBLE) {
                    animateConstraintSet(animationDurationMS, constraintSetToolbarVisible)
                }
            }
        }
        toolbarState = desiredState
    }

    internal fun setToolbarTitle(title: CharSequence) {
        toolbar.title = title
    }

    internal fun setToolbarSubtitle(subTitle: CharSequence) {
        toolbar.subtitle = subTitle
    }

    internal fun clearToolbarActionMenu() {
        toolbar.menu.clear()
        toolbar.setOnMenuItemClickListener(null)
    }

    internal fun setToolbarActionMenu(menuResId: Int, listener: Toolbar.OnMenuItemClickListener) {
        toolbar.menu.clear()
        toolbar.inflateMenu(menuResId)
        toolbar.setOnMenuItemClickListener(listener)
    }

    internal fun setUpNavigationVisible(visible: Boolean) {
        upVisibilityHandler.setUpNavigationVisible(visible)
    }

    private fun animateConstraintSet(durationMs: Long, constraintSet: ConstraintSet) {
        val transition = AutoTransition()
        transition.duration = durationMs
        TransitionManager.beginDelayedTransition(constraintLayout, transition)
        constraintSet.applyTo(constraintLayout)
    }

    interface UpVisibilityHandler {
        fun setUpNavigationVisible(visible: Boolean)
    }
}