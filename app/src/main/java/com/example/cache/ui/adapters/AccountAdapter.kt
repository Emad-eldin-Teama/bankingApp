package com.example.cache.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.data.repository.TransactionRepository.Companion.groupByDate
import com.example.cache.databinding.ExpandableContentAccountDetailsBinding
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Transaction
import com.jay.widget.StickyHeaders
import com.skydoves.expandablelayout.ExpandableLayout

class AccountAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaders {
    var account =  Account()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var transactions = listOf<Transaction>()
        set (value) {
            field = value
            notifyDataSetChanged()
        }

    private val rows = listOf(HeaderRow(), ContentRow())

    interface Row
    class HeaderRow(): Row
    class ContentRow(): Row

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accountType: TextView = itemView.findViewById(R.id.account_type)
        val accountRoutingNumber: TextView = itemView.findViewById(R.id.account_routing_number)
        val accountBalance: TextView = itemView.findViewById(R.id.account_balance)
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionsView: RecyclerView = itemView.findViewById(R.id.transactions_view)
        val accountDetails: ExpandableLayout = itemView.findViewById(R.id.account_details)
    }

    override fun getItemCount() = rows.count()

    override fun isStickyHeader(position: Int) = rows[position] is HeaderRow

    override fun getItemViewType(position: Int): Int =
        when(rows[position]) {
            is HeaderRow -> TYPE_HEADER
            is ContentRow -> TYPE_CONTENT
            else -> throw IllegalArgumentException()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_account_header, parent, false))
            TYPE_CONTENT -> ContentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_account_content, parent, false))
            else -> throw IllegalArgumentException()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when(holder.itemViewType) {
            TYPE_HEADER -> onBindHeader(holder)
            TYPE_CONTENT -> onBindContent(holder)
            else -> throw IllegalArgumentException()
        }

    private fun onBindHeader(holder: RecyclerView.ViewHolder) {
        val headerRow = holder as HeaderViewHolder
        headerRow.accountType.text = account.type.name
        headerRow.accountRoutingNumber.text = account.routingNumber
        headerRow.accountBalance.text = account.balance.toString()
    }

    private fun onBindContent(holder: RecyclerView.ViewHolder) {
        val contentRow = holder as ContentViewHolder

        val binding = DataBindingUtil.bind<ExpandableContentAccountDetailsBinding>(contentRow.accountDetails.secondLayout)
        binding?.account = account

        contentRow.accountDetails.setOnClickListener {
            when (contentRow.accountDetails.isExpanded) {
                true -> contentRow.accountDetails.collapse()
                false -> contentRow.accountDetails.expand()
             }
        }

        contentRow.transactionsView.adapter = TransactionsAdapter(getTransactionRows(transactions))
    }

    private fun getTransactionRows(transactions: List<Transaction>): List<TransactionsAdapter.Row> {
        val groupedTransactions = transactions.groupByDate()
        val rows = mutableListOf<TransactionsAdapter.Row>()

        for ((date, transactionsOnDate) in groupedTransactions) {
            rows.add(TransactionsAdapter.DateRow(date))

            transactionsOnDate.forEach {
                rows.add(TransactionsAdapter.TransactionRow(it))
            }
        }

        return rows
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }
}