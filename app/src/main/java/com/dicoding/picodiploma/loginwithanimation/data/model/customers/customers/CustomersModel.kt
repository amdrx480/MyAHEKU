package com.dicoding.picodiploma.loginwithanimation.data.model.customers.customers

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomersModel(

    @SerializedName("id")
    val id: Int,

    @SerializedName("created_at")
    val created_at: String,

    @SerializedName("updated_at")
    val updated_at: String,

    @SerializedName("customer_name")
    val customer_name: String,

    @SerializedName("customer_address")
    val customer_address: String,

    @SerializedName("customer_email")
    val customer_email: String,

    @SerializedName("customer_phone")
    val customer_phone: String,


    ) : Parcelable