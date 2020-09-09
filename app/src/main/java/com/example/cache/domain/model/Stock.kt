package com.example.cache.domain.model

data class Stock (
    var symbol: String = "",
    var name: String = "",
    var type: String = "",
    var region: String = "",
    var marketOpen: String = "",
    var marketClose: String = "",
    var timezone: String = "",
    var currency: String = "",
    var matchScore: String = ""
)