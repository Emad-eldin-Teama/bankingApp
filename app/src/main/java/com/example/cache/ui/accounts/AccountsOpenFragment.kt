package com.example.cache.ui.accounts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.domain.model.Account
import com.example.cache.ui.adapters.AccountsTypesAdapter

class AccountsOpenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_accounts_open, container, false)

        val accountTypesView: RecyclerView = view.findViewById(R.id.account_types_view)

        val accountsTypesAdapter = AccountsTypesAdapter()
        accountsTypesAdapter.data = listOf(Account.Type.Chequing, Account.Type.Savings)
        accountTypesView.adapter = accountsTypesAdapter

        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(view.context, R.drawable.list_divider)!!)
        accountTypesView.addItemDecoration(divider)

        return view
    }
}