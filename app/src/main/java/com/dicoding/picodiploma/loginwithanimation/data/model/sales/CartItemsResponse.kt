package com.dicoding.picodiploma.loginwithanimation.data.model.sales

import com.google.gson.annotations.SerializedName

data class CartItemsResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<CartItemsModel>,

    )

