package com.example.cache.ui.home

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.cache.data.repository.AccountRepository
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class HomeViewModelTest {

    private lateinit var homeViewModel: HomeViewModel
    private val accountRepository: AccountRepository = mock()

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        homeViewModel = HomeViewModel(accountRepository, application)
    }

    @Test
    fun `gets correct greeting message`() {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 8)
        assertEquals("Good morning,", homeViewModel.getGreetingMessage(calendar))

        calendar.set(Calendar.HOUR_OF_DAY, 12)
        assertEquals("Good afternoon,", homeViewModel.getGreetingMessage(calendar))

        calendar.set(Calendar.HOUR_OF_DAY, 18)
        assertEquals("Good evening,", homeViewModel.getGreetingMessage(calendar))

        calendar.set(Calendar.HOUR_OF_DAY, 21)
        assertEquals("Good night,", homeViewModel.getGreetingMessage(calendar))
    }

}