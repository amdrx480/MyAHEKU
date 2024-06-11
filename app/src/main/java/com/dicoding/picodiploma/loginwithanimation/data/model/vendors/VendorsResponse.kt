package com.dicoding.picodiploma.loginwithanimation.data.model.vendors

import com.google.gson.annotations.SerializedName


data class VendorsResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<VendorsModel>,

    )

//@Parcelize
//data class ListVendorsItem(
//
//    @PrimaryKey
//    @field:SerializedName("id")
//    val id: Int,
//
//    @SerializedName("vendor_name")
//    val vendor_Name: String,
//
//    @SerializedName("vendor_address")
//    val vendor_Address: String,
//
//    @SerializedName("vendor_email")
//    val vendor_Email: String,
//
//    @SerializedName("vendor_phone")
//    val vendor_Phone: String,
//) : Parcelable