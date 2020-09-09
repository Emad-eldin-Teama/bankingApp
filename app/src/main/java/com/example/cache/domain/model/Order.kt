package com.example.cache.domain.model

import java.math.BigDecimal
import java.security.InvalidParameterException
import java.time.OffsetDateTime
import java.util.*

data class Order (
    var id: Long = 0L,
    var accountId: Long = 0L,
    var stockSymbol: String = "",
    var stockName: String = "",
    var stockType: String = "",
    var stockCurrency: String = "",
    var stockUnitPrice: Money = Money(),
    var orderTotalPrice: Money = Money(),
    var orderVolume: Long = 0L,
    var orderType: Type = Type.Market,
    var orderDate: OffsetDateTime? = null
) {
    enum class Type {
        Market
    }

    enum class Action {
        Buy,
        Sell
    }

    companion object {
        fun create(
            account: Account,
            stock: Stock,
            quote: Quote,
            orderVolume: Long,
            orderType: Type = Type.Market,
            exchangeRate: ExchangeRate? = null,
            orderDate: OffsetDateTime = OffsetDateTime.now()
        ): Order {

            val stockUnitPrice = Money(
                BigDecimal(quote.price),
                Currency.getInstance(stock.currency)
            )

            var orderTotalPrice = Money(
                BigDecimal(quote.price) * BigDecimal(orderVolume),
                Currency.getInstance(stock.currency)
            )

            if (stock.currency != account.currency) {
                if (exchangeRate == null) {
                    throw InvalidParameterException("Error retrieving exchange rate")
                }
                else {
                    orderTotalPrice = orderTotalPrice.convertCurrency(exchangeRate)
                }
            }

            return Order (
                accountId = account.id,
                stockSymbol = stock.symbol,
                stockName = stock.name,
                stockType = stock.type,
                stockCurrency = stock.currency,
                stockUnitPrice = stockUnitPrice,
                orderTotalPrice = orderTotalPrice,
                orderVolume = orderVolume,
                orderDate = orderDate,
                orderType = orderType
            )
        }
    }

    val perUnitPriceString: String
        get() = (orderTotalPrice / orderVolume).toString()

    fun recalculateVolume(newOrderVolume: Long) {
        val pricePerStock = orderTotalPrice / orderVolume
        orderVolume = newOrderVolume
        orderTotalPrice = pricePerStock * newOrderVolume
    }
}