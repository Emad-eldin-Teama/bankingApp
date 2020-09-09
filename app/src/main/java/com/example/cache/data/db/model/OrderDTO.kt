package com.example.cache.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.cache.domain.model.Money
import com.example.cache.domain.model.Order
import java.time.OffsetDateTime

@Entity(
    tableName = "Order",
    foreignKeys = [ForeignKey(
        entity = AccountDTO::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("account_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class OrderDTO (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "account_id", index = true)
    var accountId: Long = 0L,

    @ColumnInfo(name = "stock_symbol")
    var stockSymbol: String = "",

    @ColumnInfo(name = "stock_name")
    var stockName: String = "",

    @ColumnInfo(name = "stock_type")
    var stockType: String = "",

    @ColumnInfo(name = "stock_currency")
    var stockCurrency: String = "",

    @ColumnInfo(name = "stock_unit_price")
    var stockUnitPrice: Money = Money(),

    @ColumnInfo(name = "order_total_price")
    var orderTotalPrice: Money = Money(),

    @ColumnInfo(name = "order_volume")
    var orderVolume: Long = 0L,

    @ColumnInfo(name = "order_date")
    var orderDate: OffsetDateTime? = null,

    @ColumnInfo(name = "order_type")
    var orderType: Order.Type = Order.Type.Market
)