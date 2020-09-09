package com.example.cache.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Money
import com.example.cache.domain.model.Account
import kotlinx.android.synthetic.main.list_item_accounts_summary.view.*
import java.lang.Exception


class AccountsAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data =  listOf<Account>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var subData = listOf<Money>()
        set (value) {
            field = value
            notifyDataSetChanged()
        }

    enum class ViewTypes(val value: Int) { SUMMARY_VIEW(1)}

    override fun getItemCount(): Int = data.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == data.size) return ViewTypes.SUMMARY_VIEW.value
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == ViewTypes.SUMMARY_VIEW.value) {
            return SummaryViewHolder.from(parent)
        }

        return AccountViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            when (holder) {
                is AccountViewHolder -> {
                    val item = data[position]
                    holder.bind(item)
                }
                is SummaryViewHolder -> {
                    holder.bind(subData)
                }
            }
        }
        catch (e: Exception) {}
    }

    open class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    class AccountViewHolder private constructor(itemView: View) : ViewHolder(itemView), View.OnClickListener {
        val accountType: TextView = itemView.findViewById(R.id.account_type)
        val accountNumberLast4Digits: TextView = itemView.findViewById(R.id.account_number_last_4_digits)
        val accountCurrency: TextView = itemView.findViewById((R.id.account_currency))
        val accountBalance: TextView = itemView.findViewById(R.id.account_balance)
        var accountId: Long? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Account) {
            val res = itemView.context.resources
            accountType.text = item.type.name
            accountNumberLast4Digits.text = String.format(res.getString(R.string.text_account_number_last_4_digits), item.accountNumberLast4Digits)
            accountCurrency.text = item.balance.displayCurrencyCode
            accountBalance.text = item.balance.toString()
            accountId = item.id
        }

        override fun onClick(v: View?) {
            val bundle = bundleOf("accountId" to accountId)
            v?.findNavController()?.navigate(R.id.navigation_account, bundle)
        }

        companion object {
            fun from(parent: ViewGroup): AccountViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_accounts, parent, false)

                return AccountViewHolder(view)
            }
        }
    }

    class SummaryViewHolder private constructor(itemView: View) : ViewHolder(itemView) {
        val totalsView: RecyclerView = itemView.findViewById(R.id.totals_view)

        fun bind(subData: List<Money>) {
            (totalsView.adapter as AccountsTotalsAdapter).data = subData
        }

        companion object {
            fun from(parent: ViewGroup): SummaryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_accounts_summary, parent, false)

                val subAdapter = AccountsTotalsAdapter()
                view.totals_view.adapter = subAdapter

                return SummaryViewHolder(view)
            }
        }
    }
}