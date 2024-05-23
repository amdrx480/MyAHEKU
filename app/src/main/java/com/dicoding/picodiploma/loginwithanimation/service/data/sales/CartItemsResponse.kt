package com.dicoding.picodiploma.loginwithanimation.service.data.sales

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class CartItemsResponse(

@SerializedName("error")
val error: Boolean,

@SerializedName("message")
val message: String,

@SerializedName("token")
val token : List<ListCartItems>,

)

@Parcelize
data class ListCartItems(

    @SerializedName("id")
    val id: Int,

    @SerializedName("stock_name")
    val stock_name: String,

    @SerializedName("quantity")
    val quantity: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("sub_total")
    val sub_total: String,

) : Parcelable