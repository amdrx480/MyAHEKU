package com.dicoding.picodiploma.loginwithanimation.data.model.vendors

import com.google.gson.annotations.SerializedName

data class VendorsRequest(
    @field:SerializedName("vendor_name")
    val vendorName: String,

    @field:SerializedName("vendor_address")
    val vendorAddress: String,

    @field:SerializedName("vendor_email")
    val vendorEmail: String,

    @field:SerializedName("vendor_phone")
    val vendorPhone: String
)
