package com.dicoding.picodiploma.loginwithanimation.data.model.stocks

data class StocksRequest(
    val id             : String,
    val stockLocation : String,
    val stockCode     : String,
    val stockName     : String,
    val stockQRCode   : String,
    val stockUnit     : String,
    val stockTotal    : String,
)