package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AppRepository
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

//v
class StocksViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> = _authToken

    init {
        loadAuthToken()
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e -> Log.e("StocksViewModel", "Error loading auth token", e) }
                .collect { token ->
                    _authToken.postValue(token)
                }
        }
    }

    fun getStocks(): LiveData<PagingData<StocksEntity>> {
        return authToken.switchMap { token ->
            authRepository.pagingStories(token ?: "").cachedIn(viewModelScope).asLiveData()
        }
    }
}



//fun getStocks(token: String): LiveData<PagingData<StocksEntity>> {
//    return authRepository.pagingStories(token).cachedIn(viewModelScope).asLiveData()
//}