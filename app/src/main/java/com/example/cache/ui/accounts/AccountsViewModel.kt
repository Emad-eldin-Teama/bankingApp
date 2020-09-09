package com.example.cache.ui.accounts

import androidx.lifecycle.*
import com.example.cache.data.repository.AccountRepository
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Money
import com.example.cache.domain.model.Product
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class AccountsViewModel @Inject constructor(private val accountRepository: AccountRepository) : ViewModel() {
    val accounts = accountRepository.getAllAccounts()

    fun openAccount(product: Product) {
        val accountType = when (product) {
            in Product.ChequingAccountProducts -> Account.Type.Chequing
            in Product.SavingsAccountProducts -> Account.Type.Savings
            else -> return
        }

        val accountCurrency = when(product) {
            Product.ChequingBorderless -> Currency.getInstance("USD")
            else -> Currency.getInstance("CAD")
        }

        val account = Account.issue(
            accountType,
            Money(
                BigDecimal(0),
                accountCurrency
            ),
            product
        )

        viewModelScope.launch {
            accountRepository.insert(account)
        }
    }

    fun onClear() {
        viewModelScope.launch {
            accountRepository.clear()
        }
    }
}