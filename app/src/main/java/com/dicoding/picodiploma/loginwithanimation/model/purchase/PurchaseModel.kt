package com.dicoding.picodiploma.loginwithanimation.model.purchase

data class PurchaseModel (
    val supplierVendor: String,
    val stock_Name: String,
    val stock_Code: String,
    val stock_Category: String,
    val stock_Pcs: Int,
    val stock_Pack: Int,
    val stock_Roll: Int,
    val stock_Meter: Int = 0,
)