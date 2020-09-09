package com.example.cache.di.module

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cache.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
class DatabaseModule {

    lateinit var appDatabase: AppDatabase

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase {
        appDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "cache_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(@NonNull db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    CoroutineScope(Dispatchers.IO).launch {
                        val accountIds: List<Long> = appDatabase.accountDao.insertAll(AppDatabase.prepopulateAccountData)
                        val firstAccount = appDatabase.accountDao.get(accountIds.first())
                        appDatabase.transactionDao.insertAll(AppDatabase.prepopulateTransactionData(firstAccount!!))
                        appDatabase.accountDao.update(firstAccount)
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()

        return appDatabase
    }

    @Provides
    fun provideAccountDao(database: AppDatabase) = database.accountDao

    @Provides
    fun provideTransactionDao(database: AppDatabase) = database.transactionDao

    @Provides
    fun provideOrderDao(database: AppDatabase) = database.orderDao
}