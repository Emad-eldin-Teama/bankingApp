package com.example.cache.domain.model

data class ExchangeRate (
    var fromCurrencyCode: String = "",
    var fromCurrencyName: String = "",
    var toCurrencyCode: String = "",
    var toCurrencyName: String = "",
    var exchangeRate: Double = 0.0,
    var lastRefreshed: String = "",
    var timeZone: String = "",
    var bidPrice: String = "",
    var askPrice: String = ""
)