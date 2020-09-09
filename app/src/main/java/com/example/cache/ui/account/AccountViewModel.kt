package com.example.cache.ui.account

import androidx.lifecycle.*
import com.example.cache.data.repository.AccountRepository
import com.example.cache.data.repository.TransactionRepository
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Transaction
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel()  {
    var account:  MutableLiveData<Account> = MutableLiveData()
    var transactions: LiveData<List<Transaction>> = MutableLiveData()

    fun getAccountWithTransactions(id: Long) {
        viewModelScope.launch {
            account.value = accountRepository.get(id)
        }

        transactions = transactionRepository.getTransactionsByAccount(id)
    }
}
