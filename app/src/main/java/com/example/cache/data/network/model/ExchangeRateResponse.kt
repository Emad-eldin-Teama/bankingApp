package com.example.cache.data.network.model

import com.squareup.moshi.Json

data class ExchangeRateResponse (
    @Json(name = "Realtime Currency Exchange Rate")
    val realTimeExchangeRateProperty: RealTimeExchangeRateProperty
)

data class RealTimeExchangeRateProperty (
    @Json(name = "1. From_Currency Code")
    val fromCurrencyCode: String,
    @Json(name = "2. From_Currency Name")
    val fromCurrencyName: String,
    @Json(name = "3. To_Currency Code")
    val toCurrencyCode: String,
    @Json(name = "4. To_Currency Name")
    val toCurrencyName: String,
    @Json(name = "5. Exchange Rate")
    val exchangeRate: String,
    @Json(name = "6. Last Refreshed")
    val lastRefreshed: String,
    @Json(name = "7. Time Zone")
    val timeZone: String,
    @Json(name = "8. Bid Price")
    val bidPrice: String,
    @Json(name = "9. Ask Price")
    val askPrice: String
)