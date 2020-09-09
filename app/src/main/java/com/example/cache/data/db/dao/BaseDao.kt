package com.example.cache.data.db.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(t: List<T>): List<Long>

    @Update
    suspend fun update(t: T)

    @Delete
    suspend fun delete(t: T)
}