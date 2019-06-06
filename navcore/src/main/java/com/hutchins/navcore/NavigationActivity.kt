@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.hutchins.navcore

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import java.lang.ref.WeakReference

abstract class NavigationActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    companion object {
        const val TAG = "NavCore"
    }
    protected lateinit var navController: NavController

    /**
     * Called during onCreate.
     */
    abstract val navigationHostResourceId: Int
    /**
     * Called during onCreate.
     */
    abstract val navigationGraphResourceId: Int
    /**
     * Called during onCreate.
     */
    abstract fun onSetContentView()
    /**
     * Called during onCreate.
     */
    open fun onNavigationInitialized(navController: NavController) {

    }

    open fun onNavigated(baseNavFragment: BaseNavFragment) {

    }

    /**
     * Get the navigation graph for this activity. This will be called during the NavigationActivity's
     * onCreate method.
     */

    private val baseNavFragments = ArrayList<WeakReference<BaseNavFragment>>()
    private var currentNavigationConfig: NavigationConfig? = null
    /**
     * This flag is used to distinguish a hardware back button navigation from the rest. This is needed
     * because hardware back button navigation has a different lifecycle than all others (normal forward
     * navigations and UP button navigations). The back button pops the fragment BEFORE onNavigated is called
     * however the other navigation methods generally call onNavigated first, and THEN a fragment transaction
     * occurs. We use this flag to distinguish the differences so we can synchronize appropriately.
     */
    private var isHardwareBackNavigation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val navHostFragment = NavHostFragment.create(navigationGraphResourceId)

            onCreateInternal(navHostFragment)

