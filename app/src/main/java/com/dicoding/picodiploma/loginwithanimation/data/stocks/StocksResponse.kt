package com.dicoding.picodiploma.loginwithanimation.model.stocks

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AllStocksResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: List<ListStocksItem>,

//    @SerializedName("token")
//    val token: String,
)

@Parcelize
data class ListStocksItem(

    @SerializedName("id")
    val id: String,

    @SerializedName("created_at")
    val created_at: String,

    @SerializedName("stock_location")
    val stock_Location: String,

    @SerializedName("stock_code")
    val stock_Code: String,

    @SerializedName("stock_category")
    val stock_Category: String,

    @SerializedName("stock_name")
    val stock_Name: String,

    @SerializedName("stock_pcs")
    val stock_Pcs: Int,

    @SerializedName("stock_pack")
    val stock_Pack: Int,

    @SerializedName("stock_roll")
    val stock_Roll: Int,

    @SerializedName("stock_meter")
    val stock_Meter: Int

    ) : Parcelable