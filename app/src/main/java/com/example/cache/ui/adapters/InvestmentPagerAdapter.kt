package com.example.cache.ui.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.cache.R
import com.example.cache.ui.investment.InvestmentAccountFragment
import com.example.cache.ui.investment.InvestmentQuoteFragment
import com.example.cache.ui.investment.InvestmentWatchlistFragment

private val TAB_TITLES = arrayOf(
    R.string.title_investment_tab_account,
    R.string.title_investment_tab_watchlist,
    R.string.title_investment_tab_quote
)

class InvestmentPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> InvestmentAccountFragment()
            1 -> InvestmentWatchlistFragment()
            else -> InvestmentQuoteFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}