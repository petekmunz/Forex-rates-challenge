package com.petermunyao.mobileandroidchallenge.repository

import com.petermunyao.mobileandroidchallenge.database.CurrenciesDao
import com.petermunyao.mobileandroidchallenge.model.ExchangeRates
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalData @Inject constructor(private val currenciesDao: CurrenciesDao) {

    suspend fun insertRates(exchangeRates: ExchangeRates) =
        currenciesDao.insertRates(exchangeRates)

    suspend fun insertCurrencies(supportedCurrencies: SupportedCurrencies) =
        currenciesDao.insertCurrencies(supportedCurrencies)

    fun getRates(): Flow<ExchangeRates?> = currenciesDao.getRates()

    fun getCurrencies(): Flow<SupportedCurrencies?> = currenciesDao.getCurrencies()
}