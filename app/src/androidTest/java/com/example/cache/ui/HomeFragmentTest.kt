package com.example.cache.ui


import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.cache.R
import com.example.cache.data.db.AppDatabase
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class HomeFragmentTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun resetDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("cache_database")
    }

    @Test
    fun navigateToHomeFragmentTest() {
        val homeBottomNavigation = onView(withId(R.id.navigation_home))
        homeBottomNavigation.check(matches(isDisplayed()))
        homeBottomNavigation.perform(ViewActions.click())

        val navController = mActivityTestRule.activity.findNavController(R.id.nav_host_fragment)
        assertEquals(R.id.navigation_home, navController.currentDestination?.id)
    }

    @Test
    fun verifyHomeFragmentComponentsTest() {
        onView(withId(R.id.navigation_home)).perform(ViewActions.click())

        onView(withId(R.id.home_greeting)).check(
            matches(allOf(isDisplayed(), notNullValue()))
        )

        onView(withId(R.id.heading_accounts_overview)).check(
            matches(allOf(isDisplayed(), withText("Accounts Overview")))
        )

        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        onView(withId(R.id.button_more)).check(matches(isDisplayed()))

        val accountsOverviewView: RecyclerView = mActivityTestRule.activity.findViewById(R.id.accounts_overview_view)
        val accountsOverviewItemCount = accountsOverviewView.adapter?.itemCount
        assertTrue(accountsOverviewItemCount != null && accountsOverviewItemCount > 0)
    }

}
