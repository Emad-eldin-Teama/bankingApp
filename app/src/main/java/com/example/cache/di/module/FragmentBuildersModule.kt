package com.example.cache.di.module

import com.example.cache.ui.account.AccountFragment
import com.example.cache.ui.accounts.AccountsFragment
import com.example.cache.ui.accounts.AccountsProductsFragment
import com.example.cache.ui.home.HomeFragment
import com.example.cache.ui.investment.InvestmentAccountFragment
import com.example.cache.ui.investment.InvestmentFragment
import com.example.cache.ui.investment.InvestmentQuoteFragment
import com.example.cache.ui.transfer.TransferFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelsModule::class])
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector
    abstract fun contributeAccountsFragment(): AccountsFragment

    @ContributesAndroidInjector
    abstract fun contributeAccountsProductsFragment(): AccountsProductsFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeTransferFragment(): TransferFragment

    @ContributesAndroidInjector
    abstract fun contributeInvestmentQuoteFragment(): InvestmentQuoteFragment

    @ContributesAndroidInjector
    abstract fun contributeInvestmentAccountFragment(): InvestmentAccountFragment
}