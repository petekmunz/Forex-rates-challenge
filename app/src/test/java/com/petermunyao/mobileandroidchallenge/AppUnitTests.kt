package com.petermunyao.mobileandroidchallenge

import com.petermunyao.mobileandroidchallenge.repository.LocalData
import com.petermunyao.mobileandroidchallenge.repository.RemoteData
import com.petermunyao.mobileandroidchallenge.repository.Repository
import com.petermunyao.mobileandroidchallenge.viewmodels.MainViewModel
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.mock
import java.io.IOException
import java.util.*

class AppUnitTests {
    private lateinit var viewModel: MainViewModel

    @Before
    fun initializeViewModel() {
        val localData = mock(LocalData::class.java)
        val remoteData = mock(RemoteData::class.java)
        val repository = Repository(localData, remoteData)
        viewModel = MainViewModel(repository)
        viewModel.rates = mapOf("USDJPY" to 103.5F, "USDKES" to 108.3F) as MutableMap<String, Float>
    }

    @Test
    fun ensureTokenRefreshIsOnlyAfter30Mins() {
        val today = Date()
        today.time = today.time - (31 * 60000) //Subtract 31 mins from the current time
        assertThat(viewModel.isThirtyMinsPassed(today), `is`(true))
    }

    @Test
    fun getExchangeRatesReturnsCorrectFloat() {
        assertThat(viewModel.getExchangeRate("JPY"), `is`(103.5F))
    }

    @Test
    fun currencyConversionIsCorrect() {
        assertThat(viewModel.calculateOtherCurrencyValue("2", "KES"), `is`("216.6"))
    }

    @Test
    fun exceptionHandlingIsCorrect() {
        val method =
            viewModel.javaClass.getDeclaredMethod("handleCommonExceptions", Exception::class.java)
        method.isAccessible = true
        val exception = IOException()
        assertThat(
            method.invoke(viewModel, exception),
            `is`("There was a network error, check your internet connection & retry")
        )
    }

    @Test
    fun ratesAreSetCorrectly() {
        val newRates = mapOf("USDCAD" to 1.29F, "USDBGN" to 1.61F) as MutableMap<String, Float>
        viewModel.setCurrentRatesToMemory(newRates)
        assertThat(viewModel.rates["USDBGN"], `is`(1.61F))
    }

    @Test
    fun currenciesAreSetInListCorrectly() {
        val currencies = mapOf("JPY" to "Japanese Yen", "KSH" to "Kenyan Shillings")
        viewModel.setCurrenciesToListInMemory(currencies)
        assertThat(viewModel.currenciesList[0], `is`("Japanese Yen JPY"))
    }

    @Test
    fun refreshOperationIsCorrect() {
        val date = Date()
        assertThat(viewModel.refreshExchangeRates(date), `is`(false))
    }
}