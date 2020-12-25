package com.petermunyao.mobileandroidchallenge.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.petermunyao.mobileandroidchallenge.model.ExchangeRates
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import com.petermunyao.mobileandroidchallenge.repository.Repository
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {

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

    private fun getRemoteExchangeRates() {
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
}