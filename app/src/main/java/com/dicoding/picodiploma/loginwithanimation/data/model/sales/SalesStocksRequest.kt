package com.dicoding.picodiploma.loginwithanimation.data.model.sales

data class SalesStocksRequest (
    val customer_id: Int,
    val stock_id: Int,
    val quantity: Int,
)