package com.example.cache.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Account

class AccountsTypesAdapter: RecyclerView.Adapter<AccountsTypesAdapter.ViewHolder>() {

    var data = listOf<Account.Type>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var accountTypeItem: Account.Type
        val accountType: TextView = itemView.findViewById(R.id.account_type)
        val accountTypeDescription: TextView = itemView.findViewById(R.id.account_type_description)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Account.Type) {
            val res = itemView.context.resources
            accountType.text = item.name
            accountTypeDescription.text = when(item) {
                Account.Type.Chequing -> R.string.text_chequing_description
                Account.Type.Savings -> R.string.text_savings_description
                else -> null
            }?.let { res.getString(it) }
            accountTypeItem = item
        }

        override fun onClick(v: View?) {
            val bundle = Bundle()
            bundle.putSerializable("accountType", accountTypeItem)
            v?.findNavController()?.navigate(R.id.navigation_accounts_products, bundle)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_accounts_types, parent, false)

                return ViewHolder(view)
            }
        }
    }
}