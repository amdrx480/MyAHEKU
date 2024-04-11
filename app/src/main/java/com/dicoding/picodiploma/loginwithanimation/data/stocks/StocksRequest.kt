package com.dicoding.picodiploma.loginwithanimation.data.stocks

data class StocksRequest(
//    val token : String,
    val id             : String,
    val stock_Location : String,
    val stock_Code     : String,
    val stock_Name     : String,
    val stock_QRCode   : String,
    val stock_Unit     : String,
    val stock_Total    : String,
)