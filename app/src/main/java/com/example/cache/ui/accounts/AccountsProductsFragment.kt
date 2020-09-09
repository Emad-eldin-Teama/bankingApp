package com.example.cache.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.cache.R
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Product
import com.example.cache.ui.adapters.AccountsProductsAdapter
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class AccountsProductsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var accountsViewModel: AccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_accounts_products, container, false)
        accountsViewModel = ViewModelProvider(this, viewModelFactory).get(AccountsViewModel::class.java)
        val accountType  = arguments?.getSerializable("accountType") as Account.Type

        val titleAccountsProducts: TextView = view.findViewById(R.id.title_accounts_products)
        val productsListAdapterData: List<Product>

        when(accountType) {
            Account.Type.Chequing -> {
                titleAccountsProducts.text = getString(R.string.title_accounts_products_chequing)
                productsListAdapterData = Product.ChequingAccountProducts
            }
            Account.Type.Savings -> {
                titleAccountsProducts.text = getString(R.string.title_accounts_products_savings)
                productsListAdapterData = Product.SavingsAccountProducts
            }
            else -> return null
        }

        val productsList: ExpandableListView = view.findViewById(R.id.products_list)
        val productsListAdapter = AccountsProductsAdapter(view.context, productsListAdapterData) { openAccountClick(it) }
        productsList.setAdapter(productsListAdapter)

        return view
    }

    private fun openAccountClick(item: Any) {
        val product = item as Product

        AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
            .setTitle(R.string.dialog_title_open_account_confirmation)
            .setMessage(String.format(resources.getString(R.string.dialog_message_open_account_confirmation), product.productName))
            .setPositiveButton(R.string.button_confirm) { _, _ -> accountsViewModel.openAccount(product) }
            .show()
    }
}