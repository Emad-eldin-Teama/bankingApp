package com.example.cache.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.cache.R
import com.example.cache.data.repository.AccountRepository.Companion.totals
import com.example.cache.databinding.FragmentAccountsBinding
import com.example.cache.domain.model.Account
import com.example.cache.ui.adapters.AccountsAdapter
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class AccountsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentAccountsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_accounts, container, false)

        // Get a reference to the ViewModel associated with this fragment
        val accountsViewModel = ViewModelProvider(this, viewModelFactory).get(AccountsViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.accountsViewModel = accountsViewModel

        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.lifecycleOwner = this

        val bankingAdapter = AccountsAdapter()
        binding.bankingView.adapter = bankingAdapter

        val investmentsAdapter = AccountsAdapter()
        binding.investmentsView.adapter = investmentsAdapter

        accountsViewModel.accounts.observe(viewLifecycleOwner, Observer { accounts ->
            accounts?.let {
                bankingAdapter.data = accounts.filter { it.type in listOf(Account.Type.Chequing, Account.Type.Savings) }
                bankingAdapter.subData = accounts.totals(listOf(Account.Type.Chequing, Account.Type.Savings))
                investmentsAdapter.data = accounts.filter { it.type in listOf(Account.Type.TFSA, Account.Type.RRSP) }
                investmentsAdapter.subData = accounts.totals(listOf(Account.Type.TFSA, Account.Type.RRSP))
            }
        })

        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        binding.bankingView.addItemDecoration(divider)
        binding.investmentsView.addItemDecoration(divider)

        val onMoreButton = binding.root.findViewById<ImageButton>(R.id.button_more)
        onMoreButton.setOnClickListener { view ->
            val popup = PopupMenu(view.context, onMoreButton)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_open_account -> {
                        findNavController().navigate(R.id.navigation_accounts_open)
                    }
                    R.id.menu_clear -> accountsViewModel.onClear()
                }
                true
            }

            popup.menuInflater.inflate(R.menu.accounts_menu, popup.menu)
            popup.show()
        }

        return binding.root
    }
}