//        threshold = resources.getDimension(R.dimen.motionThresholdForEditTextDismiss)
        } else {
            val navHostFragment = supportFragmentManager.findFragmentById(navigationHostResourceId) as NavHostFragment

            onCreateInternal(navHostFragment)

//        threshold = resources.getDimension(R.dimen.motionThresholdForEditTextDismiss)
        }
    }

    private fun onCreateInternal(navHostFragment: NavHostFragment) {
        // Set the layout view into our window.
        onSetContentView()
        // Immediately and synchronously insert the NavHostFragment into our navHost section of the UI.
        supportFragmentManager.beginTransaction()
            .replace(navigationHostResourceId, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment) // this is the equivalent to app:defaultNavHost="true"
            .commitNow()
        navController = navHostFragment.navController
        // If the view Delegate wants to connect the Navigation Controller to its view, it can do so
        // in this call. Generally a side nav will want to connect, also an ActionBar.
        onNavigationInitialized(navController)
        navController.addOnDestinationChangedListener(this)
    }

    /**
     * Should ONLY be called by a PageFragment when the view is created.
     */
    internal fun registerBaseNavFragment(baseNavFragment: BaseNavFragment) {
        baseNavFragments.add(WeakReference(baseNavFragment))
        Log.i(TAG, "Registering baseNavFragment. Current size (including this): " + baseNavFragments.size)

        // Only trim and apply the current Fragment if this is a normal navigation. In a normal navigation,
        // onNavigated would have already been called, and therefore the NavDestination is available now.
        // On a HardwareBackNavigation, we have not yet been given the NavDestination object, so we must
        // wait to synchronize.

        // There is also another unique navigation, one where a new fragment is made, but no onNavigated
        // call will happen. This occurs when menu items are reselected when already on that screen.
        // The behavior is to "restart" the screen. We must allow the new fragment to become the
        // current PageFragment with the same NavDirections object.

        // NOTE: If this is a replacement PageFragment transaction with NO onNavigated call, the
        // parameters saved in the NavDirections bundle will go to the new Fragment. So if a title was
        // changed, or visibility state altered, these changes persist to the new instance of the
        // fragment.
        if (!isHardwareBackNavigation) {
            trimFragments()
            setCurrentBaseNavFragment()
        }
    }

    private fun trimFragments() {
        if (baseNavFragments.size < 2) {
            Log.w(TAG, "trimFragments called when none will be trimmed.")
        }
        while (baseNavFragments.size > 1) {
            val pageFragment = baseNavFragments.removeAt(0)
            pageFragment.get()?.apply {
                this.onRemoveAsCurrentNavFragment()
            }
        }
    }

    /**
     * This will throw exception if called at the wrong time. We assert that things are not null when
     * this is called empirically, as the design expects these to be non null here, and if that is
     * ever false, we have a big big problem.
     */
    private fun setCurrentBaseNavFragment() {
        val current = baseNavFragments[0].get()!!
        val navConfig = current.onSetAsCurrentNavFragment(navController.currentDestination!!)
        currentNavigationConfig = navConfig

        onNavigated(current)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return if (maybeDoNavigateUpOverride()) false else super.onSupportNavigateUp()
//    }

    override fun onBackPressed() {
        if (!maybeDoBackButtonOverride()) {
            // Distinguish this navigation.
            isHardwareBackNavigation = true
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (maybeDoNavigateUpOverride()) false else super.onSupportNavigateUp()
    }

    fun maybeDoBackButtonOverride(): Boolean {
        return currentNavigationConfig?.backButtonOverrideProvider?.onBackPressed() ?: false
    }

    fun maybeDoNavigateUpOverride(): Boolean {
        return currentNavigationConfig?.upButtonOverrideProvider?.onSupportNavigateUp() ?: false
    }

    /**
     * Details of Navigation Logic:
     * The lifecycle of navigation is not consistent (at all) with any lifecycle of a Fragment. As a
     * result, custom code has been written to synchronize navigation lifecycle with a BaseNavFragment's
     * lifecycle. This was done in order to properly manage providing a PageFragment with it's navigation
     * information bundle parameters so it can potentially customize its view, as well as allow for
     * inheritors to define a means to let a PageFragment that it alone is the current destination.
     */
    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        // Only trim and apply the current Fragment if this is a hardware back navigation. In a
        // hardware back navigation, registerFragment would have already been called, and therefore
        // the currentPageFragment is available now at the end of the list.
        // On a normal navigation, we have not yet been given the PageFragment object, so we must
        // wait to synchronize.
        if (isHardwareBackNavigation) {
            trimFragments()
            setCurrentBaseNavFragment()

            // Reset the flag
            isHardwareBackNavigation = false
        }
    }

    // TODO: We can reflect on whether it's worth it to make this a delegate instead of in
    // the inheritance. This forces this behavior on any Edittext for any activity on the gen2 platform.
    // Arguably, this is behavior that should always have existed...
//    private var startX: Float = START_X_NOT_TRACKING
//    private var startY: Float = START_Y_NOT_TRACKING
//    private var threshold: Float = SWIPE_EVENT_THRESHOLD_DEFAULT

//    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
//        // This code is attempting to discern when the using is tapping outside an EditText, and
//        // if the keyboard should be closed and focus lost. This is deemed desirable behavior to help
//        // trigger decluttering of the user interface for the user as well as a time to trigger
//        // a validation for InputWithLabels. In this code, it is important to also distinguish
//        // between a user swiping and a user tapping. If the user is swiping (scrolling), we should
//        // not clear the focus and close the keyboard. That is poor user experience.
//        if (event.action == MotionEvent.ACTION_DOWN) {
//            if (startX == START_X_NOT_TRACKING && startY == START_Y_NOT_TRACKING) {
//                // If we are not currently tracking, start tracking. This is the first DOWN event, so
//                // this is the start position.
//                val v = currentFocus
//                if (v is EditText) {
//                    startX = event.rawX
//                    startY = event.rawY
//                }
//            }
//        } else if (event.action == MotionEvent.ACTION_MOVE) {
//            // If we are tracking touches (because the user first did a DOWN event), then make some
//            // additional checks to determine if we're doing a swipe.
//            if (startX != START_X_NOT_TRACKING && startY != START_Y_NOT_TRACKING) {
//                val diffX = Math.abs(startX - event.rawX)
//                val diffY = Math.abs(startY - event.rawY)
//                if (diffX > threshold || diffY > threshold) {
//                    // We have determined here that the user is swiping, and not tapping DOWN, so
//                    // we should stop tracking and not worry about releasing focus on the EditText.
//                    startX = START_X_NOT_TRACKING
//                    startY = START_Y_NOT_TRACKING
//                }
//            }
//        } else if (event.action == MotionEvent.ACTION_UP) {
//            if (startX != START_X_NOT_TRACKING && startY != START_Y_NOT_TRACKING) {
//                // So now the user is releasing their touch. If we reach this code, it is because a
//                // event has not been detected. Here, we want to check if the touch event is intersecting
//                // with the currently focused editText. If it is, there is no need to clear focus on it
//                // but if it's not, then we SHOULD clear focus.
//                val v = currentFocus
//                if (v is EditText) {
//                    val outRect = Rect()
//                    v.getGlobalVisibleRect(outRect)
//                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
//                        v.clearFocus()
//                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//                        imm!!.hideSoftInputFromWindow(v.getWindowToken(), 0)
//                    }
//                    // Reset touch tracking.
//                    startX = START_X_NOT_TRACKING
//                    startY = START_Y_NOT_TRACKING
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event)
//    }
}

open class NavigationConfig(val backButtonOverrideProvider: BackButtonOverrideProvider, val upButtonOverrideProvider: UpButtonOverrideProvider)

interface NavigationOverrideClickListener {
    fun onClick(): Boolean
}

class BackButtonOverrideProvider {
    private var backButtonOverride: NavigationOverrideClickListener? = null

    /**
     * Return true if this is going to be overriden, otherwise false.
     */
    internal fun onBackPressed(): Boolean {
        return if (backButtonOverride != null) {
            backButtonOverride?.onClick() ?: false
        } else {
            false
        }
    }

    fun setBackButtonOverride(listener: NavigationOverrideClickListener?) {
        backButtonOverride = listener
    }
}
class UpButtonOverrideProvider {
    private var upButtonOverride: NavigationOverrideClickListener? = null

    /**
     * Return true if this is going to be overriden, otherwise false.
     */
    internal fun onSupportNavigateUp(): Boolean {
        return if (upButtonOverride != null) {
            upButtonOverride?.onClick() ?: false
        } else {
            false
        }
    }

    fun setUpButtonOverride(listener: NavigationOverrideClickListener?) {
        upButtonOverride = listener
    }
}
