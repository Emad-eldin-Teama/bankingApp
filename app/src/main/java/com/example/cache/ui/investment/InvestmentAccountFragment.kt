package com.example.cache.ui.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.ViewFlipper
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.databinding.FragmentInvestmentAccountBinding
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Order
import com.example.cache.ui.adapters.AccountsSpinnerAdapter
import com.example.cache.ui.adapters.InvestmentAccountsAdapter
import com.example.cache.ui.adapters.InvestmentsAdapter
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class InvestmentAccountFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var investmentViewModel: InvestmentViewModel

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var investmentAccountsView: View
    private lateinit var investmentsView: View
    private lateinit var investmentsAdapter: InvestmentsAdapter
    private lateinit var stockDetailsView: View
    private lateinit var stockOrderView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentInvestmentAccountBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_investment_account, container, false
        )

        investmentViewModel = ViewModelProvider(this, viewModelFactory).get(InvestmentViewModel::class.java)
        binding.investmentViewModel = investmentViewModel
        binding.lifecycleOwner = this

        viewFlipper = binding.root.findViewById(R.id.view_flipper)
        investmentAccountsView = binding.root.findViewById(R.id.investment_accounts_view)
        investmentsView = binding.root.findViewById(R.id.investments_view)
        stockDetailsView = binding.root.findViewById(R.id.stock_details_view)
        stockOrderView = binding.root.findViewById(R.id.stock_order_view)

        configureInvestmentAccountsView()
        configureInvestmentsView()
        configureStockDetailsView()
        configureStockOrderView()

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        investmentViewModel.orderErrorMessage.removeObservers(this)
        investmentViewModel.orderSuccessMessage.removeObservers(this)
        investmentViewModel.orderConfirmationMessage.removeObservers(this)
        investmentViewModel.orderErrorMessage = MutableLiveData<String>()
        investmentViewModel.orderConfirmationMessage = MutableLiveData<String>()
        investmentViewModel.orderSuccessMessage = MutableLiveData<String>()
        investmentViewModel.selectedOrder.value = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewFlipper.displayedChild > 0) {
                viewFlipper.displayedChild = viewFlipper.displayedChild - 1
            }
            else {
                findNavController().popBackStack()
            }
        }
    }

    private fun configureStockOrderView() {
        val orderTypeSpinner: Spinner = stockOrderView.findViewById(R.id.order_type)
        val orderTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, investmentViewModel.orderTypes)
        orderTypeSpinner.adapter = orderTypeAdapter

        val accountSpinner: Spinner = stockOrderView.findViewById(R.id.account)
        investmentViewModel.investmentAccounts.observe(viewLifecycleOwner, Observer {
            val accountsSpinnerAdapter = AccountsSpinnerAdapter(requireContext(), R.layout.spinner_account_item, it)
            accountsSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            accountSpinner.adapter = accountsSpinnerAdapter
        })

        val buttonBuy: Button = stockOrderView.findViewById(R.id.button_buy)
        buttonBuy.setOnClickListener {
            investmentViewModel.placeOrder(Order.Action.Buy)
        }

        val buttonSell: Button = stockOrderView.findViewById(R.id.button_sell)
        buttonSell.setOnClickListener {
            investmentViewModel.placeOrder(Order.Action.Sell)
        }

        investmentViewModel.orderErrorMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
                .setTitle(R.string.dialog_title_order_incomplete)
                .setMessage(it)
                .setPositiveButton(R.string.button_ok, null)
                .show()
        })

        investmentViewModel.orderConfirmationMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
                .setTitle(R.string.dialog_title_order_confirmation)
                .setMessage(it)
                .setPositiveButton(R.string.button_confirm) { _, _ -> investmentViewModel.completeOrder() }
                .show()
        })

        investmentViewModel.orderSuccessMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
                .setTitle(R.string.dialog_title_order_complete)
                .setMessage(it)
                .setPositiveButton(R.string.button_ok, null)
                .show()
        })
    }

    private fun configureStockDetailsView() {
        val buttonBuySell: Button = stockDetailsView.findViewById(R.id.button_buy_sell)
        buttonBuySell.setOnClickListener {
            viewFlipper.displayedChild = 3
        }
    }

    private fun configureInvestmentsView() {
        val investmentsList: RecyclerView = investmentsView.findViewById(R.id.investments_list)
        investmentsAdapter = InvestmentsAdapter() { investmentClick(it) }
        investmentsList.adapter = investmentsAdapter

        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(investmentsView.context, R.drawable.list_divider)!!)
        investmentsList.addItemDecoration(divider)
    }

    private fun configureInvestmentAccountsView() {
        val investmentAccountsList: RecyclerView = investmentAccountsView.findViewById(R.id.investment_accounts_list)
        val investmentAccountsAdapter = InvestmentAccountsAdapter() { investmentAccountClick(it) }
        investmentAccountsList.adapter = investmentAccountsAdapter

        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(investmentAccountsView.context, R.drawable.list_divider)!!)
        investmentAccountsList.addItemDecoration(divider)

        investmentViewModel.investmentAccounts.observe(viewLifecycleOwner, Observer {
            investmentAccountsAdapter.data = it
        })
    }

    private fun investmentAccountClick(item: Any) {
        val account = item as Account
        investmentViewModel.getAccountInvestments(account)

        investmentViewModel.investments.observe(viewLifecycleOwner, Observer {
            investmentsAdapter.data = it
            viewFlipper.displayedChild = 1
        })
    }

    private fun investmentClick(item: Any) {
        val order = item as Order
        investmentViewModel.selectedOrder.value = order
        investmentViewModel.quoteStock(order.stockSymbol)

        investmentViewModel.selectedStockQuote.observe(viewLifecycleOwner, Observer {
            viewFlipper.displayedChild = 2
        })

    }

}