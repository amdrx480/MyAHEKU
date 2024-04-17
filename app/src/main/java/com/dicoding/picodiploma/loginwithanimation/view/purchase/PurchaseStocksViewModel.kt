package com.dicoding.picodiploma.loginwithanimation.view.purchase

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository

class PurchaseStocksViewModel(private val repository: AppRepository): ViewModel() {
    fun uploadPurchaseStocks(
        token: String,
        supplierVendor: String,
        stock_Name: String,
        stock_Code: String,
        stock_Category: String,
//        stock_Pcs: Int,
//        stock_Pack: Int,
//        stock_Roll: Int,
//        stock_Meter: Int
    ) = repository.postPurchaseStocks(token, supplierVendor, stock_Name, stock_Code, stock_Category)
//    ) = repository.postPurchaseStocks(token, supplierVendor, stock_Name, stock_Code, stock_Category, stock_Pcs, stock_Pack, stock_Roll, stock_Meter)
}