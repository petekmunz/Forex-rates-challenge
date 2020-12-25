package com.petermunyao.mobileandroidchallenge.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.HashMap

@Entity(tableName = "exchangeRates")
data class ExchangeRates(
    var currencyRates: Map<String, Float> = HashMap(),
    var timeStamp: Date = Date(),
    @PrimaryKey
    val id: Int = 1
)