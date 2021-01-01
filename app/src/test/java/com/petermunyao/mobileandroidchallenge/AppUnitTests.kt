package com.petermunyao.mobileandroidchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.petermunyao.mobileandroidchallenge.repository.LocalData
import com.petermunyao.mobileandroidchallenge.repository.RemoteData
import com.petermunyao.mobileandroidchallenge.repository.Repository
import com.petermunyao.mobileandroidchallenge.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.io.IOException
import java.util.*

@ExperimentalCoroutinesApi
class AppUnitTests {
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun initializeViewModel() {
        Dispatchers.setMain(testDispatcher)
        val localData = mock(LocalData::class.java)
        val remoteData = mock(RemoteData::class.java)
        val repository = Repository(localData, remoteData)
        viewModel = MainViewModel(repository)
        viewModel.rates = mapOf(
            "USDUSD" to 1F,
            "USDJPY" to 103.5F,
            "USDKES" to 108.3F
        ) as MutableMap<String, Float>
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `ensure token refresh is only after 30 minutes`() {
        val today = Date()
        today.time = today.time - (31 * 60000) //Subtract 31 mins from the current time
        assertThat(viewModel.isThirtyMinsPassed(today), `is`(true))
    }

    @Test
    fun `currency conversion is correct, expect correct string representation of calculation`() {
        assertThat(viewModel.calculateAmountFromRate("USD", "KES", "2"), `is`("216.6"))
    }

    @Test
    fun `exception handling is correct, expect a string message of the exception`() {
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
    fun `rates are set correctly in cache`() {
        val newRates = mapOf("USDCAD" to 1.29F, "USDBGN" to 1.61F) as MutableMap<String, Float>
        viewModel.setCurrentRatesToMemory(newRates)
        assertThat(viewModel.rates["USDBGN"], `is`(1.61F))
    }

    @Test
    fun `currencies are set in cache correctly`() {
        val currencies = mapOf("JPY" to "Japanese Yen", "KSH" to "Kenyan Shillings")
        viewModel.setCurrenciesToListInMemory(currencies)
        assertThat(viewModel.currenciesList[0], `is`("Japanese Yen JPY"))
    }

    @Test
    fun `refresh operation is correct`() {
        val date = Date()
        assertThat(viewModel.refreshExchangeRates(date), `is`(false))
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `get currencies from remote runs into error, expect error livedata to have error message`() =
        testDispatcher.runBlockingTest {
            given(viewModel.getRemoteCurrencies()).willThrow(RuntimeException())
            assertThat(
                viewModel.errorLiveData.value,
                `is`("There was an unforeseen error")
            )
        }
}