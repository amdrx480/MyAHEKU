package com.dicoding.picodiploma.loginwithanimation.data.model.customers.customers

import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsModel
import com.google.gson.annotations.SerializedName

data class CustomersResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<CustomersModel>,
)
