package com.example.cache.ui.home

public class CompoundInterestCalc() {
    /*
         P is principal amount.
        R is the annual interest rate.
        t is the time the money is invested or borrowed for.
        n is the number of times that interest is compounded per unit t,
        for example if interest is compounded monthly and t is in years then the value of n would be 12.
        If interest is compounded quarterly and t is in years then the value of n would be 4.
         */
    fun calculate(p: Int, t: Int, r: Double, n: Int) {
        val amount = p * Math.pow(1 + r / n, n * t.toDouble())
        val cinterest = amount - p
        println("Compound Interest after $t years: $cinterest")
        println("Amount after $t years: $amount")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val obj = CompoundInterestCalc()
            obj.calculate(50000, 5, .18, 12)
            /*
        in this example we assumed the period of investment year is 5, the  compound interval is monthly...
        and  the capital is 50000$
         */
        }
    }
}