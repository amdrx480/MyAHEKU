package com.dicoding.picodiploma.loginwithanimation.data.model.purchases

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "purchases")
data class PurchasesEntity (

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null,

    @field:SerializedName("vendor_name")
    val vendorName: String? = null,

    @field:SerializedName("stock_name")
    val stockName: String? = null,

    @field:SerializedName("stock_code")
    val stockCode: String? = null,

    @field:SerializedName("category_id")
    val categoryId: Int? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("unit_id")
    val unitId: Int? = null,

    @field:SerializedName("unit_name")
    val unitName: String? = null,

    @field:SerializedName("quantity")
    val quantity: Int? = null,

    @field:SerializedName("purchase_price")
    val purchasePrice: Int? = null,

    @field:SerializedName("selling_price")
    val sellingPrice: Int? = null

) : Parcelable