package com.example.cache.ui.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cache.R
import com.example.cache.databinding.FragmentTransferBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import com.example.cache.ui.adapters.AccountsSpinnerAdapter
import kotlinx.android.synthetic.main.fragment_transfer.*

class TransferFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var transferViewModel: TransferViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentTransferBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_transfer, container, false)

        // Get a reference to the ViewModel associated with this fragment
        transferViewModel = ViewModelProvider(this, viewModelFactory).get(TransferViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.transferViewModel = transferViewModel

        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.lifecycleOwner = this

        val fromAccountSpinner: Spinner = binding.root.findViewById(R.id.from_account_field)
        val toAccountSpinner: Spinner = binding.root.findViewById((R.id.to_account_field))

        transferViewModel.accounts.observe(viewLifecycleOwner, Observer {
            val adapter = AccountsSpinnerAdapter(binding.root.context, R.layout.spinner_account_item, it)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            fromAccountSpinner.adapter = adapter
            toAccountSpinner.adapter = adapter
            transferViewModel.fromAccountItemPosition.observe(viewLifecycleOwner, Observer { setAmountCurrencyToggle() })
            transferViewModel.toAccountItemPosition.observe(viewLifecycleOwner, Observer { setAmountCurrencyToggle() })
        })

        transferViewModel.transferErrorMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(binding.root.context, R.style.AlertDialogStyle)
                .setTitle(R.string.dialog_title_transfer_incomplete)
                .setMessage(it)
                .setPositiveButton(R.string.button_ok, null)
                .show()
        })

        transferViewModel.transferConfirmationMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(binding.root.context, R.style.AlertDialogStyle)
                .setTitle(R.string.dialog_title_transfer_confirmation)
                .setMessage(it)
                .setPositiveButton(R.string.button_confirm) { _, _ -> transferViewModel.completeTransfer() }
                .show()
        })

        transferViewModel.transferSuccessMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(binding.root.context, R.style.AlertDialogStyle)
                .setTitle(R.string.dialog_title_transfer_complete)
                .setMessage(it)
                .setPositiveButton(R.string.button_ok, null)
                .show()
        })

        return binding.root
    }

    private fun setAmountCurrencyToggle() {
        val currencies = transferViewModel.getCurrencies()

        if (currencies.first != currencies.second) {
            amount_currency_toggle.textOn = currencies.first.currencyCode
            amount_currency_toggle.textOff = currencies.second.currencyCode
            amount_currency_toggle.isChecked = true
            amount_currency_toggle.visibility = View.VISIBLE
        }
        else {
            amount_currency_toggle.visibility = View.GONE
        }
    }

}
