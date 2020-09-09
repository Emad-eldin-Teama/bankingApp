package com.example.cache.domain.model

enum class Product(val productName: String) {
    ChequingVIP("VIP Chequing Account"),
    ChequingNoLimit("No Limit Chequing Account"),
    ChequingDayToDay("Day to Day Chequing Account"),
    ChequingBorderless("Borderless Chequing Account"),
    SavingsPremium("Premium Savings Account"),
    SavingsEveryday("Every Day Savings Account"),
    SavingsHighInterest("High Interest Savings Account");

    companion object {
        val ChequingAccountProducts = listOf(ChequingVIP, ChequingNoLimit, ChequingDayToDay, ChequingBorderless)
        val SavingsAccountProducts = listOf(SavingsPremium, SavingsEveryday, SavingsHighInterest)

        fun fromString(productName: String): Product? = values().find { it.productName == productName}
    }
}