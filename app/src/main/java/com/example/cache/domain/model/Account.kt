package com.example.cache.domain.model

import java.security.InvalidParameterException
import kotlin.random.Random

data class Account(
    var id: Long = 0L,
    var type: Type = Type.Savings,
    var product: Product? = Product.SavingsEveryday,
    var balance: Money = Money(),
    var currency: String = balance.currency.currencyCode,
    var transitNumber: String = "",
    var institutionNumber: String = "",
    var accountNumber: String = "",
    var liquidBalance: Money = Money()
) {
    enum class Type {
        Chequing,
        Savings,
        TFSA,
        RRSP
    }

    companion object {
        private val TRANSIT_NUMBER_DIGITS = 5..5
        private val INSTITUTION_NUMBER_DIGITS = 3..3
        private val ACCOUNT_NUMBER_DIGITS = 7..12

        private fun <T> getPaddedString(number: T, minDigits: Int): String {
            if (number.toString().length < minDigits) {
                return "%0${minDigits}d".format(number)
            }

            return number.toString()
        }

        fun issue(
            accountType: Type,
            accountBalance: Money = Money(),
            accountProduct: Product? = null,
            random: Random = Random.Default
        ): Account {
            val transitNumberInt = random.nextInt(0, "9".repeat(TRANSIT_NUMBER_DIGITS.last).toInt())
            val institutionNumberInt = random.nextInt(0, "9".repeat(INSTITUTION_NUMBER_DIGITS.last).toInt())
            val accountNumberLong =  random.nextLong(0, "9".repeat(ACCOUNT_NUMBER_DIGITS.last).toLong())

            return Account(
                type = accountType,
                product = accountProduct,
                balance = accountBalance,
                transitNumber = getPaddedString(transitNumberInt, TRANSIT_NUMBER_DIGITS.first),
                institutionNumber = getPaddedString(institutionNumberInt, INSTITUTION_NUMBER_DIGITS.first),
                accountNumber = getPaddedString(accountNumberLong, ACCOUNT_NUMBER_DIGITS.first),
                liquidBalance = accountBalance
            )
        }
    }

    init {
        if (transitNumber.length > TRANSIT_NUMBER_DIGITS.last ||
            institutionNumber.length > INSTITUTION_NUMBER_DIGITS.last ||
            accountNumber.length > ACCOUNT_NUMBER_DIGITS.last
        )
            throw InvalidParameterException()
    }

    val routingNumber: String
        get() = "$transitNumber-$accountNumber"

    val accountNumberLast4Digits: String
        get() = accountNumber.takeLast(4)


    override fun toString(): String = "$type ($accountNumberLast4Digits)"
}