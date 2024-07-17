package com.dicoding.picodiploma.loginwithanimation.data.model.customers.customers

import com.google.gson.annotations.SerializedName

data class CustomersRequest(

    @SerializedName("customer_name")
    val customer_name: String,

    @SerializedName("customer_address")
    val customer_address: String,

    @SerializedName("customer_email")
    val customer_email: String,

    @SerializedName("customer_phone")
    val customer_phone: String,
)

//data class VendorsRequest(
//    @field:SerializedName("vendor_name")
//    val vendorName: String,
//
//    @field:SerializedName("vendor_address")
//    val vendorAddress: String,
//
//    @field:SerializedName("vendor_email")
//    val vendorEmail: String,
//
//    @field:SerializedName("vendor_phone")
//    val vendorPhone: String
//)
