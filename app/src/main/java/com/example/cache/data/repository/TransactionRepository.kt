package com.example.cache.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.cache.data.db.dao.TransactionDao
import com.example.cache.data.db.model.TransactionDTO
import com.example.cache.domain.model.Transaction
import org.modelmapper.ModelMapper
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getTransactionsByAccount(accountId: Long): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByAccount(accountId).map {
            ModelMapper().map(it, Array<Transaction>::class.java).toList()
        }
    }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(ModelMapper().map(transaction, TransactionDTO::class.java))
    }

    suspend fun insertAll(transactions: List<Transaction>) {
        transactionDao.insertAll(ModelMapper().map(transactions, Array<TransactionDTO>::class.java).toList())
    }

    suspend fun update(transaction: Transaction) {
        transactionDao.update(ModelMapper().map(transaction, TransactionDTO::class.java))
    }

    suspend fun clear() {
        transactionDao.clear()
    }

    companion object {
        fun List<Transaction>.groupByDate() =
            this.groupBy {
                DateTimeFormatter.ofPattern("MMM dd, yyyy").format(it.date)
            }
    }
}