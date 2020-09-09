package com.example.cache.domain.model

import com.example.cache.utils.Failure
import com.example.cache.utils.Success
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.util.*


class TransactionTest {

    @Test
    fun `can transfer money between accounts`() {
        val currency = Currency.getInstance("CAD")
        val sourceAccount = Account.issue(
            Account.Type.Chequing,
            Money(BigDecimal(5000), currency)
        )

        val destinationAccount = Account.issue(
            Account.Type.Savings,
            Money(BigDecimal(600), currency)
        )

        val transferAmount = Money(BigDecimal(50.50), currency)
        val transactions = Transaction.transfer(sourceAccount, destinationAccount, transferAmount)

        assertTrue(transactions is Success)
        assertEquals(Money(BigDecimal(4949.50), currency), sourceAccount.balance)
        assertEquals(Money(BigDecimal(650.50), currency), destinationAccount.balance)
    }

    @Test
    fun `can't transfer money to same account`() {
        val currency = Currency.getInstance("CAD")
        val account = Account.issue(
            Account.Type.Chequing,
            Money(BigDecimal(78.60), currency)
        )

        val transferAmount = Money(BigDecimal(1.75), currency)
        val transactions = Transaction.transfer(account, account, transferAmount)

        assertTrue(transactions is Failure)
    }

    @Test
    fun `can't transfer money between accounts with mismatched currencies`() {
        val currency1 = Currency.getInstance("CAD")
        val currency2 = Currency.getInstance("USD")

        val sourceAccount = Account.issue(
            Account.Type.Chequing,
            Money(BigDecimal(5000), currency1)
        )

        val destinationAccount = Account.issue(
            Account.Type.Savings,
            Money(BigDecimal(600), currency2)
        )

        val transferAmount = Money(BigDecimal(50.50), currency1)
        val transactions = Transaction.transfer(sourceAccount, destinationAccount, transferAmount)

        assertTrue(transactions is Failure)
        assertEquals(
            "Cannot transfer money between accounts with mismatched currencies",
            (transactions as Failure).reason
        )
    }

    @Test
    fun `can't transfer money from account with insufficient funds`() {
        val currency = Currency.getInstance("CAD")
        val sourceAccount = Account.issue(
            Account.Type.Chequing,
            Money(BigDecimal(2.50), currency)
        )

        val destinationAccount = Account.issue(
            Account.Type.Savings,
            Money(BigDecimal(4167), currency)
        )

        val transferAmount = Money(BigDecimal(333.10), currency)
        val transactions = Transaction.transfer(sourceAccount, destinationAccount, transferAmount)

        assertTrue(transactions is Failure)
        assertEquals(
            "Insufficient funds",
            (transactions as Failure).reason
        )
    }

}