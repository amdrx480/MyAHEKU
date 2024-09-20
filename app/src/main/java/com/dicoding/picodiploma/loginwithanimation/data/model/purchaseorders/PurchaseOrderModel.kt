package com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurchaseOrderModel(

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("customer_name")
    val customerName: String,

    @field:SerializedName("stock_name")
    val stockName: String,

    @field:SerializedName("stock_code")
    val stockCode: String,

    @field:SerializedName("unit_name")
    val unitName: String,

    @field:SerializedName("category_name")
    val categoryName: String,

    @field:SerializedName("quantity")
    val quantity: Int,

    @field:SerializedName("price")
    val price: Int,

    @SerializedName("packaging_officer_name")
    val packagingOfficerName: String? = "", // Dapat kosong

    @SerializedName("reminder_time")
    val reminderTime: String,

    @SerializedName("delivery_address")
    val deliveryAddress: String

): Parcelable


//    @field:SerializedName("reminder_time")
//    val reminderTime: Int,

//    @field:SerializedName("quantity")
//    val id: Int,