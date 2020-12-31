package com.petermunyao.mobileandroidchallenge.model

data class ExchangeRateInfo(
    var referenceCurrency: String,
    var otherCurrency: String,
    var refCurrencyToOtherRate: String,
    var otherCurrencyTORefRate: String
)