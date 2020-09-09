package com.example.cache.ui.transfer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.cache.data.repository.AccountRepository
import com.example.cache.data.repository.TradingRepository
import com.example.cache.data.repository.TransactionRepository
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Money
import com.example.cache.domain.model.Transaction
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class TransferViewModelTest {

    private lateinit var transferViewModel: TransferViewModel
    private val accountRepository: AccountRepository = mock()
    private val transactionRepository: TransactionRepository = mock()
    private val tradingRepository: TradingRepository = mock()

    private lateinit var accountsLiveData: MutableLiveData<List<Account>>

    private val accounts = listOf(
        Account.issue(Account.Type.Chequing, Money(BigDecimal(50000))),
        Account.issue(Account.Type.Savings, Money(BigDecimal(50000))),
        Account.issue(Account.Type.RRSP, Money(BigDecimal(50000))),
        Account.issue(Account.Type.TFSA, Money(BigDecimal(50000)))
    )

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        accountsLiveData = MutableLiveData(accounts)
        whenever(accountRepository.getAllAccounts()).thenReturn(accountsLiveData)
        transferViewModel = TransferViewModel(accountRepository, transactionRepository, tradingRepository)
    }

    @Test
    fun `can perform transfer transaction`() = runBlocking {
        transferViewModel.fromAccountItemPosition = MutableLiveData(0)
        transferViewModel.toAccountItemPosition = MutableLiveData(1)
        transferViewModel.amount = "1500.60"
        transferViewModel.onTransfer()
        transferViewModel.completeTransfer()

        val accountsCaptor = argumentCaptor<Account> {}
        verify(accountRepository, times(2)).update(accountsCaptor.capture())

        val sourceAccount = accounts[0].copy(balance = Money(BigDecimal(48499.40)))
        val destinationAccount = accounts[1].copy(balance = Money(BigDecimal(51500.60)))

        assertEquals(sourceAccount, accountsCaptor.firstValue)
        assertEquals(destinationAccount, accountsCaptor.lastValue)

        val transactionsCaptor = argumentCaptor<Transaction> {}
        verify(transactionRepository, times(2)).insert(transactionsCaptor.capture())

        assertEquals(Transaction.Type.Transfer, transactionsCaptor.firstValue.type)
        assertEquals(Money(BigDecimal(50000)), transactionsCaptor.firstValue.openingBalance)
        assertEquals(Money(BigDecimal(-1500.60)), transactionsCaptor.firstValue.amount)

        assertEquals(Transaction.Type.Transfer, transactionsCaptor.secondValue.type)
        assertEquals(Money(BigDecimal(50000)), transactionsCaptor.secondValue.openingBalance)
        assertEquals(Money(BigDecimal(1500.60)), transactionsCaptor.secondValue.amount)
    }

    @Test
    fun `validates transfer inputs correctly`() = runBlocking {
        transferViewModel.amount = null
        transferViewModel.onTransfer()

        transferViewModel.amount = "invalid"
        transferViewModel.onTransfer()

        verify(accountRepository, never()).update(any())
        verify(transactionRepository, never()).insert(any())
    }


}