package com.dicoding.picodiploma.loginwithanimation.data.purchase

import com.google.gson.annotations.SerializedName

class PurchaseRequest (
    val id: String,
//    val created_at: String,
    val stock_Location: String,
    val stock_Code: String,
    val stock_Category: String,
    val stock_Name: String,
    val stock_Pcs: Int,
    val stock_Pack: Int,
    val stock_Roll: Int,
    val stock_Meter: Int
    )