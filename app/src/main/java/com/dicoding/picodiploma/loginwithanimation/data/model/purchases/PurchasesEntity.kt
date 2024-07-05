package com.dicoding.picodiploma.loginwithanimation.data.model.purchases

import android.os.Parcelable
import androidx.room.ColumnInfo
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

    @ColumnInfo(name = "vendor_name")
    @SerializedName("vendor_name")
//    @field:SerializedName("vendor_name")
    val vendorName: String? = null,

//    @field:SerializedName("stock_name")
    @ColumnInfo(name = "stock_name")
    @SerializedName("stock_name")
    val stockName: String? = null,

    @ColumnInfo(name = "stock_code")
    @SerializedName("stock_code")
//    @field:SerializedName("stock_code")
    val stockCode: String? = null,

//    @field:SerializedName("category_id")
//    val categoryId: Int? = null,

    @ColumnInfo(name = "category_name")
    @SerializedName("category_name")
    val categoryName: String? = null,

//    @ColumnInfo(name = "unit_id")
//    @SerializedName("unit_id")
//    @field:SerializedName("unit_id")
//    val unitId: Int? = null,

    @ColumnInfo(name = "unit_name")
    @SerializedName("unit_name")
    val unitName: String? = null,

    @ColumnInfo(name = "quantity")
    @SerializedName("quantity")
    val quantity: Int? = null,

    @ColumnInfo(name = "purchase_price")
    @SerializedName("purchase_price")
    val purchasePrice: Int? = null,

    @ColumnInfo(name = "selling_price")
    @SerializedName("selling_price")
    val sellingPrice: Int? = null

) : Parcelable

//    @ColumnInfo(name = "vendor_name")
//    @SerializedName("vendor_name")
//    @field:SerializedName("vendor_id")
//    val vendorId: Int? = null,