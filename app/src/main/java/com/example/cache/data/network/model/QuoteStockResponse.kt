package com.example.cache.data.network.model

import com.squareup.moshi.Json

data class QuoteStockResponse (
    @Json(name = "Global Quote")
    val globalQuoteProperty: GlobalQuoteProperty
)

data class GlobalQuoteProperty (
    @Json(name = "01. symbol")
    val symbol: String,
    @Json(name = "02. open")
    val open: String,
    @Json(name = "03. high")
    val high: String,
    @Json(name = "04. low")
    val low: String,
    @Json(name = "05. price")
    val price: String,
    @Json(name = "06. volume")
    val volume: String,
    @Json(name = "07. latest trading day")
    val latestTradingDay: String,
    @Json(name = "08. previous close")
    val previousClose: String,
    @Json(name = "09. change")
    val change: String,
    @Json(name = "10. change percent")
    val changePercent: String
)