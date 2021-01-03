package com.petermunyao.mobileandroidchallenge.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.petermunyao.mobileandroidchallenge.model.ExchangeRateInfo
import com.petermunyao.mobileandroidchallenge.model.ExchangeRates
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import com.petermunyao.mobileandroidchallenge.repository.Repository
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.round

class MainViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {

    lateinit var currenciesList: Array<String>
    var rates: MutableMap<String, Float> = HashMap()
    var lastRefreshTime: Date? = null
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

    fun isCurrenciesListInitialized() = ::currenciesList.isInitialized

    fun getRemoteCurrencies() {
        viewModelScope.launch {
            try {
                val response = repository.getCurrenciesRemote()
                if (response.success == true && response.error == null) {
                    val supportedCurrencies = SupportedCurrencies(response.currencies!!)
                    repository.insertCurrencies(supportedCurrencies)
                } else {
                    if (response.error != null) {
                        errorLiveData.value = response.error?.info
                    }
                }
            } catch (exception: Exception) {
                errorLiveData.value = handleCommonExceptions(exception)
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
                } else {
                    if (response.error != null) {
                        errorLiveData.value = response.error?.info
                    }
                }
            } catch (exception: Exception) {
                errorLiveData.value = handleCommonExceptions(exception)
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

    fun refreshExchangeRates(date: Date): Boolean {
        return if (isThirtyMinsPassed(date)) {
            getRemoteExchangeRates()
            true
        } else {
            false
        }
    }

    fun setCurrenciesToListInMemory(currenciesMap: Map<String, String>) {
        if (!isCurrenciesListInitialized()) {
            currenciesList = currenciesMap.map {
                "${it.key} ${it.value}"
            }.toTypedArray()
        }
    }

    fun setCurrentRatesToMemory(exchangeRates: Map<String, Float>) {
        rates.clear()
        rates.putAll(exchangeRates)
    }

    fun calculateAmountFromRate(
        primaryCurrency: String,
        secondaryCurrency: String,
        amount: String
    ): String {
        val primaryRate = rates["USD$primaryCurrency"]
        val secondaryRate = rates["USD$secondaryCurrency"]
        return if (primaryRate != null && secondaryRate != null) {
            val effectiveRate = secondaryRate / primaryRate
            val result = effectiveRate * amount.toFloat()
            (round(result * 1000) / 1000).toString()
        } else {
            ""
        }

    }

    private fun handleCommonExceptions(exception: Exception): String {
        return when (exception) {
            is SocketTimeoutException -> {
                "Could not make a connection in time, check your Internet Connection"
            }
            is IOException -> {
                "There was a network error, check your internet connection & retry"
            }
            else -> exception.message ?: "There was an unforeseen error"
        }
    }

    fun getExchangeRatesForSelectedCurrency(currencyCode: String): List<ExchangeRateInfo> {
        val usdToSelected = rates["USD$currencyCode"]
        return if (usdToSelected != null) {
            val exchangeInfo = rates.map {
                ExchangeRateInfo(
                    currencyCode,
                    it.key.substringAfter("USD"),
                    (round((it.value / usdToSelected) * 1000) / 1000).toString(),
                    (round((usdToSelected / it.value) * 1000) / 1000).toString()
                )
            } as MutableList
            exchangeInfo.sortBy { it.otherCurrency }
            exchangeInfo
        } else {
            emptyList()
        }
    }
}