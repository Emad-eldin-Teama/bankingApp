package com.example.cache.data.db.model

import androidx.room.TypeConverter
import com.example.cache.domain.model.*
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Converters {
    private val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun moneyToString(input: Money?): String {
        return if(input != null) "${input.currency.currencyCode} ${input.amount}" else ""
    }

    @TypeConverter
    fun stringToMoney(input: String?): Money {
        val parts = input?.split(" ")
        val currencyCode: String? = parts?.getOrNull(0)
        val amountStr: String? = parts?.getOrNull(1)

        return if (currencyCode != null && amountStr != null) {
            Money(BigDecimal(amountStr), Currency.getInstance(currencyCode))
        } else {
            Money()
        }
    }

    @TypeConverter
    fun accountTypeToString(input: Account.Type?): String {
        return input?.name ?: ""
    }

    @TypeConverter
    fun stringToAccountType(input: String?): Account.Type? {
        if (input.isNullOrBlank()) return null
        return Account.Type.valueOf(input)
    }

    @TypeConverter
    fun productToString(input: Product?): String {
        return input?.productName ?: ""
    }

    @TypeConverter
    fun stringToProduct(input: String?): Product? {
        if (input.isNullOrBlank()) return null
        return Product.fromString(input)
    }

    @TypeConverter
    fun stringToOffsetDateTime(input: String?): OffsetDateTime? {
        return input?.let {
            return dateFormatter.parse(input, OffsetDateTime::from)
        }
    }

    @TypeConverter
    fun offsetDateTimeToString(input: OffsetDateTime?): String? {
        return input?.let {
            it.format(dateFormatter)
        }
    }

    @TypeConverter
    fun transactionTypeToString(input: Transaction.Type?): String {
        return input?.name ?: ""
    }

    @TypeConverter
    fun stringToTransactionType(input: String?): Transaction.Type? {
        if (input.isNullOrBlank()) return null
        return Transaction.Type.valueOf(input)
    }

    @TypeConverter
    fun orderTypeToString(input: Order.Type?): String {
        return input?.name ?: ""
    }

    @TypeConverter
    fun stringToOrderType(input: String?): Order.Type? {
        if (input.isNullOrBlank()) return null
        return Order.Type.valueOf(input)
    }
}

