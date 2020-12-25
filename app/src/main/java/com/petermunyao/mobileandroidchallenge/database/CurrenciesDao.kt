package com.petermunyao.mobileandroidchallenge.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.petermunyao.mobileandroidchallenge.model.ExchangeRates
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrenciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(exchangeRates: ExchangeRates)

    @Query("SELECT * FROM exchangeRates WHERE id = 1")
    fun getRates(): Flow<ExchangeRates?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(supportedCurrencies: SupportedCurrencies)

    @Query("SELECT * FROM currencies WHERE id = 1")
    fun getCurrencies(): Flow<SupportedCurrencies?>
}