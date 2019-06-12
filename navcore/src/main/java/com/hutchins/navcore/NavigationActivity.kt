@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.hutchins.navcore

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import java.lang.ref.WeakReference

/**
 * NavigationActivity defines a Navigation pattern that allows for safe access of Navigation lifecycle events to a Fragment.
 * This Activity maintains a state machine that keeps track of Fragment transactions within the [NavController] and provides
 * a lifecycle of these navigation transitions through methods calls [BaseNavFragment][setCurrentBaseNavFragment] and
 * [BaseNavFragment][onRemoveAsCurrentNavFragment].
 *
 *
 * <p>This Activity must be used with the Navigation Architecture component and children must provide the resource Id to
 * the navigation graph. All [NavDestination]s within the navigation graph must be children of [BaseNavFragment] as that is
 * the only way we can keep track of the navigation backstack state machine.</p>
 *
 * <p> The Navigation lifecycle guarantees that only one [BaseNavFragment] will be inside the [onCurrentNavFragment] lifecycle
 * at a time. This is useful to know for many reasons, one common one is synchronization around a Fragment's access to a
 * "Navigation View" held by the [Activity]. Generally, a toolbar is held at the Activity level, but a Fragment may want
 * to change the title or other features. Common bugs can occur if the Fragment attempts to do this at invalid times close to
 * Lifecycle transitions. This pattern explicitly defines that so a Fragment can know when it is safe to do these operations.</p>
 *
 * <p> This Navigation pattern also provides convenient means for a [BaseNavFragment] to register for callbacks on common
 * navigation events like [onBackPressed] and [onSupportNavigateUp] and optionally handle those events themselves. See
 * [UpButtonOverrideProvider] and [BackButtonOverrideProvider].</p>
 */
abstract class NavigationActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    companion object {
        const val TAG = "NavCore"
    }
    protected lateinit var navController: NavController

    /**
     * Called during [AppCompatActivity][onCreate]. A child must provide a resource id to a [FrameLayout] that is
     * the "navigation host" view for [BaseNavFragment]'s.
     *
     * A Common layout pattern for an Activity is to have a Toolbar and a Frame for Fragments to be inflated into. The
     * Navigation Architecture component promotes this as well. This Base [AppCompatActivity] needs to know where to inflate
     * the [NavHostFragment].
     */
    abstract val navigationHostResourceId: Int

    /**
     * Called during Called during [AppCompatActivity][onCreate]. A child must provide the reference to the navigation graph.
     *
     * Note navigation graphs should only have [NavDestination]s that inherit [BaseNavFragment].
     */
    abstract val navigationGraphResourceId: Int

    /**
     * Called during Called during [AppCompatActivity][onCreate]. A child must inflate the activity's basic layout and
     * provide references to the navigation graph resource id and a [FrameLayout] where the [NavHostFragment] will be inflated.
     * Those two resources are provided in separate calls that will be invoked during [onCreate]. See [navigationGraphResourceId] and
     * [navigationHostResourceId]
     */
    abstract fun onSetContentView()

    /**
     * Called during Called during [AppCompatActivity][onCreate]. This will be called at the end of [onCreate] providing
     * a reference to the [NavController] this activity will use during its lifetime.
     */
    open fun onNavigationInitialized(navController: NavController) {

    }

    /**
     * Called on transitions of the [NavigationActivity]s internal state machine, providing a reference to the currently
     * active [BaseNavFragment]
     *
     * @param baseNavFragment The currently active BaseNavFragment.
     */
    open fun onNavigated(baseNavFragment: BaseNavFragment) {

    }

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
        } else {
            val navHostFragment = supportFragmentManager.findFragmentById(navigationHostResourceId) as NavHostFragment

            onCreateInternal(navHostFragment)
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
     * Should ONLY be called by a BaseNavFragment when the view is created.
     *
     * The synchronization of Fragment lifecycles with navigation lifecycles is delicate. A Fragment might be created and attached
     * before or after the onDestinationChanged callback occurs in the NavController. Empirically, I have gathered this behavior as it differs
     * between forward, back, and up navigation events. Not all events are equal.
     */
    internal fun registerBaseNavFragment(baseNavFragment: BaseNavFragment) {
        baseNavFragments.add(WeakReference(baseNavFragment))
        Log.i(TAG, "Registering baseNavFragment. Current size (including this): " + baseNavFragments.size)

        // Only trim and apply the current Fragment if this is a normal navigation. In a forward navigation or up navigation,
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

    /**
     * Let's avoid memory leaks and only hold references to Fragments that are active.
     */
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
     * lifecycle. This was done in order to properly manage providing a BaseNavFragment with it's navigation
     * information bundle parameters so it can potentially customize its view, as well as allow for
     * inheritors to define a means to let a BaseNavFragment that it alone is the current destination.
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
}

/**
 * Config class returned by [BaseNavFragment] when the [NavigationActivity] has notified it that it is the active
 * Fragment in the navigation. These providers are overrides a Fragments can optionally set to receive callbacks
 * when up or back events occur.
 */
open class NavigationConfig(val backButtonOverrideProvider: BackButtonOverrideProvider, val upButtonOverrideProvider: UpButtonOverrideProvider)

interface NavigationOverrideClickListener {
    fun onClick(): Boolean
}

class BackButtonOverrideProvider {
    private var backButtonOverride: NavigationOverrideClickListener? = null

    /**
     * @return true if this is going to be handled, otherwise false. If false, the Activity will proceed as usual with back
     * events.
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
     * @return true if this is going to be handled, otherwise false. If false, the Activity will proceed as usual with up events.
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
