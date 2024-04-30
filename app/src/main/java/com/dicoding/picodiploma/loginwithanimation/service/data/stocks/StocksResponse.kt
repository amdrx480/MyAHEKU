package com.dicoding.picodiploma.loginwithanimation.service.data.stocks

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AllStocksResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: List<ListStocksItem>,

    )

@Parcelize
@Entity(tableName = "stocks")
data class ListStocksItem(

    @PrimaryKey
//    @SerializedName("id")
    @field:SerializedName("id")
    val id: String,

    @SerializedName("created_at")
    val created_at: String,

    @SerializedName("stock_name")
    val stock_Name: String,

    @SerializedName("stock_code")
    val stock_Code: String,

    @SerializedName("category_name")
    val category_Name: String,

    @SerializedName("units_name")
    val units_Name: String,

    @SerializedName("stock_total")
    val stock_total: Int,
//
//    @SerializedName("selling_price")
//    val selling_Price: Int,
//
//    @SerializedName("stock_meter")
//    val stock_Meter: Int

    ) : Parcelable