package com.example.cache.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cache.data.db.dao.AccountDao
import com.example.cache.data.db.model.AccountDTO
import com.example.cache.domain.model.Money
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class AccountDatabaseTest {

    private lateinit var accountDao: AccountDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        accountDao = db.accountDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAccountTest() = runBlocking {
        val account = AccountDTO()
        val accountId = accountDao.insert(account)
        val latestAccount = accountDao.getLatestAccount()
        assertNotNull(latestAccount)
        assertEquals(accountId, latestAccount?.id)
    }

}
