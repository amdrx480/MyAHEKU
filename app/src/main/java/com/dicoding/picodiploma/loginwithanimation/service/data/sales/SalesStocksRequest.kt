package com.dicoding.picodiploma.loginwithanimation.service.data.sales

data class SalesStocksRequest (
    val customer_id: Int,
    val stock_id: Int,
    val quantity: Int,
)