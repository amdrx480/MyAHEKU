package com.dicoding.picodiploma.loginwithanimation.data.model.transactions

import com.google.gson.annotations.SerializedName

data class ItemTransactionsResponse(
    @SerializedName("error")
    val error: Boolean? = null,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: List<ItemTransactionsEntity>,
    )