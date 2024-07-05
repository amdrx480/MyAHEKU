package com.dicoding.picodiploma.loginwithanimation.data.model.excel

import com.google.gson.annotations.SerializedName

data class ExcelResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ExcelModel
)