package com.dicoding.picodiploma.loginwithanimation.model.stocks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StocksModel(
    val id: String,
    val stockLocation: String,
    val stockCode: String,
    val stockCategory: String,
    val stockName: String,
    val stockPcs: Int,
    val stockPack: Int,
    val stockRoll: Int,
    val stockMeter: Int
): Parcelable

//{
//    "stock_location": "Gudang A",
//    "stock_code": "STK001",
//    "stock_category": "Elektronik",
//    "stock_name": "Laptop ASUS",
//    "stock_pcs": 50,
//    "stock_pack": 20,
//    "stock_roll": 10,
//    "stock_meter": 5000
//}