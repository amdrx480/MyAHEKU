package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itempurchases

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AppRepository
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity

class ItemPurchasesViewModel(private val repository: AuthRepository) : ViewModel() {

    fun getPurchases(token: String): LiveData<PagingData<PurchasesEntity>> {
        return repository.pagingPurchases(token).cachedIn(viewModelScope).asLiveData()
    }
}