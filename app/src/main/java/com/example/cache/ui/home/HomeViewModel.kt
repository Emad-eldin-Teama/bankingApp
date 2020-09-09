package com.example.cache.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.cache.R
import com.example.cache.data.repository.AccountRepository
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val application: Application
) : ViewModel() {
    val accounts = accountRepository.getAllAccounts()

    fun getGreetingMessage(calender: Calendar = Calendar.getInstance()): String {
        val res = application.resources

        return when (calender.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> res.getString(R.string.text_home_greeting_morning)
            in 12..15 -> res.getString(R.string.text_home_greeting_afternoon)
            in 16..20 -> res.getString(R.string.text_home_greeting_evening)
            in 21..23 -> res.getString(R.string.text_home_greeting_night)
            else -> res.getString(R.string.text_home_greeting_fallback)
        }
    }
}

