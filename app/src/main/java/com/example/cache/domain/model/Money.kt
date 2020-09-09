package com.example.cache.domain.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.security.InvalidParameterException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

data class Money(
    var amount: BigDecimal = BigDecimal.ZERO,
    val currency: Currency = DEFAULT_CURRENCY,
    val rounding: RoundingMode = DEFAULT_ROUNDING
) {
    companion object {
        val DEFAULT_CURRENCY: Currency = Currency.getInstance("CAD")
        val DEFAULT_ROUNDING = RoundingMode.HALF_EVEN
    }

    val displayCurrencyCode: String = if (currency == Currency.getInstance(Locale.getDefault())) "" else currency.currencyCode

    init {
        amount = amount.setScale(currency.defaultFractionDigits, rounding)
    }

    operator fun plus(m: Money): Money {
       if (currency != m.currency || rounding != m.rounding) throw InvalidParameterException()
        return Money(
            amount.add(m.amount),
            currency,
            rounding
        )
    }

    operator fun minus(m: Money): Money {
        if (currency != m.currency || rounding != m.rounding) throw InvalidParameterException()
        return Money(amount.minus(m.amount), currency, rounding)
    }

    operator fun div(divisor: Long): Money {
        return Money(amount.div(BigDecimal(divisor)), currency, rounding)
    }

    operator fun times(factor: Long): Money {
        return Money(amount.times(BigDecimal(factor)), currency, rounding)
    }

    operator fun unaryMinus(): Money {
        return Money(amount.unaryMinus(), currency, rounding)
    }

    operator fun compareTo(m: Money): Int {
        if (currency != m.currency || rounding != m.rounding) throw InvalidParameterException()
        return amount.compareTo(m.amount)
    }

    fun convertCurrency(exchangeRate: ExchangeRate): Money {
        return if (currency.currencyCode == exchangeRate.fromCurrencyCode) {
            Money(
                amount * BigDecimal(exchangeRate.exchangeRate),
                Currency.getInstance(exchangeRate.toCurrencyCode),
                rounding
            )
        } else {
            Money(
                amount / BigDecimal(exchangeRate.exchangeRate),
                Currency.getInstance(exchangeRate.fromCurrencyCode),
                rounding
            )
        }
    }

    override fun toString(): String {
        val formatter = NumberFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = ""
        formatter.decimalFormatSymbols = symbols
        return formatter.format(amount)
    }
}
