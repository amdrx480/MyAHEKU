package com.dicoding.picodiploma.loginwithanimation.data.model.transactions

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "item_transactions")
class ItemTransactionsEntity(

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("customer_id")
    val customerId: Int? = null,

    @field:SerializedName("customer_name")
    val customerName: String? = null,

    @field:SerializedName("stock_id")
    val stockId: Int? = null,

    @field:SerializedName("stock_name")
    val stockName: String? = null,

    @field:SerializedName("unit_id")
    val unitId: Int? = null,

    @field:SerializedName("unit_name")
    val unitName: String? = null,

    @field:SerializedName("category_id")
    val categoryId: Int? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("quantity")
    val quantity: Int? = null,

    @field:SerializedName("sub_total")
    val subTotal: Int? = null,

    ) : Parcelable