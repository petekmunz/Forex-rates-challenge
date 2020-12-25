package com.petermunyao.mobileandroidchallenge.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class SupportedCurrencies(
    var currencies: Map<String, String> = HashMap(),
    @PrimaryKey
    val id: Int = 1
)