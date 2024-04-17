package com.dicoding.picodiploma.loginwithanimation.view.purchase

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository

class PurchaseStocksViewModel(private val repository: AppRepository): ViewModel() {
    fun uploadPurchaseStocks(
        token: String,
        purchaseRequest: PurchaseRequest
    ) = repository.postPurchaseStocks(token, purchaseRequest)
}