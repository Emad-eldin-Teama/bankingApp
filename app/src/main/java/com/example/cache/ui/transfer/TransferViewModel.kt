package com.example.cache.ui.transfer

import androidx.lifecycle.*
import com.example.cache.data.repository.AccountRepository
import com.example.cache.data.repository.TradingRepository
import com.example.cache.data.repository.TransactionRepository
import com.example.cache.domain.model.ExchangeRate
import com.example.cache.utils.Failure
import com.example.cache.domain.model.Money
import com.example.cache.utils.Success
import com.example.cache.domain.model.Transaction
import com.example.cache.utils.Result
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.security.InvalidParameterException
import java.util.*
import javax.inject.Inject

class TransferViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val tradingRepository: TradingRepository
) : ViewModel() {
    val accounts = accountRepository.getAllAccounts()

    var fromAccountItemPosition = MutableLiveData(0)
    var toAccountItemPosition = MutableLiveData(0)

    var transferErrorMessage = MutableLiveData<String>()
    var transferConfirmationMessage = MutableLiveData<String>()
    var transferSuccessMessage = MutableLiveData<String>()

    var useFromAccountCurrency = true
    var amount: String? = null

    var exchangeRate: ExchangeRate = ExchangeRate()
    var transferAmount: Money = Money()

    fun onTransfer() {
        if (!validateTransferInputs()) return

        val sourceAccount = accounts.value!![fromAccountItemPosition.value!!]
        val destinationAccount = accounts.value!![toAccountItemPosition.value!!]

        if (sourceAccount.currency != destinationAccount.currency) {
            if (useFromAccountCurrency) {
                transferAmount = Money(BigDecimal(amount), sourceAccount.balance.currency)
                confirmExchangeRate(sourceAccount.currency, destinationAccount.currency)
            }
            else {
                transferAmount = Money(BigDecimal(amount), destinationAccount.balance.currency)
                confirmExchangeRate(destinationAccount.currency, sourceAccount.currency)
            }

            return
        }

        transferAmount = Money(BigDecimal(amount), destinationAccount.balance.currency)
        transferConfirmationMessage.value = "Transfer $transferAmount ${transferAmount.currency} from $sourceAccount to $destinationAccount?"
    }

    fun completeTransfer() {
        val sourceAccount = accounts.value!![fromAccountItemPosition.value!!]
        val destinationAccount = accounts.value!![toAccountItemPosition.value!!]
        val transactions: Result<Pair<Transaction, Transaction>, String>

        transactions = if (sourceAccount.currency != destinationAccount.currency) {
            Transaction.transfer(sourceAccount, destinationAccount, transferAmount, exchangeRate)
        } else {
            Transaction.transfer(sourceAccount, destinationAccount, transferAmount)
        }

        when (transactions) {
            is Failure -> transferErrorMessage.value = transactions.reason
            is Success -> {
                viewModelScope.launch {
                    accountRepository.update(sourceAccount)
                    accountRepository.update(destinationAccount)
                    transactionRepository.insert(transactions.value.first)
                    transactionRepository.insert(transactions.value.second)
                }

                transferSuccessMessage.value = "Transferred $transferAmount ${transferAmount.currency.currencyCode} from $sourceAccount to $destinationAccount"
            }
        }
    }

    fun getCurrencies(): Pair<Currency, Currency> {
        val sourceAccount = accounts.value!![fromAccountItemPosition.value!!]
        val destinationAccount = accounts.value!![toAccountItemPosition.value!!]

        return Pair(sourceAccount.balance.currency, destinationAccount.balance.currency)
    }

    private fun validateTransferInputs(): Boolean {
        try {
            when {
                amount.isNullOrBlank() -> throw InvalidParameterException("Amount cannot be blank")
                amount?.toBigDecimalOrNull() == null -> throw InvalidParameterException("Invalid amount")
            }
        }
        catch (e: InvalidParameterException) {
            transferErrorMessage.value = e.message
            return false
        }

        return true
    }

    private fun confirmExchangeRate(fromCurrency: String, toCurrency: String) {
        val sourceAccount = accounts.value!![fromAccountItemPosition.value!!]
        val destinationAccount = accounts.value!![toAccountItemPosition.value!!]

        viewModelScope.launch {
            when (val exchangeRateResult = tradingRepository.getExchangeRate(fromCurrency, toCurrency)) {
                is Failure -> transferErrorMessage.value = "Unable to retrieve exchange rate."
                is Success -> {
                    exchangeRate = exchangeRateResult.value
                    transferConfirmationMessage.value = "Transfer $transferAmount ${transferAmount.currency} from $sourceAccount to $destinationAccount at an exchange rate of ${"%.2f".format(exchangeRateResult.value.exchangeRate)}?"
                }
            }
        }
    }
}