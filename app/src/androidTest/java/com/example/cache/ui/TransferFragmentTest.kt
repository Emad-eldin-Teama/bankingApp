package com.example.cache.ui

import android.widget.Spinner
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.cache.R
import com.example.cache.data.db.AppDatabase
import com.example.cache.di.component.DaggerAppComponent
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class TransferFragmentTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun resetDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("cache_database")
    }

    @Test
    fun navigateToTransferFragmentTest() {
        val navigateTransferButton = onView(withId(R.id.fab))
        navigateTransferButton.check(matches(isDisplayed()))
        navigateTransferButton.perform(click())

        val navController = mActivityTestRule.activity.findNavController(R.id.nav_host_fragment)
        assertEquals(R.id.navigation_transfer, navController.currentDestination?.id)
    }

    @Test
    fun verifyTransferFragmentComponentsTest() {
        onView(withId(R.id.fab)).perform(click())

        onView(withId(R.id.label_amount)).check(
            matches(allOf(isDisplayed(), withText("Amount")))
        )

        onView(withId(R.id.label_from)).check(
            matches(allOf(isDisplayed(), withText("From")))
        )

        onView(withId(R.id.label_to)).check(
            matches(allOf(isDisplayed(), withText("To")))
        )

        onView(withId(R.id.transfer_button)).check(matches(isDisplayed()))
        onView(withId(R.id.amount_field)).check(matches(isDisplayed()))
        onView(withId(R.id.from_account_field)).check(matches(isDisplayed()))
        onView(withId(R.id.to_account_field)).check(matches(isDisplayed()))

        val fromAccountSpinner: Spinner = mActivityTestRule.activity.findViewById(R.id.from_account_field)
        val fromAccountSpinnerItemCount = fromAccountSpinner.adapter?.count
        assertTrue(fromAccountSpinnerItemCount != null && fromAccountSpinnerItemCount > 0)

        val toAccountSpinner: Spinner = mActivityTestRule.activity.findViewById(R.id.to_account_field)
        val toAccountSpinnerItemCount = toAccountSpinner.adapter?.count
        assertTrue(toAccountSpinnerItemCount != null && toAccountSpinnerItemCount > 0)
    }

    @Test
    fun performTransferTransactionTest() {
        onView(withId(R.id.fab)).perform(click())

        val amountField = onView(withId(R.id.amount_field))
        amountField.perform(replaceText("100.50"), closeSoftKeyboard())

        val fromAccountField = onView(withId(R.id.from_account_field))
        fromAccountField.perform(click())

        val fromAccountFieldSelectedItem = onData(anything()).atPosition(0)
        fromAccountFieldSelectedItem.perform(click())

        val toAccountField = onView(withId(R.id.to_account_field))
        toAccountField.perform(click())

        val toAccountSelectedItem = onData(anything()).atPosition(1)
        toAccountSelectedItem.perform(click())

        val transferButton = onView(withId(R.id.transfer_button))
        transferButton.perform(click())

        onView(withText("Transfer confirmation")).check(matches(isDisplayed()))
        val confirmButton = onView(withId(android.R.id.button1))
        confirmButton.check(matches(isDisplayed()))
        confirmButton.perform(click())

        onView(withText("Transfer completed")).check(matches(isDisplayed()))
    }
}
