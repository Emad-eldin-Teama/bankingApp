package com.example.cache.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.TextView
import com.example.cache.R
import com.example.cache.domain.model.Product
import com.example.cache.ui.utils.DisplayUtils.toBulletedList

class AccountsProductsAdapter(
    private val context: Context,
    private val productList: List<Product>,
    val adapterOnClick: (Any) -> Unit
): BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val res = context.resources
        return when(productList[groupPosition]) {
            Product.ChequingVIP -> res.getStringArray(R.array.text_account_features_chequing_vip).toList()
            Product.ChequingNoLimit -> res.getStringArray(R.array.text_account_features_chequing_no_limit).toList()
            Product.ChequingDayToDay -> res.getStringArray(R.array.text_account_features_chequing_day_to_day).toList()
            Product.ChequingBorderless -> res.getStringArray(R.array.text_account_features_chequing_borderless).toList()
            Product.SavingsPremium -> res.getStringArray(R.array.text_account_features_savings_premium).toList()
            Product.SavingsEveryday -> res.getStringArray(R.array.text_account_features_savings_everyday).toList()
            Product.SavingsHighInterest -> res.getStringArray(R.array.text_account_features_savings_high_interest).toList()
        }
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val view = convertView ?: layoutInflater.inflate(R.layout.list_item_accounts_products_child, parent, false)

        val productFeatures: TextView = view.findViewById(R.id.product_features)
        productFeatures.text = (getChild(groupPosition, childPosition) as List<String>).toBulletedList()

        val buttonOpenAccount: Button = view.findViewById(R.id.button_open_account)
        buttonOpenAccount.setOnClickListener{ adapterOnClick(productList[groupPosition]) }

        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getGroup(groupPosition: Int): Any = productList[groupPosition]

    override fun getGroupCount(): Int = productList.size

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val view = convertView ?: layoutInflater.inflate(R.layout.list_item_accounts_products_group, parent, false)

        val productName: TextView = view.findViewById(R.id.product_name)
        productName.text = (getGroup(groupPosition) as Product).productName

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = false

}