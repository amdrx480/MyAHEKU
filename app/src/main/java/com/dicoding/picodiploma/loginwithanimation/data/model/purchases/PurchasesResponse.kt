package com.dicoding.picodiploma.loginwithanimation.data.model.purchases

import com.google.gson.annotations.SerializedName

data class PurchasesResponse(
    @SerializedName("error")
    val error: Boolean? = null,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: List<PurchasesEntity>,
    )