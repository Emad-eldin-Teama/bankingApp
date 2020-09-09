package com.example.cache.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Order

class InvestmentsAdapter(
    private val adapterOnClick: (Any) -> Unit
): RecyclerView.Adapter<InvestmentsAdapter.ViewHolder>() {

    var data = listOf<Order>()
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
        val stockSymbol: TextView = itemView.findViewById(R.id.stock_symbol)
        val stockName: TextView = itemView.findViewById(R.id.stock_name)
        val stockType: TextView = itemView.findViewById(R.id.stock_type)
        val orderVolume: TextView = itemView.findViewById(R.id.order_volume)
        val stockPrice: TextView = itemView.findViewById(R.id.stock_price)
        val orderTotal: TextView = itemView.findViewById(R.id.order_total)
        lateinit var order: Order

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Order) {
            order = item
            stockSymbol.text = item.stockSymbol
            stockName.text = item.stockName
            stockType.text = item.stockType
            orderVolume.text = item.orderVolume.toString()
            stockPrice.text = (item.orderTotalPrice / item.orderVolume).toString()
            orderTotal.text = item.orderTotalPrice.toString()
        }

        override fun onClick(v: View?) {
            adapterOnClick(order)
        }

        companion object {
            fun from(parent: ViewGroup, adapterOnClick: (Any) -> Unit): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_investments, parent, false)

                return ViewHolder(view, adapterOnClick)
            }
        }
    }

}