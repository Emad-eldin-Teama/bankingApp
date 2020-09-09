package com.example.cache.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Money

class AccountsTotalsAdapter: RecyclerView.Adapter<AccountsTotalsAdapter.ViewHolder>() {
    var data = listOf<Money>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currency: TextView = itemView.findViewById(R.id.currency)
        val total: TextView = itemView.findViewById(R.id.total)

        fun bind(item: Money) {
            currency.text = item.displayCurrencyCode
            total.text = item.toString()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_totals, parent, false)

                return ViewHolder(
                    view
                )
            }
        }
    }

}
