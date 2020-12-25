package com.petermunyao.mobileandroidchallenge.model

import com.squareup.moshi.Json

data class ExchangeRateResponse(
    @field:Json(name = "success") var success: Boolean?,
    @field:Json(name = "source") var source: String?,
    @field:Json(name = "quotes") var quotes: Map<String, Float>?,
    @field:Json(name = "timestamp") var timestamp: Int?,
    @field:Json(name = "error") var error: ErrorInfo?
)