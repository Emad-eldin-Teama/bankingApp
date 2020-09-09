package com.example.cache.ui

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.cache.R
import com.example.cache.data.db.AppDatabase
import org.hamcrest.Matchers.allOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class AccountsFragmentTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun resetDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("cache_database")
    }

    @Test
    fun navigateToAccountsFragmentTest() {
        val accountsBottomNavigation = onView(withId(R.id.navigation_accounts))
        accountsBottomNavigation.check(matches(isDisplayed()))
        accountsBottomNavigation.perform(click())

        val navController = mActivityTestRule.activity.findNavController(R.id.nav_host_fragment)
        assertEquals(R.id.navigation_accounts, navController.currentDestination?.id)
    }

    @Test
    fun verifyAccountsFragmentComponentsTest() {
        val accountsBottomNavigation = onView(withId(R.id.navigation_accounts))
        accountsBottomNavigation.check(matches(isDisplayed()))
        accountsBottomNavigation.perform(click())

        onView(withId(R.id.title_accounts)).check(
            matches(allOf(isDisplayed(), withText("Accounts")))
        )

        onView(withId(R.id.heading_banking)).check(
            matches(allOf(isDisplayed(), withText("Banking")))
        )

        onView(withId(R.id.heading_investments)).check(
            matches(allOf(isDisplayed(),withText("Investments")))
        )

        onView(withId(R.id.banking_view)).check(matches(isDisplayed()))
        onView(withId(R.id.investments_view)).check(matches(isDisplayed()))

        val bankingView: RecyclerView = mActivityTestRule.activity.findViewById(R.id.banking_view)
        val bankingViewItemCount = bankingView.adapter?.itemCount
        assertTrue(bankingViewItemCount != null && bankingViewItemCount > 0)

        val investmentsView: RecyclerView = mActivityTestRule.activity.findViewById(R.id.investments_view)
        val investmentsViewItemCount = investmentsView.adapter?.itemCount
        assertTrue(investmentsViewItemCount != null && investmentsViewItemCount > 0)

        val moreButton = onView(withId(R.id.button_more))
        moreButton.check(matches(allOf(isDisplayed(), withContentDescription("More"))))
        moreButton.perform(click())

        onView(withText("Add")).check(matches(isDisplayed()))
        onView(withText("Clear")).check(matches(isDisplayed()))
    }

    @Test
    fun addAccountTest() {
        onView(withId(R.id.navigation_accounts)).perform(click())

        val bankingView: RecyclerView = mActivityTestRule.activity.findViewById(R.id.banking_view)
        val preAddBankingViewItemCount = bankingView.adapter?.itemCount

        onView(withId(R.id.button_more)).perform(click())
        onView(withText("Add")).perform(click())

        val postAddBankingViewItemCount = bankingView.adapter?.itemCount

        assertNotNull(preAddBankingViewItemCount)
        assertNotNull(postAddBankingViewItemCount)
        assertEquals(preAddBankingViewItemCount!! + 1, postAddBankingViewItemCount)
    }

    @Test
    fun clearAccountsTest() {
        onView(withId(R.id.navigation_accounts)).perform(click())

        val bankingView: RecyclerView = mActivityTestRule.activity.findViewById(R.id.banking_view)
        val investmentsView: RecyclerView = mActivityTestRule.activity.findViewById(R.id.investments_view)
        val preClearBankingViewItemCount = bankingView.adapter?.itemCount
        val preClearInvestmentsViewItemCount = investmentsView.adapter?.itemCount

        onView(withId(R.id.button_more)).perform(click())
        onView(withText("Clear")).perform(click())

        val postClearBankingViewItemCount = bankingView.adapter?.itemCount
        val postClearInvestmentsViewItemCount = investmentsView.adapter?.itemCount

        assertNotNull(preClearBankingViewItemCount)
        assertNotNull(preClearInvestmentsViewItemCount)
        assertNotNull(postClearBankingViewItemCount)
        assertNotNull(postClearInvestmentsViewItemCount)

        assertEquals(1, postClearBankingViewItemCount)
        assertEquals(1, postClearInvestmentsViewItemCount)
    }
}
