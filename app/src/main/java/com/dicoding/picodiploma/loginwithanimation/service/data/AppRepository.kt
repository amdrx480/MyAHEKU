package com.dicoding.picodiploma.loginwithanimation.service.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.model.stocks.ListStocksItem
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiResponse
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseResponse
import com.dicoding.picodiploma.loginwithanimation.service.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.service.database.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AppRepository(
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
) {
    fun login(pass: String): LiveData<ResultResponse<LoginResponse>> =
        liveData {
            emit(ResultResponse.Loading)
            try {
                val loginRequest = LoginRequest(pass) // Buat objek LoginRequest dengan password yang diberikan
                val response = apiService.login(loginRequest)
                if (!response.error) {
                    emit(ResultResponse.Success(response))
                } else {
                    Log.e(TAG, "Register Fail: ${response.message}")
                    emit(ResultResponse.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
                emit(ResultResponse.Error(e.message.toString()))
            }
        }
    fun postPurchaseStocks(
        token: String,
        purchaseRequest: PurchaseRequest
    ): LiveData<ResultResponse<ApiResponse>> =
        liveData {
            emit(ResultResponse.Loading)
            try {
                val response = apiService.addPurchaseStocks("Bearer $token", purchaseRequest)
                if (!response.error) {
                    emit(ResultResponse.Success(response))
                } else {
                    Log.e(TAG, "Register Fail: ${response.message}")
                    emit(ResultResponse.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
                emit(ResultResponse.Error(e.message.toString()))
            }
        }
    fun pagingStories(token: String): Flow<PagingData<ListStocksItem>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = RemoteMediator(appDatabase, apiService, token),
                pagingSourceFactory = {
                    appDatabase.appDao().getAllStocks()
                }
            ).flow
        }
    }

    companion object {
        private const val TAG = "AppRepository"
    }
}