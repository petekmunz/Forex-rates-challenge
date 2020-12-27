package com.petermunyao.mobileandroidchallenge.viewmodels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.petermunyao.mobileandroidchallenge.model.ExchangeRates
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import com.petermunyao.mobileandroidchallenge.repository.Repository
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {

    var currenciesList: MutableList<String> = ArrayList()
    var rates: MutableMap<String, Float> = HashMap()

    fun getRemoteCurrencies() {
        viewModelScope.launch {
            try {
                val response = repository.getCurrenciesRemote()
                if (response.success == true && response.error == null) {
                    val supportedCurrencies = SupportedCurrencies(response.currencies!!)
                    repository.insertCurrencies(supportedCurrencies)
                }
            } catch (exception: Exception) {

            }
        }
    }

    fun getRemoteExchangeRates() {
        viewModelScope.launch {
            try {
                val response = repository.getExchangeRatesRemote()
                if (response.success == true && response.error == null) {
                    val exchangeRates = ExchangeRates(response.quotes!!, Date())
                    repository.insertRates(exchangeRates)
                }
            } catch (exception: Exception) {
            }
        }
    }

    fun getLocalCurrencies() = repository.getCurrenciesLocal().asLiveData()

    fun getLocalExchangeRates() = repository.getExchangeRatesLocal().asLiveData()

    fun isThirtyMinsPassed(date: Date): Boolean {
        val timeNow = Calendar.getInstance()
        val referenceTime = Calendar.getInstance()
        referenceTime.time = date
        referenceTime.add(Calendar.MINUTE, 30)
        return referenceTime.before(timeNow)
    }

    fun refreshExchangeRates(date: Date) {
        if (isThirtyMinsPassed(date)) {
            getRemoteExchangeRates()
        }
    }

    fun setCurrenciesToListInMemory(currenciesMap: Map<String, String>) {
        currenciesList.clear()
        currenciesList.addAll(currenciesMap.map {
            "${it.value} ${it.key}"
        })
    }

    fun setCurrentRatesToMemory(exchangeRates: Map<String, Float>) {
        rates.clear()
        rates.putAll(exchangeRates)
    }

    fun getExchangeRate(currencyCode: String): Float? {
        return rates["USD${currencyCode}"]
    }
}