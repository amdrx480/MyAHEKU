package com.dicoding.picodiploma.loginwithanimation.data.model.stocks

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stocks")
data class StocksEntity(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("created_at")
    val createdAt: String,

    @ColumnInfo(name = "stock_name")
    @SerializedName("stock_name")
    val stockName: String,

    @ColumnInfo(name = "stock_code")
    @SerializedName("stock_code")
    val stockCode: String,

    @ColumnInfo(name = "category_name")
    @SerializedName("category_name")
    val categoryName: String,

    @ColumnInfo(name = "unit_name")
    @SerializedName("unit_name")
    val unitName: String,

    @ColumnInfo(name = "stock_total")
    @SerializedName("stock_total")
    val stockTotal: Int,

    @ColumnInfo(name = "selling_price")
    @SerializedName("selling_price")
    val sellingPrice: Int
) : Parcelable


//@Parcelize
//@Entity(tableName = "stocks")
//data class StocksEntity(
//    @PrimaryKey
//    @SerializedName("id")
//    val id: String,
//
//    @SerializedName("created_at")
//    val createdAt: String,
//
//    @SerializedName("stock_name")
//    val stockName: String,
//
//    @SerializedName("stock_code")
//    val stockCode: String,
//
//    @SerializedName("category_name")
//    val categoryName: String,
//
//    @SerializedName("unit_name")
//    val unitName: String,
//
//    @SerializedName("stock_total")
//    val stockTotal: Int,
//
//    @SerializedName("selling_price")
//    val sellingPrice: Int
//) : Parcelable
