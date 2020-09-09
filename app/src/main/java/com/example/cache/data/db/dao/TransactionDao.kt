package com.example.cache.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.cache.data.db.model.TransactionDTO

@Dao
interface TransactionDao: BaseDao<TransactionDTO> {
    @Query("SELECT * from `transaction` WHERE id = :id")
    suspend fun get(id: Long): TransactionDTO?

    @Query("DELETE from `transaction`")
    suspend fun clear()

    @Query("SELECT * from `transaction` WHERE account_id = :accountId ORDER BY datetime(date) DESC")
    fun getTransactionsByAccount(accountId: Long): LiveData<List<TransactionDTO>>
}