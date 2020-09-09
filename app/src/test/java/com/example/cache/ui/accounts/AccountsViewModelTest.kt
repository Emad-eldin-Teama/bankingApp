package com.example.cache.ui.accounts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.cache.data.repository.AccountRepository
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Money
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal
import java.util.*

class AccountsViewModelTest {

    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var accountsLiveData: MutableLiveData<List<Account>>

    private val accounts = listOf(
        Account.issue(Account.Type.Chequing, Money(BigDecimal(50000))),
        Account.issue(Account.Type.Savings, Money(BigDecimal(50000))),
        Account.issue(Account.Type.RRSP, Money(BigDecimal(50000))),
        Account.issue(Account.Type.TFSA, Money(BigDecimal(50000)))
    )

    private val accountRepository: AccountRepository = mock()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        accountsLiveData = MutableLiveData(accounts)
        whenever(accountRepository.getAllAccounts()).thenReturn(accountsLiveData)
        accountsViewModel = AccountsViewModel(accountRepository)
    }

    @Test
    fun `can add new account`() = runBlocking {
        accountsViewModel.openAccount()

        val accountCaptor = argumentCaptor<Account> {}
        verify(accountRepository, times(1)).insert(accountCaptor.capture())

        assertEquals(Account.Type.Savings, accountCaptor.lastValue.type)
        assertEquals(
            Money(BigDecimal(20000), Currency.getInstance("USD")),
            accountCaptor.lastValue.balance
        )
    }

    @Test
    fun `can clear all accounts`() = runBlocking {
        accountsViewModel.onClear()
        verify(accountRepository, times(1)).clear()
    }

}