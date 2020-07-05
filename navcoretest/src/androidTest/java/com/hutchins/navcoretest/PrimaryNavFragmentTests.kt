/*******************************************************************************
 * Copyright 2020 Joseph Hutchins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.hutchins.navcoretest

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Created by joeyhutchins on 7/5/20.
 */
class PrimaryNavFragmentTests {
    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testFirstFragmentInitialized() {
        Espresso.onView(ViewMatchers.withId(R.id.firstFragmentButton))

        val firstFragment = activityRule.activity.supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments[0] as FirstFragment

        firstFragment.assertNavigationActivityReference()
        Assert.assertEquals(1, firstFragment.numOnStartCalled)
        Assert.assertEquals(0, firstFragment.numOnStopCalled)
    }

    @Test
    fun testActivityCallbacksWithNavigations() {
        Espresso.onView(ViewMatchers.withId(R.id.firstFragmentButton))

        val firstFragment = activityRule.activity.supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments[0] as FirstFragment

        firstFragment.assertNavigationActivityReference()
        Assert.assertEquals(1, firstFragment.numOnStartCalled)
        Assert.assertEquals(0, firstFragment.numOnStopCalled)

        Espresso.onView(ViewMatchers.withId(R.id.firstFragmentButton)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.secondFragmentButton))

        val secondFragment = activityRule.activity.supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments[0] as SecondFragment

        secondFragment.assertNavigationActivityReference()
        Assert.assertEquals(1, firstFragment.numOnStopCalled)
        Assert.assertEquals(1, secondFragment.numOnStartCalled)
        Assert.assertEquals(0, secondFragment.numOnStopCalled)

        Espresso.onView(ViewMatchers.withId(R.id.secondFragmentButton)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.thirdFragmentButton))

        val thirdFragment = activityRule.activity.supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments[0] as ThirdFragment

        thirdFragment.assertNavigationActivityReference()
        Assert.assertEquals(1, secondFragment.numOnStopCalled)
        Assert.assertEquals(1, thirdFragment.numOnStartCalled)
        Assert.assertEquals(0, thirdFragment.numOnStopCalled)

        Espresso.onView(ViewMatchers.withId(R.id.thirdFragmentButton)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.fourthFragmentButton))

        val fourthFragment = activityRule.activity.supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments[0] as FourthFragment

        fourthFragment.assertNavigationActivityReference()
        Assert.assertEquals(1, thirdFragment.numOnStopCalled)
        Assert.assertEquals(1, fourthFragment.numOnStartCalled)
        Assert.assertEquals(0, fourthFragment.numOnStopCalled)

        Espresso.onView(ViewMatchers.withId(R.id.fourthFragmentButton)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.fifthFragmentTextView))

        val fifthFragment = activityRule.activity.supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments[0] as FifthFragment

        fifthFragment.assertNavigationActivityReference()
        Assert.assertEquals(1, fourthFragment.numOnStopCalled)
        Assert.assertEquals(1, fifthFragment.numOnStartCalled)
        Assert.assertEquals(0, fifthFragment.numOnStopCalled)

        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.fourthFragmentButton))

        Assert.assertEquals(1, fifthFragment.numOnStopCalled)
        Assert.assertEquals(2, fourthFragment.numOnStartCalled)
        Assert.assertEquals(1, fourthFragment.numOnStopCalled)

        fourthFragment.setOverrideUp()
        activityRule.activity.onSupportNavigateUp()

        Espresso.onView(ViewMatchers.withId(R.id.fourthFragmentButton))

        // nothing should change.
        Assert.assertEquals(1, fifthFragment.numOnStopCalled)
        Assert.assertEquals(2, fourthFragment.numOnStartCalled)
        Assert.assertEquals(1, fourthFragment.numOnStopCalled)

        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.thirdFragmentButton))

        Assert.assertEquals(2, fourthFragment.numOnStopCalled)
        Assert.assertEquals(2, thirdFragment.numOnStartCalled)
        Assert.assertEquals(1, thirdFragment.numOnStopCalled)

        thirdFragment.setOverrideBack()
        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.thirdFragmentButton))

        // nothing should change
        Assert.assertEquals(2, fourthFragment.numOnStopCalled)
        Assert.assertEquals(2, thirdFragment.numOnStartCalled)
        Assert.assertEquals(1, thirdFragment.numOnStopCalled)

        thirdFragment.clearBackOverride()
        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.secondFragmentButton))

        Assert.assertEquals(2, thirdFragment.numOnStopCalled)
        Assert.assertEquals(2, secondFragment.numOnStartCalled)
        Assert.assertEquals(1, secondFragment.numOnStopCalled)

        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.firstFragmentButton))

        Assert.assertEquals(2, secondFragment.numOnStopCalled)
        Assert.assertEquals(2, firstFragment.numOnStartCalled)
        Assert.assertEquals(1, firstFragment.numOnStopCalled)
    }
}