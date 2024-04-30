package com.dicoding.picodiploma.loginwithanimation.service.data.purchase

data class PurchaseRequest (
    val vendor_id: Int,
    val stock_name: String,
    val stock_code: String,
    val category_id: Int,
    val units_id: Int,
    val Quantity: Int,
    val purchase_price: Int,
    val selling_price: Int
)