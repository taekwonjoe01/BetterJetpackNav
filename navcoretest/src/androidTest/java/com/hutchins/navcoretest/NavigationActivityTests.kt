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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Created by joeyhutchins on 7/3/20.
 */
class NavigationActivityTests {
    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testActivityInitCallbacks() {
        onView(withId(R.id.navHost))

        activityRule.activity.assertReferenceToNavController()
        Assert.assertEquals(1, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)
    }

    @Test
    fun testActivityCallbacksWithNavigations() {
        onView(withId(R.id.firstFragmentButton))

        Assert.assertEquals(1, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        onView(withId(R.id.firstFragmentButton)).perform(click())

        onView(withId(R.id.secondFragmentButton))

        Assert.assertEquals(2, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(2, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        onView(withId(R.id.secondFragmentButton)).perform(click())

        onView(withId(R.id.thirdFragmentButton))

        Assert.assertEquals(3, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(3, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        onView(withId(R.id.thirdFragmentButton)).perform(click())

        onView(withId(R.id.fourthFragmentButton))

        Assert.assertEquals(4, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(4, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        onView(withId(R.id.fourthFragmentButton)).perform(click())

        onView(withId(R.id.fifthFragmentTextView))

        Assert.assertEquals(5, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(5, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        pressBack()

        onView(withId(R.id.fourthFragmentButton))

        Assert.assertEquals(6, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(6, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        pressBack()

        onView(withId(R.id.thirdFragmentButton))

        Assert.assertEquals(7, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(7, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        pressBack()

        onView(withId(R.id.secondFragmentButton))

        Assert.assertEquals(8, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(8, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)

        pressBack()

        onView(withId(R.id.firstFragmentButton))

        Assert.assertEquals(9, activityRule.activity.numAfterNavigatedCalls)
        Assert.assertEquals(9, activityRule.activity.numBeforeNavigatedCalls)
        Assert.assertEquals(1, activityRule.activity.numPrimaryNavInitializedCalls)
    }
}
