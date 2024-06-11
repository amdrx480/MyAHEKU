package com.dicoding.picodiploma.loginwithanimation.data.model.sales

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItemsModel(

    @SerializedName("id")
    val id: Int,

    @SerializedName("stock_name")
    val stockName: String,

    @SerializedName("unit_name")
    val unitName: String,

    @SerializedName("quantity")
    val quantity: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("sub_total")
    val subTotal: String,

    ) : Parcelable