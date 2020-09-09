package com.example.cache.domain.model

import com.example.cache.utils.Failure
import com.example.cache.utils.Result
import com.example.cache.utils.Success
import java.security.InvalidParameterException
import java.time.OffsetDateTime

data class Transaction(
    var id: Long = 0L,
    var accountId: Long = 0L,
    var date: OffsetDateTime? = null,
    var openingBalance: Money = Money(),
    var amount: Money = Money(),
    var currency: String = amount.currency.currencyCode,
    var type: Type = Type.Transfer,
    var description: String = type.name
) {
    enum class Type {
        Charge,
        Deposit,
        Withdrawal,
        Transfer,
        Investment
    }

    companion object {

        fun charge(
            account: Account,
            chargeAmount: Money,
            description: String = Type.Charge.name,
            date: OffsetDateTime = OffsetDateTime.now()
        ): Result<Transaction, String> {
            when {
                chargeAmount > account.liquidBalance -> return Failure(
                    "Insufficient funds"
                )
                chargeAmount.currency != account.balance.currency -> return Failure(
                    "Cannot charge account with mismatched currency"
                )
            }

            val transaction = Transaction(
                accountId = account.id,
                date = date,
                openingBalance = account.balance,
                amount = -chargeAmount,
                type = Type.Transfer,
                description = description
            )

            account.balance = account.balance - chargeAmount
            account.liquidBalance = account.liquidBalance - chargeAmount

            return Success(transaction)
        }

        fun deposit(
            account: Account,
            depositAmount: Money,
            description: String = Type.Deposit.name,
            date: OffsetDateTime = OffsetDateTime.now()
        ): Result<Transaction, String> {
            when {
                depositAmount.currency != account.balance.currency -> return Failure(
                    "Cannot deposit into account with mismatched currency"
                )
            }

            val transaction = Transaction(
                accountId = account.id,
                date = date,
                openingBalance = account.balance,
                amount = depositAmount,
                type = Type.Transfer,
                description = description
            )

            account.balance = account.balance + depositAmount
            account.liquidBalance = account.liquidBalance + depositAmount

            return Success(transaction)
        }

        fun transfer(
            sourceAccount: Account,
            destinationAccount: Account,
            transferAmount: Money,
            exchangeRate: ExchangeRate? = null,
            description: String = Type.Transfer.name
        ): Result<Pair<Transaction, Transaction>, String> {

            var sourceTransferAmount = transferAmount
            var destinationTransferAmount = transferAmount

            if (sourceAccount.currency != destinationAccount.currency && exchangeRate != null) {
                if (transferAmount.currency == sourceAccount.balance.currency) {
                    sourceTransferAmount = transferAmount
                    destinationTransferAmount = transferAmount.convertCurrency(exchangeRate)
                }
                else {
                    sourceTransferAmount = transferAmount.convertCurrency(exchangeRate)
                    destinationTransferAmount = transferAmount
                }
            }

            when {
                sourceAccount == destinationAccount -> return Failure(
                    "Cannot transfer money to the same account"
                )
                sourceAccount.currency != destinationAccount.currency && exchangeRate == null -> return Failure(
                    "Error retrieving exchange rate"
                )
                sourceTransferAmount > sourceAccount.liquidBalance -> return Failure(
                    "Insufficient funds"
                )
            }

            val sourceTransaction = Transaction(
                accountId = sourceAccount.id,
                date = OffsetDateTime.now(),
                openingBalance = sourceAccount.balance,
                amount = -sourceTransferAmount,
                type = Type.Transfer,
                description = description
            )

            val destinationTransaction = Transaction(
                accountId = destinationAccount.id,
                date = OffsetDateTime.now(),
                openingBalance = destinationAccount.balance,
                amount = destinationTransferAmount,
                type = Type.Transfer,
                description = description
            )

            sourceAccount.balance = sourceAccount.balance - sourceTransferAmount
            sourceAccount.liquidBalance = sourceAccount.liquidBalance - sourceTransferAmount
            destinationAccount.balance = destinationAccount.balance + destinationTransferAmount
            destinationAccount.liquidBalance = destinationAccount.liquidBalance + destinationTransferAmount

            return Success(Pair(sourceTransaction, destinationTransaction))
        }

        fun purchaseStock(
            account: Account,
            stock: Stock,
            quote: Quote,
            orderVolume: Long,
            orderType: Order.Type,
            exchangeRate: ExchangeRate? = null,
            description: String = String.format("${Type.Investment.name} - ${stock.symbol}"),
            date: OffsetDateTime = OffsetDateTime.now()
        ): Result<Pair<Order, Transaction>, String> {

            val order: Order

            try {
                order = Order.create(account, stock, quote, orderVolume, orderType, exchangeRate)
            }
            catch(e: InvalidParameterException) {
                return Failure(e.message!!)
            }

            when {
                order.orderTotalPrice > account.liquidBalance -> return Failure(
                    "Insufficient funds"
                )
            }

            val transaction = Transaction(
                accountId = account.id,
                date = date,
                openingBalance = account.balance,
                amount = -order.orderTotalPrice,
                type = Type.Investment,
                description = description
            )

            account.liquidBalance = account.liquidBalance - order.orderTotalPrice

            return Success(Pair(order, transaction))
        }

        fun sellOrder(
            account: Account,
            order: Order,
            stock: Stock,
            quote: Quote,
            sellVolume: Long,
            exchangeRate: ExchangeRate? = null,
            description: String = String.format("${Type.Investment.name} - ${stock.symbol}"),
            date: OffsetDateTime = OffsetDateTime.now()
        ): Result<Pair<Order, Transaction>, String> {

            val sellOrder: Order

            if (sellVolume > order.orderVolume) {
                return Failure("Sell volume exceeds order volume")
            }

            try {
                sellOrder = Order.create(account, stock, quote, sellVolume, order.orderType, exchangeRate)
            }
            catch(e: InvalidParameterException) {
                return Failure(e.message!!)
            }

            val transaction = Transaction(
                accountId = account.id,
                date = date,
                openingBalance = account.balance,
                amount = sellOrder.orderTotalPrice,
                type = Type.Investment,
                description = description
            )

            account.balance = account.balance - (order.orderTotalPrice / order.orderVolume * sellVolume) + sellOrder.orderTotalPrice
            account.liquidBalance = account.liquidBalance + sellOrder.orderTotalPrice

            order.recalculateVolume(order.orderVolume - sellVolume)

            return Success(Pair(order, transaction))
        }

        fun sellStock(
            account: Account,
            stockOrders: List<Order>?,
            stock: Stock,
            quote: Quote,
            sellVolume: Long,
            exchangeRate: ExchangeRate? = null,
            description: String = String.format("${Type.Investment.name} - ${stock.symbol}"),
            date: OffsetDateTime = OffsetDateTime.now()
        ): Result<Pair<List<Order>, List<Transaction>>, String> {

            when {
                stockOrders.isNullOrEmpty() -> return Failure("No ${stock.symbol} stocks purchased in $account")
                stockOrders.map{ it.orderVolume }.sum() < sellVolume -> return Failure("Quantity exceeds number of ${stock.symbol} stocks in $account")
            }

            val transactions: MutableList<Transaction> = mutableListOf()
            val orders: MutableList<Order> = mutableListOf()
            var remainingSellVolume = sellVolume

            for (order in stockOrders!!) {
                val volumeToSell: Long

                if (remainingSellVolume < order.orderVolume) {
                    volumeToSell = remainingSellVolume
                    remainingSellVolume = 0L
                }
                else {
                    volumeToSell = order.orderVolume
                    remainingSellVolume -= order.orderVolume
                }

                when (val sellOrderResult = sellOrder(account, order, stock, quote, volumeToSell, exchangeRate, description, date)) {
                    is Failure -> return Failure(sellOrderResult.reason)
                    is Success -> {
                        transactions.add(sellOrderResult.value.second)
                        orders.add(order)
                    }
                }

                if (remainingSellVolume == 0L) {
                    break
                }
            }

            return Success(Pair(orders, transactions))
        }
    }
}