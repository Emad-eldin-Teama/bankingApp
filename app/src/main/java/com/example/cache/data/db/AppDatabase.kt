package com.example.cache.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cache.data.db.dao.AccountDao
import com.example.cache.data.db.dao.OrderDao
import com.example.cache.data.db.dao.TransactionDao
import com.example.cache.data.db.model.AccountDTO
import com.example.cache.data.db.model.Converters
import com.example.cache.data.db.model.OrderDTO
import com.example.cache.data.db.model.TransactionDTO
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Money
import com.example.cache.domain.model.Product
import com.example.cache.utils.Success
import com.example.cache.domain.model.Transaction
import org.modelmapper.ModelMapper
import java.math.BigDecimal
import java.time.OffsetDateTime

@Database(entities = [AccountDTO::class, TransactionDTO::class, OrderDTO::class], version = 11, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val transactionDao: TransactionDao
    abstract val orderDao: OrderDao

    companion object {
        val prepopulateAccountData = ModelMapper().map(
            listOf(
                Account.issue(Account.Type.Chequing, Money(BigDecimal(50000)), Product.ChequingDayToDay),
                Account.issue(Account.Type.Savings, Money(BigDecimal(50000)), Product.SavingsEveryday),
                Account.issue(Account.Type.RRSP, Money(BigDecimal(50000))),
                Account.issue(Account.Type.TFSA, Money(BigDecimal(50000)))
            ),
            Array<AccountDTO>::class.java
        ).toList()


        fun prepopulateTransactionData(accountDTO: AccountDTO): List<TransactionDTO> {
            val account = ModelMapper().map(accountDTO, Account::class.java)

            return ModelMapper().map(
                listOf(
                    (Transaction.charge(account, Money(BigDecimal(200.00)), "e-transfer sent Raquel Riel") as Success).value,
                    (Transaction.deposit(account, Money(BigDecimal(5600.66)), "Payroll Deposit MyCompany", OffsetDateTime.now().minusDays(1)) as Success).value,
                    (Transaction.charge(account, Money(BigDecimal(56.86)), "Hydro Bill", OffsetDateTime.now().minusDays(1)) as Success).value,
                    (Transaction.charge(account, Money(BigDecimal(15.03)), "Monthly Fee", OffsetDateTime.now().minusDays(1)) as Success).value,
                    (Transaction.charge(account, Money(BigDecimal(10.66)), "UberBV", OffsetDateTime.now().minusDays(2)) as Success).value,
                    (Transaction.charge(account, Money(BigDecimal(9.86)), "UberBV", OffsetDateTime.now().minusDays(2)) as Success).value,
                    (Transaction.charge(account, Money(BigDecimal(12.56)), "UberBV", OffsetDateTime.now().minusDays(2)) as Success).value,
                    (Transaction.charge(account, Money(BigDecimal(55.00)), "e-transfer sent Debby Reido", OffsetDateTime.now().minusDays(2)) as Success).value,
                    (Transaction.charge(account, Money(BigDecimal(25.00)), "e-transfer sent Raquel Riel", OffsetDateTime.now().minusDays(2)) as Success).value
                ),
                Array<TransactionDTO>::class.java
            ).toList()
        }
    }
}