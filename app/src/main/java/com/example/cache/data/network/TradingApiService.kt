package com.example.cache.data.network

import com.example.cache.BuildConfig
import com.example.cache.data.network.model.ExchangeRateResponse
import com.example.cache.data.network.model.QuoteStockResponse
import com.example.cache.data.network.model.SearchStockResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A public interface that exposes the [getProperties] method
 */
interface TradingApiService {
    /**
     * Returns a Coroutine [Deferred] [List] of [ExchangeRateResponse] which can be fetched with await() if
     * in a Coroutine scope.
     * The @GET annotation indicates that the "exchangerate" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("query")
    suspend fun getExchangeRate(
        @Query("from_currency") fromCurrency: String,
        @Query("to_currency") toCurrency: String,
        @Query("function") function: String = "CURRENCY_EXCHANGE_RATE",
        @Query("apikey") apiKey: String = BuildConfig.TRADING_API_KEY
    ):
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
            ExchangeRateResponse

    @GET("query")
    suspend fun searchStock(
        @Query("keywords") keywords: String,
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("apikey") apiKey: String = BuildConfig.TRADING_API_KEY
    ): SearchStockResponse

    @GET("query")
    suspend fun quoteStock(
        @Query("symbol") symbol: String,
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("apikey") apiKey: String = BuildConfig.TRADING_API_KEY
    ): QuoteStockResponse
}