package com.example.cache.di.module

import androidx.lifecycle.ViewModel
import com.example.cache.di.annotation.ViewModelKey
import com.example.cache.ui.account.AccountViewModel
import com.example.cache.ui.accounts.AccountsViewModel
import com.example.cache.ui.home.HomeViewModel
import com.example.cache.ui.investment.InvestmentViewModel
import com.example.cache.ui.transfer.TransferViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountsViewModel::class)
    abstract fun bindAccountsViewModel(accountsViewModel: AccountsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransferViewModel::class)
    abstract fun bindTransferViewModel(transferViewModel: TransferViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InvestmentViewModel::class)
    abstract fun bindStocksViewModel(investmentViewModel: InvestmentViewModel): ViewModel
}