package com.example.cache.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.cache.data.db.model.AccountDTO

@Dao
interface AccountDao: BaseDao<AccountDTO> {
    @Query("SELECT * from account WHERE id = :id")
    suspend fun get(id: Long): AccountDTO?

    @Query("DELETE from account")
    suspend fun clear()

    @Query("SELECT * from account")
    fun getAllAccounts(): LiveData<List<AccountDTO>>

    @Query("SELECT * from account ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAccount(): AccountDTO?
}