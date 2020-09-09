package com.example.cache.data.db.model

import androidx.room.*
import com.example.cache.domain.model.Money
import com.example.cache.domain.model.Transaction
import java.time.OffsetDateTime

@Entity(
    tableName = "Transaction",
    foreignKeys = [ForeignKey(
        entity = AccountDTO::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("account_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class TransactionDTO (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "account_id", index = true)
    var accountId: Long = 0L,

    @ColumnInfo(name = "date")
    var date: OffsetDateTime? = null,

    @ColumnInfo(name = "opening_balance")
    var openingBalance: Money = Money(),

    @ColumnInfo(name = "amount")
    var amount: Money = Money(),

    @ColumnInfo(name = "currency")
    var currency: String = amount.currency.currencyCode,

    @ColumnInfo(name = "transaction_type")
    var type: Transaction.Type = Transaction.Type.Transfer,

    @ColumnInfo(name = "description")
    var description: String = ""
)