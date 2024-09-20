package com.dicoding.picodiploma.loginwithanimation.data.model.transactions

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "item_transactions")
class ItemTransactionsEntity(

    @PrimaryKey
    @SerializedName("id")
    val id: Int,

//    @SerializedName("customer_id")
//    val customerId: Int? = null,

    @ColumnInfo(name = "customer_name")
    @SerializedName("customer_name")
    val customerName: String? = null,

//    @SerializedName("stock_id")
//    val stockId: Int? = null,

    @ColumnInfo(name = "stock_name")
    @SerializedName("stock_name")
    val stockName: String? = null,

    @ColumnInfo(name = "stock_code")
    @SerializedName("stock_code")
    val stockCode: String? = null,

//    @SerializedName("unit_id")
//    val unitId: Int? = null,

    @ColumnInfo(name = "unit_name")
    @SerializedName("unit_name")
    val unitName: String? = null,

//    @SerializedName("category_id")
//    val categoryId: Int? = null,

    @ColumnInfo(name = "category_name")
    @SerializedName("category_name")
    val categoryName: String? = null,

    @ColumnInfo(name = "quantity")
    @SerializedName("quantity")
    val quantity: Int? = null,

    @ColumnInfo(name = "price")
    @SerializedName("price")
    val price: Int? = null,

//    @ColumnInfo(name = "sub_total")
//    @SerializedName("sub_total")
//    val subTotal: Int? = null,

    ) : Parcelable