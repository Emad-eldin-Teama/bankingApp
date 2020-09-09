package com.example.cache.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.security.InvalidParameterException
import java.util.*

class MoneyTest {
    @Test
    fun `can add money objects`() {
        assertEquals(
            Money(amount = BigDecimal(300.95)),
            Money(amount = BigDecimal(100.45)) + Money(amount = BigDecimal(200.50))
        )
    }

    @Test(expected = InvalidParameterException::class)
    fun `can't add money objects with different currencies`() {
        Money(amount = BigDecimal(45.50), currency = Currency.getInstance("USD")) +
        Money(amount = BigDecimal(60.25), currency = Currency.getInstance("CAD"))
    }

    @Test
    fun `can subtract money objects`() {
        assertEquals(
            Money(amount = BigDecimal(200.00)),
            Money(amount = BigDecimal(300.50)) - Money(amount = BigDecimal(100.50))
        )
    }

    @Test(expected = InvalidParameterException::class)
    fun `can't subtract money objects with different currencies`() {
        Money(amount = BigDecimal(105.99), currency = Currency.getInstance("USD")) -
        Money(amount = BigDecimal(34.99), currency = Currency.getInstance("CAD"))
    }

    @Test
    fun `can get the negative value of a money object`() {
        assertEquals(Money(amount = BigDecimal(-25.59)), -Money(amount = BigDecimal(25.59)))
    }

    @Test
    fun `can compare money objects`() {
        assertTrue(Money(amount = BigDecimal(87.30)) > Money(amount = BigDecimal(10.19)))
        assertFalse(Money(amount = BigDecimal(90.15)) > Money(amount = BigDecimal(500.09)))

        assertTrue(Money(amount = BigDecimal(15.56)) < Money(amount = BigDecimal(16.99)))
        assertFalse(Money(amount = BigDecimal(30.67)) < Money(amount = BigDecimal(1.10)))

        assertEquals(Money(amount = BigDecimal(49.55)), Money(amount = BigDecimal(49.55)))
        assertNotEquals(Money(amount = BigDecimal(108.80)), Money(amount = BigDecimal(2.25)))
    }

    @Test(expected = InvalidParameterException::class)
    fun `can't subtract compare objects with different currencies`() {
        Money(amount = BigDecimal(105.99), currency = Currency.getInstance("EUR")).compareTo(
            Money(amount = BigDecimal(34.99), currency = Currency.getInstance("CNY"))
        )
    }
}
