package com.petermunyao.mobileandroidchallenge.repository

import com.petermunyao.mobileandroidchallenge.api.ApiInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteData @Inject constructor(
    private val apiInterface: ApiInterface
) {
    suspend fun getCurrencies() = apiInterface.getCurrencies()

    suspend fun getExchangeRates() = apiInterface.getExchangeRates()
}