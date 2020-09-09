package com.example.cache.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cache.R
import com.example.cache.domain.model.Account

class AccountsSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val accounts: List<Account>
): ArrayAdapter<Account>(context, resource, accounts) {

    override fun getCount() = accounts.size

    override fun getItem(position: Int): Account? = accounts[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = convertView ?: layoutInflater.inflate(resource, parent, false)
        val holder = view.tag as? ViewHolder ?: ViewHolder(view)
        view.tag = holder

        val account = accounts[position]
        holder.label.text = account.toString()
        holder.currency.text = account.balance.displayCurrencyCode
        holder.balance.text = account.balance.toString()

        return view
    }

    override fun getItemId(position: Int): Long = position.toLong()

    class ViewHolder(itemView: View) {
        val label: TextView = itemView.findViewById(R.id.account_label)
        val currency: TextView = itemView.findViewById(R.id.account_currency)
        val balance: TextView = itemView.findViewById(R.id.account_balance)
    }

}