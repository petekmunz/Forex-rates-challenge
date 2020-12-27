package com.petermunyao.mobileandroidchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry
import com.petermunyao.mobileandroidchallenge.database.CurrenciesDao
import com.petermunyao.mobileandroidchallenge.database.CurrencyDatabase
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.IOException

@ExperimentalCoroutinesApi
class DatabaseReadWriteTest {
    private lateinit var currenciesDao: CurrenciesDao
    private lateinit var db: CurrencyDatabase
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun createDb() {
        Dispatchers.setMain(testDispatcher)
        val context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, CurrencyDatabase::class.java)
            .build()
        currenciesDao = db.getCurrenciesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Test
    @Throws(Exception::class)
    fun writeAndReadUserInDb() = testDispatcher.runBlockingTest {
        val testCurrency = mapOf("JPY" to "Japanese Yen")
        val supportedCurrencies = SupportedCurrencies(
            testCurrency
        )
        currenciesDao.insertCurrencies(supportedCurrencies)
        currenciesDao.getCurrencies().first {
            assertThat(it, `is`(supportedCurrencies))
            true
        }
    }
}