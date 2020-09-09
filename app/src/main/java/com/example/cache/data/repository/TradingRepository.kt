package com.example.cache.data.repository

import com.example.cache.data.network.TradingApiService
import com.example.cache.domain.model.ExchangeRate
import com.example.cache.domain.model.Quote
import com.example.cache.domain.model.Stock
import com.example.cache.utils.Result
import com.example.cache.utils.Failure
import com.example.cache.utils.Success
import org.modelmapper.ModelMapper
import javax.inject.Inject

class TradingRepository(private val tradingApiService: TradingApiService) {

    suspend fun getExchangeRate(
        fromCurrency: String,
        toCurrency: String
    ): Result<ExchangeRate, String> {
        return try {
            val exchangeRateResponse = tradingApiService.getExchangeRate(fromCurrency, toCurrency)
            val exchangeRate = ModelMapper().map(
                exchangeRateResponse.realTimeExchangeRateProperty,
                ExchangeRate::class.java
            )
            Success(exchangeRate)
        } catch (e: Exception) {
            Failure(e.message.toString())
        }
    }

    suspend fun searchStock(keywords: String): Result<List<Stock>, String> {
        return try {
            val searchStockResponse = tradingApiService.searchStock(keywords)
            val matchedStocks =
                ModelMapper().map(searchStockResponse.bestMatches, Array<Stock>::class.java)
                    .toList()
            Success(matchedStocks)
        } catch (e: Exception) {
            Failure(e.message.toString())
        }
    }

    suspend fun quoteStock(symbol: String): Result<Quote, String> {
        return try {
            val quoteStockResponse = tradingApiService.quoteStock(symbol)
            val quote = ModelMapper().map(quoteStockResponse.globalQuoteProperty, Quote::class.java)
            Success(quote)
        } catch (e: Exception) {
            Failure(e.message.toString())
        }
    }

}