package com.petermunyao.mobileandroidchallenge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.petermunyao.mobileandroidchallenge.model.ExchangeRates
import com.petermunyao.mobileandroidchallenge.model.SupportedCurrencies
import com.petermunyao.mobileandroidchallenge.utils.Converter

@Database(
    entities = [ExchangeRates::class, SupportedCurrencies::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun getCurrenciesDao(): CurrenciesDao
}