package com.petermunyao.mobileandroidchallenge.repository

import com.petermunyao.mobileandroidchallenge.model.ExchangeRates
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val localData: LocalData,
    private val remoteData: RemoteData
) {

    fun getCurrenciesLocal() = localData.getCurrencies()

    fun getExchangeRatesLocal() = localData.getRates()

    suspend fun getCurrenciesRemote() = remoteData.getCurrencies()

    suspend fun getExchangeRatesRemote() = remoteData.getExchangeRates()

    suspend fun insertCurrencies(supportedCurrencies: SupportedCurrencies) =
        localData.insertCurrencies(supportedCurrencies)

    suspend fun insertRates(exchangeRates: ExchangeRates) = localData.insertRates(exchangeRates)
}