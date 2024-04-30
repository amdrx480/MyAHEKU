package com.dicoding.picodiploma.loginwithanimation.view.stocks

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.service.data.stocks.ListStocksItem
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository
//v
class StocksViewModel(
    private val repository: AppRepository,
) : ViewModel() {
    fun getStocks(token: String): LiveData<PagingData<ListStocksItem>> {
        return repository.pagingStories(token).cachedIn(viewModelScope).asLiveData()
    }
}