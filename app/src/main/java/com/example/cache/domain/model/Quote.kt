package com.example.cache.domain.model

data class Quote (
    var symbol: String = "",
    var open: String = "",
    var high: String = "",
    var low: String = "",
    var price: String = "",
    var volume: String = "",
    var latestTradingDay: String = "",
    var previousClose: String = "",
    var change: String = "",
    var changePercent: String = ""
)