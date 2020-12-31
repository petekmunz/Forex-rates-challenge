package com.petermunyao.mobileandroidchallenge.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object ViewBindingAdapter {

    @JvmStatic
    @BindingAdapter("referenceCurrency", "otherCurrency", "refToOtherRate", requireAll = true)
    fun setReferenceCurrencyInfo(
        textView: TextView,
        referenceCurrency: String,
        otherCurrency: String,
        exchangeRate: String
    ) {
        val textToDisplay = "1 $referenceCurrency = $exchangeRate $otherCurrency"
        textView.text = textToDisplay
    }

    @JvmStatic
    @BindingAdapter("referenceCurrency", "otherCurrency", "otherToRefRate", requireAll = true)
    fun setOtherCurrencyInfo(
        textView: TextView,
        referenceCurrency: String,
        otherCurrency: String,
        exchangeRate: String
    ) {
        val textToDisplay = "1 $otherCurrency = $exchangeRate $referenceCurrency"
        textView.text = textToDisplay
    }
}