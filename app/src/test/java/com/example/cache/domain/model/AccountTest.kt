package com.example.cache.domain.model

import org.junit.Assert.*
import org.junit.Test

class AccountTest {

    @Test
    fun `can generate valid account numbers`() {
        val account = Account.issue(Account.Type.Chequing)

        assertTrue(account.transitNumber.length in 5..5)
        assertTrue(account.institutionNumber.length in 3..3)
        assertTrue(account.accountNumber.length in 7..12)
    }
}