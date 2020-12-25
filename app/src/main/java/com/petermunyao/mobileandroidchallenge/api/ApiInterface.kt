package com.petermunyao.mobileandroidchallenge.api

import com.petermunyao.mobileandroidchallenge.model.CurrenciesResponse
import com.petermunyao.mobileandroidchallenge.model.ExchangeRateResponse
import retrofit2.http.GET

interface ApiInterface {
    @GET("list")
    suspend fun getCurrencies(): CurrenciesResponse

    @GET("live")
    suspend fun getExchangeRates(): ExchangeRateResponse
}