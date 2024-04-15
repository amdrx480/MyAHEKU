package com.dicoding.picodiploma.loginwithanimation.data.purchase

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class PurchaseResponse (
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token : List<ListPurchaseItem>,
    )

@Parcelize
data class ListPurchaseItem(

    @SerializedName("id")
    val id: String,

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