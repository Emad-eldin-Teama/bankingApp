package com.example.cache.ui.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.cache.R
import com.example.cache.databinding.FragmentInvestmentQuoteBinding
import com.example.cache.domain.model.Account
import com.example.cache.domain.model.Order
import com.example.cache.domain.model.Stock
import com.example.cache.ui.adapters.AccountsSpinnerAdapter
import com.example.cache.ui.adapters.StocksAdapter
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import javax.inject.Inject

class InvestmentQuoteFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var investmentViewModel: InvestmentViewModel

    private var queryTextChangedJob: Job? = null

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var progressBar: ProgressBar
    private lateinit var stockDetailsView: View
    private lateinit var stockOrderView: View
    private lateinit var searchView: SearchView
    private lateinit var searchResultsView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentInvestmentQuoteBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_investment_quote, container, false)

        investmentViewModel = ViewModelProvider(this, viewModelFactory).get(InvestmentViewModel::class.java)
        binding.investmentViewModel = investmentViewModel
        binding.lifecycleOwner = this

        stockDetailsView = binding.root.findViewById(R.id.stock_details_view)
        stockOrderView = binding.root.findViewById(R.id.stock_order_view)
        searchView = binding.root.findViewById(R.id.search_view)
        searchResultsView = binding.root.findViewById(R.id.search_results_view)

        viewFlipper = binding.root.findViewById(R.id.view_flipper)
        progressBar = binding.root.findViewById(R.id.progress)
        progressBar.visibility = View.GONE

        configureSearchView()
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
            viewFlipper.displayedChild = 2
        }
    }

    private fun configureSearchView() {
        val stocksAdapter = StocksAdapter() { stockClick(it) }
        searchResultsView.adapter = stocksAdapter

        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!)
        searchResultsView.addItemDecoration(divider)

        investmentViewModel.searchStockError.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        investmentViewModel.searchStockResults.observe(viewLifecycleOwner, Observer { stocks ->
            stocks?.let{
                stocksAdapter.data = stocks
                progressBar.visibility = View.GONE
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    queryTextChangedJob?.cancel()

                    viewFlipper.displayedChild = 0
                    progressBar.visibility = View.VISIBLE

                    queryTextChangedJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        investmentViewModel.searchStock(newText)
                    }
                }

                return false
            }
        })
    }

    private fun stockClick(item: Any) {
        val stock = item as Stock
        investmentViewModel.selectedStock.value = stock
        investmentViewModel.quoteStock(stock.symbol)

        progressBar.visibility = View.VISIBLE

        investmentViewModel.selectedStockQuote.observe(viewLifecycleOwner, Observer {
            progressBar.visibility = View.GONE
            viewFlipper.displayedChild = 1
        })

    }
}