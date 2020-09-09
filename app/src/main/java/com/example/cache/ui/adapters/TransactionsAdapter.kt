package com.example.cache.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Transaction

class TransactionsAdapter(private val rows: List<Row>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Row
    class DateRow(val date: String): Row
    class TransactionRow(val transaction: Transaction): Row

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transaction_date: TextView = itemView.findViewById(R.id.transaction_date)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transaction_description: TextView = itemView.findViewById(R.id.transaction_description)
        val transaction_amount: TextView = itemView.findViewById(R.id.transaction_amount)
    }

    override fun getItemCount() = rows.count()

    override fun getItemViewType(position: Int): Int =
        when(rows[position]) {
            is DateRow -> TYPE_DATE
            is TransactionRow -> TYPE_TRANSACTION
            else -> throw IllegalArgumentException()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_DATE -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_transactions_date, parent, false))
            TYPE_TRANSACTION -> TransactionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_transactions, parent, false))
            else -> throw IllegalArgumentException()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when(holder.itemViewType) {
            TYPE_DATE -> onBindDate(holder, rows[position] as DateRow)
            TYPE_TRANSACTION -> onBindTransaction(holder, rows[position] as TransactionRow)
            else -> throw IllegalArgumentException()
        }

    private fun onBindDate(holder: RecyclerView.ViewHolder, row: DateRow) {
        val dateRow = holder as HeaderViewHolder
        dateRow.transaction_date.text = row.date
    }

    private fun onBindTransaction(holder: RecyclerView.ViewHolder, row: TransactionRow) {
        val transactionRow = holder as TransactionViewHolder
        transactionRow.transaction_description.text = row.transaction.description
        transactionRow.transaction_amount.text = row.transaction.amount.toString()
    }

    companion object {
        private const val TYPE_DATE = 0
        private const val TYPE_TRANSACTION = 1
    }
}