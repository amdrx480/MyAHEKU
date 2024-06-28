package com.dicoding.picodiploma.loginwithanimation.data.model.stocks

import com.google.gson.annotations.SerializedName

data class StocksResponse(

    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<StocksEntity>,
)