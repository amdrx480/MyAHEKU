package com.dicoding.picodiploma.loginwithanimation.service.data.sales

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class SalesStocksResponse(

@SerializedName("error")
val error: Boolean,

@SerializedName("message")
val message: String,

@SerializedName("token")
val token : List<ListSalesStocksItem>,

)

@Parcelize
data class ListSalesStocksItem(

    @SerializedName("id")
    val id: Int,

    @SerializedName("vendor_name")
    val vendor_name: String,

    @SerializedName("stock_name")
    val stock_name: String,

    @SerializedName("quantity")
    val quantity: String,

) : Parcelable