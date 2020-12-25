package com.petermunyao.mobileandroidchallenge.model

import com.squareup.moshi.Json

data class CurrenciesResponse(
    @field:Json(name = "success") var success: Boolean?,
    @field:Json(name = "currencies") var currencies: Map<String, String>?,
    @field:Json(name = "error") var error: ErrorInfo?
)