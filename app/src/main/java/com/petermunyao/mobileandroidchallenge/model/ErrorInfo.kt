package com.petermunyao.mobileandroidchallenge.model

import com.squareup.moshi.Json

data class ErrorInfo(
    @field:Json(name = "code") var code: Int?,
    @field:Json(name = "info") var info: String?
)