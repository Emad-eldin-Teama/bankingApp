package com.example.cache.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.cache.data.db.model.OrderDTO

@Dao
interface OrderDao: BaseDao<OrderDTO> {

    @Query("SELECT * from `order` WHERE account_id = :accountId ORDER BY datetime(order_date) DESC")
    suspend fun getAllOrdersByAccount(accountId: Long): List<OrderDTO>

    @Query("SELECT * from `order` WHERE account_id = :accountId AND stock_symbol = :stockSymbol ORDER BY datetime(order_date) DESC")
    suspend fun getStockOrdersByAccount(accountId: Long, stockSymbol: String): List<OrderDTO>

}