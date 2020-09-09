package com.example.cache.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Stock

class StocksAdapter(
    private val adapterOnClick: (Any) -> Unit
): RecyclerView.Adapter<StocksAdapter.ViewHolder>() {

    var data = listOf<Stock>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, adapterOnClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder private constructor(
        itemView: View,
        private val adapterOnClick: (Any) -> Unit
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val stockSymbol: TextView = itemView.findViewById(R.id.stock_symbol)
        val stockName: TextView = itemView.findViewById(R.id.stock_name)
        lateinit var stock: Stock

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Stock) {
            stockSymbol.text = item.symbol
            stockName.text = item.name
            stock = item
        }

        override fun onClick(v: View?) {
            adapterOnClick(stock)
        }

        companion object {
            fun from(parent: ViewGroup, adapterOnClick: (Any) -> Unit): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_stocks, parent, false)

                return ViewHolder(view, adapterOnClick)
            }
        }
    }


}