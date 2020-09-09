package com.example.cache.di.module

import com.example.cache.data.db.dao.AccountDao
import com.example.cache.data.db.dao.OrderDao
import com.example.cache.data.db.dao.TransactionDao
import com.example.cache.data.network.TradingApiService
import com.example.cache.data.repository.AccountRepository
import com.example.cache.data.repository.OrderRepository
import com.example.cache.data.repository.TradingRepository
import com.example.cache.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class, NetworkModule::class])
class RepositoryModule {

    @Singleton
    @Provides
    fun provideAccountRepository(accountDao: AccountDao): AccountRepository = AccountRepository(accountDao)

    @Singleton
    @Provides
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository = TransactionRepository(transactionDao)

    @Singleton
    @Provides
    fun provideOrderRepository(orderDao: OrderDao): OrderRepository = OrderRepository(orderDao)

    @Singleton
    @Provides
    fun provideTradingRepository(tradingApiService: TradingApiService): TradingRepository = TradingRepository(tradingApiService)

}