package com.example.cache.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.cache.data.db.dao.AccountDao
import com.example.cache.data.db.model.AccountDTO
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Money
import org.modelmapper.ModelMapper
import javax.inject.Inject

class AccountRepository(private val accountDao: AccountDao) {

    fun getAllAccounts(): LiveData<List<Account>> {
        return accountDao.getAllAccounts().map {
            ModelMapper().map(it, Array<Account>::class.java).toList()
        }
    }

    fun getInvestmentAccounts(): LiveData<List<Account>> {
        return accountDao.getAllAccounts().map {
            ModelMapper().map(it, Array<Account>::class.java).toList().filter {
                account -> account.type in listOf(Account.Type.RRSP, Account.Type.TFSA)
            }
        }
    }

    suspend fun get(id: Long): Account {
        return ModelMapper().map(accountDao.get(id), Account::class.java)
    }

    suspend fun insert(account: Account) {
        accountDao.insert(ModelMapper().map(account, AccountDTO::class.java))
    }

    suspend fun insertAll(accounts: List<Account>): List<Long> {
        return accountDao.insertAll(ModelMapper().map(accounts, Array<AccountDTO>::class.java).toList())
    }

    suspend fun clear() {
        accountDao.clear()
    }

    suspend fun update(account: Account) {
        accountDao.update(ModelMapper().map(account, AccountDTO::class.java))
    }

    companion object {
        fun List<Account>.totals(accountTypes: List<Account.Type> = Account.Type.values().toList()): List<Money> =
            this.filter { it.type in accountTypes }
                .groupingBy(Account::currency)
                .aggregate { _, accumulator: Money?, element: Account, _ ->
                    accumulator?.plus(element.balance) ?: element.balance
                }
                .values.toList()
    }
}