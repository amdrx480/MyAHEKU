package com.dicoding.picodiploma.loginwithanimation.data.model.vendors

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorsModel(

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