package com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders

import com.google.gson.annotations.SerializedName

data class PurchaseOrderResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<PurchaseOrderModel>,

    )
