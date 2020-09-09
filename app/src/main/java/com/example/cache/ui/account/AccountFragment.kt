package com.example.cache.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.databinding.FragmentAccountBinding
import com.example.cache.ui.adapters.AccountAdapter
import com.jay.widget.StickyHeadersLinearLayoutManager
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class AccountFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var accountViewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAccountBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_account, container, false)

        accountViewModel = ViewModelProvider(this, viewModelFactory).get(AccountViewModel::class.java)
        arguments?.getLong("accountId")?.let { accountViewModel.getAccountWithTransactions(it) }

        binding.accountViewModel = accountViewModel
        binding.lifecycleOwner = this

        val accountView = binding.root.findViewById<RecyclerView>(R.id.account_view)
        accountView.layoutManager = StickyHeadersLinearLayoutManager<AccountAdapter>(this.context)

        val accountAdapter = AccountAdapter()
        accountView.adapter = accountAdapter

        accountViewModel.account.observe(viewLifecycleOwner, Observer { accountAdapter.account = it })
        accountViewModel.transactions.observe(viewLifecycleOwner, Observer { accountAdapter.transactions = it })

        return binding.root
    }
}
