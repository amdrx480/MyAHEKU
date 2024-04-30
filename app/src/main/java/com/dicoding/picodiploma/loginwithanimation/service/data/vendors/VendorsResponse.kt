package com.dicoding.picodiploma.loginwithanimation.service.data.vendors

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class AllVendorsResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: List<ListVendorsItem>,

    )

@Parcelize
data class ListVendorsItem(

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @SerializedName("vendor_name")
    val vendor_Name: String,

    @SerializedName("vendor_address")
    val vendor_Address: String,

    @SerializedName("vendor_email")
    val vendor_Email: String,

    @SerializedName("vendor_phone")
    val vendor_Phone: String,
) : Parcelable