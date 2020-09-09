package com.example.cache.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Account

class InvestmentAccountsAdapter(
    private val adapterOnClick: (Any) -> Unit
): RecyclerView.Adapter<InvestmentAccountsAdapter.ViewHolder>() {

    var data = listOf<Account>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, adapterOnClick)
    }

    class ViewHolder private constructor(
        itemView: View,
        private val adapterOnClick: (Any) -> Unit
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val accountName: TextView = itemView.findViewById(R.id.account_name)
        val accountTotal: TextView = itemView.findViewById(R.id.account_total)
        val accountSavings: TextView = itemView.findViewById(R.id.account_savings)
        lateinit var account: Account

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Account) {
            account = item
            accountName.text = item.toString()
            accountTotal.text = item.balance.toString()
            accountSavings.text = item.liquidBalance.toString()
        }

        override fun onClick(v: View?) {
            adapterOnClick(account)
        }

        companion object {
            fun from(parent: ViewGroup, adapterOnClick: (Any) -> Unit): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_investment_accounts, parent, false)

                return ViewHolder(view, adapterOnClick)
            }
        }
    }




}