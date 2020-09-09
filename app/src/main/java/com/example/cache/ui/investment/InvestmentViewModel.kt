package com.example.cache.ui.investment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cache.data.repository.AccountRepository
import com.example.cache.data.repository.OrderRepository
import com.example.cache.data.repository.TradingRepository
import com.example.cache.data.repository.TransactionRepository
import com.example.cache.domain.model.*
import com.example.cache.utils.Failure
import com.example.cache.utils.Result
import com.example.cache.utils.Success
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.math.BigDecimal
import java.security.InvalidParameterException
import javax.inject.Inject

class InvestmentViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val orderRepository: OrderRepository,
    private val transactionRepository: TransactionRepository,
    private val tradingRepository: TradingRepository
) : ViewModel() {
    val investmentAccounts = accountRepository.getInvestmentAccounts()
    val investments = MutableLiveData<List<Order>>()
    val orderTypes = Order.Type.values().toList()

    var searchStockResults = MutableLiveData<List<Stock>>()
    var searchStockError = MutableLiveData<String>()
    var selectedStock = MutableLiveData<Stock>()
    var selectedStockQuote = MutableLiveData<Quote>()
    var orderQuantityText = MutableLiveData<String>()
    var orderAccountPosition = MutableLiveData(0)
    var orderTypePosition = MutableLiveData(0)
    var orderExchangeRate: ExchangeRate = ExchangeRate()
    var orderAction: Order.Action = Order.Action.Buy

    var orderErrorMessage = MutableLiveData<String>()
    var orderConfirmationMessage = MutableLiveData<String>()
    var orderSuccessMessage = MutableLiveData<String>()

    var selectedOrder = MutableLiveData<Order>()

    fun getAccountInvestments(account: Account) {
        viewModelScope.launch {
            investments.value = orderRepository.getAllOrdersByAccount(account.id)
        }
    }

    fun searchStock(keyword: String) {
        viewModelScope.launch {
            when (val matchedStocks = tradingRepository.searchStock(keyword)) {
                is Failure -> {
                    searchStockError.value = matchedStocks.reason
                }
                is Success -> {
                    searchStockResults.value = matchedStocks.value
                }
            }
        }
    }

    fun quoteStock(symbol: String) {
        viewModelScope.launch {
            when (val quote = tradingRepository.quoteStock(symbol)) {
                is Failure -> { }
                is Success -> {
                    selectedStockQuote.value = quote.value
                }
            }
        }
    }

    fun placeOrder(action: Order.Action) {
        if (!validateOrderInputs()) return

        orderAction = action
        val account = investmentAccounts.value!![orderAccountPosition.value!!]

       if (selectedOrder.value != null) {
            selectedStock.value = Stock(
                symbol = selectedOrder.value!!.stockSymbol,
                name = selectedOrder.value!!.stockName,
                type = selectedOrder.value!!.stockType,
                currency = selectedOrder.value!!.stockCurrency
            )
        }

        if (selectedStock.value!!.currency != account.currency) {
            confirmExchangeRate(selectedStock.value!!.currency, account.currency)
            return
        }

        orderConfirmationMessage.value = "${orderAction.name} $orderQuantityText ${selectedStock.value!!.symbol} @ ${selectedStockQuote.value!!.price} ${selectedStock.value!!.currency} in $account?"
    }

    fun completeOrder() {
        viewModelScope.launch {
            val account = investmentAccounts.value!![orderAccountPosition.value!!]
            val quantity = orderQuantityText.value!!.toLong()
            val orderType = orderTypes[orderTypePosition.value!!]

            when (orderAction) {
                Order.Action.Buy -> {
                    val orderTransaction = Transaction.purchaseStock(
                        account,
                        selectedStock.value!!,
                        selectedStockQuote.value!!,
                        quantity,
                        orderType,
                        orderExchangeRate
                    )

                    when (orderTransaction) {
                        is Failure -> orderErrorMessage.value = orderTransaction.reason
                        is Success -> {
                            accountRepository.update(account)
                            orderRepository.insert(orderTransaction.value.first)
                            transactionRepository.insert(orderTransaction.value.second)

                            orderSuccessMessage.value = "Purchased ${orderQuantityText.value} ${selectedStock.value!!.symbol} @ ${selectedStockQuote.value!!.price} ${selectedStock.value!!.currency} in $account"
                        }
                    }
                }
                Order.Action.Sell -> {
                    if (selectedOrder.value != null) {
                        val transaction = Transaction.sellOrder(
                            account,
                            selectedOrder.value!!,
                            selectedStock.value!!,
                            selectedStockQuote.value!!,
                            quantity,
                            orderExchangeRate
                        )

                        when (transaction) {
                            is Failure -> orderErrorMessage.value = transaction.reason
                            is Success -> {
                                accountRepository.update(account)
                                transactionRepository.insert(transaction.value.second)
                                val order = transaction.value.first

                                if (order.orderVolume == 0L) {
                                    orderRepository.delete(order)
                                }
                                else {
                                    orderRepository.update(order)
                                }

                                orderSuccessMessage.value = "Sold ${orderQuantityText.value} ${selectedStock.value!!.symbol} @ ${selectedStockQuote.value!!.price} ${selectedStock.value!!.currency} in $account"
                            }
                        }
                    }
                    else {
                        val accountStockOrders = orderRepository.getStockOrdersByAccount(account.id, selectedStock.value!!.symbol)
                        val ordersTransactions = Transaction.sellStock(
                            account,
                            accountStockOrders,
                            selectedStock.value!!,
                            selectedStockQuote.value!!,
                            quantity,
                            orderExchangeRate
                        )

                        when (ordersTransactions) {
                            is Failure -> orderErrorMessage.value = ordersTransactions.reason
                            is Success -> {
                                accountRepository.update(account)
                                transactionRepository.insertAll(ordersTransactions.value.second)

                                for (order in ordersTransactions.value.first) {
                                    if (order.orderVolume == 0L) {
                                        orderRepository.delete(order)
                                    }
                                    else {
                                        orderRepository.update(order)
                                    }
                                }

                                orderSuccessMessage.value = "Sold ${orderQuantityText.value} ${selectedStock.value!!.symbol} @ ${selectedStockQuote.value!!.price} ${selectedStock.value!!.currency} in $account"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateOrderInputs(): Boolean {
        try {
            when {
                orderQuantityText.value.isNullOrBlank() -> throw InvalidParameterException("Quantity cannot be blank")
                orderQuantityText.value!!.toLongOrNull() == null -> throw InvalidParameterException("Invalid quantity")
                orderQuantityText.value!!.toLong() == 0L -> throw InvalidParameterException("Quantity cannot be 0")
            }
        }
        catch (e: InvalidParameterException) {
            orderErrorMessage.value = e.message
            return false
        }

        return true
    }

    private fun confirmExchangeRate(fromCurrency: String, toCurrency: String) {
        val account = investmentAccounts.value!![orderAccountPosition.value!!]

        viewModelScope.launch {
            when (val exchangeRateResult = tradingRepository.getExchangeRate(fromCurrency, toCurrency)) {
                is Failure -> orderErrorMessage.value = "Unable to retrieve exchange rate."
                is Success -> {
                    orderExchangeRate = exchangeRateResult.value
                    orderConfirmationMessage.value = "${orderAction.name} ${orderQuantityText.value!!} ${selectedStock.value!!.symbol} @ ${selectedStockQuote.value!!.price} ${selectedStock.value!!.currency} with an exchange rate of ${"%.2f".format(exchangeRateResult.value.exchangeRate)} in $account?"
                }
            }
        }
    }

}