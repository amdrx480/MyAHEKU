package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itemtransactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity

class ItemTransactionsViewModel(private val repository: AuthRepository) : ViewModel() {

    fun getItemTransactions(token: String): LiveData<PagingData<ItemTransactionsEntity>> {
        return repository.pagingItemTransactions(token).cachedIn(viewModelScope).asLiveData()
    }
}