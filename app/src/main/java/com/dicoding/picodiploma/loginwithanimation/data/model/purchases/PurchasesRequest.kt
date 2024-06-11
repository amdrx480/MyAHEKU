package com.dicoding.picodiploma.loginwithanimation.data.model.purchases

import com.google.gson.annotations.SerializedName

data class PurchasesRequest (

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null,

    @field:SerializedName("stock_name")
    val stockName: String? = null,

    @field:SerializedName("stock_code")
    val stockCode: String? = null,

    @field:SerializedName("category_id")
    val categoryId: Int? = null,

    @field:SerializedName("unit_id")
    val unitId: Int? = null,

    @field:SerializedName("quantity")
    val quantity: Int? = null,

    @field:SerializedName("purchase_price")
    val purchasePrice: Int? = null,

    @field:SerializedName("selling_price")
    val sellingPrice: Int? = null
)