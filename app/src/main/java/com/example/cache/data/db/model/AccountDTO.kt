package com.example.cache.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Money
import com.example.cache.domain.model.Product

@Entity(tableName = "Account")
data class AccountDTO (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "type")
    var type: Account.Type = Account.Type.Savings,

    @ColumnInfo(name = "product")
    var product: Product? = Product.SavingsEveryday,

    @ColumnInfo(name = "balance")
    var balance: Money = Money(),

    @ColumnInfo(name = "currency")
    var currency: String = balance.currency.currencyCode,

    @ColumnInfo(name = "transit_number")
    var transitNumber: String = "",

    @ColumnInfo(name = "institution_number")
    var institutionNumber: String = "",

    @ColumnInfo(name = "account_number")
    var accountNumber: String = "",

    @ColumnInfo(name = "liquid_balance")
    var liquidBalance: Money = Money()
)