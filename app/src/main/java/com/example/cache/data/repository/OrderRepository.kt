package com.example.cache.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.cache.data.db.dao.OrderDao
import com.example.cache.data.db.model.OrderDTO
import com.example.cache.domain.model.Order
import org.modelmapper.ModelMapper

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun getAllOrdersByAccount(accountId: Long): List<Order> {
        /*
        return orderDao.getAllOrdersByAccount(accountId).map {
            ModelMapper().map(it, Array<Order>::class.java).toList()
        }

         */
        return ModelMapper().map(
            orderDao.getAllOrdersByAccount(accountId),
            Array<Order>::class.java
        ).toList()
    }

    suspend fun getStockOrdersByAccount(accountId: Long, stockSymbol: String): List<Order> {
        return ModelMapper().map(
            orderDao.getStockOrdersByAccount(accountId, stockSymbol),
            Array<Order>::class.java
        ).toList()
    }

    suspend fun update(order: Order) {
        orderDao.update(ModelMapper().map(order, OrderDTO::class.java))
    }

    suspend fun delete(order: Order) {
        orderDao.delete(ModelMapper().map(order, OrderDTO::class.java))
    }

    suspend fun insert(order: Order) {
        orderDao.insert(ModelMapper().map(order, OrderDTO::class.java))
    }
}