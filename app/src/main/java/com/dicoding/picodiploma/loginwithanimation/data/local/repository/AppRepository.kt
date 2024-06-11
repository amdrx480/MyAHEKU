package com.dicoding.picodiploma.loginwithanimation.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.mediator.PurchasesRemoteMediator
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.data.local.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.mediator.RemoteMediator
import com.dicoding.picodiploma.loginwithanimation.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
) {
    fun postPurchaseStocks(
        token: String,
        purchasesResponse: PurchasesRequest
    ): LiveData<ResultResponse<ApiResponse>> =
        liveData {
            emit(ResultResponse.Loading)
            try {
                val response = apiService.addPurchaseStocks("Bearer $token", purchasesResponse)
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

    fun postItems(
        token: String,
        salesStocksRequest: SalesStocksRequest
    ): LiveData<ResultResponse<ApiResponse>> =
        liveData {
            emit(ResultResponse.Loading)
            try {
                val response = apiService.addItems("Bearer $token", salesStocksRequest)
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

    fun postItemTransactions(
        token: String,
        customerId: Int,
    ): LiveData<ResultResponse<ApiResponse>> =
        liveData {
            emit(ResultResponse.Loading)
            try {
                val response = apiService.postItemTransactions("Bearer $token", customerId)
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

    fun pagingStories(token: String): Flow<PagingData<StocksEntity>> {
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

    fun pagingPurchases(token: String): Flow<PagingData<PurchasesEntity>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = PurchasesRemoteMediator(appDatabase, apiService, token),
                pagingSourceFactory = {
                    appDatabase.purchasesDao().getAllPurchases()
                }
            ).flow
        }
    }

    companion object {
        private const val TAG = "AppRepository"
    }
}

//    fun login(pass: String): LiveData<ResultResponse<LoginWithVoucherResponse>> =
//        liveData {
//            emit(ResultResponse.Loading)
//            try {
//                val loginRequest =
//                    LoginWithVoucherRequest(pass) // Buat objek LoginRequest dengan password yang diberikan
//                val response = apiService.login(loginRequest)
//                if (!response.error) {
//                    emit(ResultResponse.Success(response))
//                } else {
//                    Log.e(TAG, "Register Fail: ${response.message}")
//                    emit(ResultResponse.Error(response.message))
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
//                emit(ResultResponse.Error(e.message.toString()))
//            }
//        }

//    fun getItemPurchase(
//        token: String,
//    ): LiveData<ResultResponse<ApiResponse>> =
//        liveData {
//            emit(ResultResponse.Loading)
//            try {
//                val response = apiService.getItemPurchase("Bearer $token")
//                if (!response.error) {
//                    emit(ResultResponse.Success(response))
//                } else {
//                    Log.e(TAG, "Register Fail: ${response.message}")
//                    emit(ResultResponse.Error(response.message))
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
//                emit(ResultResponse.Error(e.message.toString()))
//            }
//        }

//    suspend fun getCustomerById(token: String, customerId: Int): ListCustomersItem? {
//        return try {
//            val response = ApiConfig.ApiService().getCustomerById(token, customerId)
//    suspend fun getCustomers(token: String): ListCustomersItem? {
//    return try {
//        val response = ApiConfig.ApiService().getCustomer(token)
//            if (!response.error) {
//                response.token.firstOrNull()
//            } else null
//        } catch (e: Exception) {
//            null
//        }
//    }
//    fun postSalesToHistory(
//        token: String,
//    ): LiveData<ResultResponse<ApiResponse>> =
//        liveData {
//            emit(ResultResponse.Loading)
//            try {
//                val response = apiService.addSalesToHistory("Bearer $token")
//                if (!response.error) {
//                    emit(ResultResponse.Success(response))
//                } else {
//                    Log.e(TAG, "Register Fail: ${response.message}")
//                    emit(ResultResponse.Error(response.message))
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
//                emit(ResultResponse.Error(e.message.toString()))
//            }
//        }

//    fun getSalesStocks(
//        token: String,
//    ): LiveData<ResultResponse<SalesStocksResponse>> =
//        liveData {
//            emit(ResultResponse.Loading)
//            try {
//                val response = apiService.getSalesStocks("Bearer $token")
//                if (!response.error) {
//                    emit(ResultResponse.Success(response))
//                } else {
//                    Log.e(TAG, "SalesStock Fail: ${response.message}")
//                    emit(ResultResponse.Error(response.message))
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "SalesStock Exception: ${e.message.toString()} ")
//                emit(ResultResponse.Error(e.message.toString()))
//            }
//        }
//    fun getVendorStocks(
//        token: String,
//    ): LiveData<ResultResponse<AllVendorsResponse>> = liveData {
//        emit(ResultResponse.Loading)
//        try {
//            val response = apiService.getVendors("Bearer $token")
//            if (!response.error) {
//                emit(ResultResponse.Success(response))
//            } else {
//                Log.e(TAG, "Register Fail: ${response.message}")
//                emit(ResultResponse.Error(response.message))
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Register Exception: ${e.message.toString()} ")
//            emit(ResultResponse.Error(e.message.toString()))
//        }
//    }
//    fun getCategoryStocks(
//        token: String,
//    ): LiveData<ResultResponse<ApiResponse>> = liveData {
//        emit(ResultResponse.Loading)
//        try {
//            val response = apiService.getCategory("Bearer $token")
//            if (!response.error) {
//                emit(ResultResponse.Success(response))
//            } else {
//                Log.e(TAG, "Register Fail: ${response.message}")
//                emit(ResultResponse.Error(response.message))
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Register Exception: ${e.message.toString()} ")
//            emit(ResultResponse.Error(e.message.toString()))
//        }
//    }

//    fun getCategoriesStocks(
//        token: String,
//    ): LiveData<ResultResponse<AllCategoryResponse>> = liveData {
//        emit(ResultResponse.Loading)
//        try {
//            val response = apiService.getCategories("Bearer $token")
//            if (!response.error) {
//                emit(ResultResponse.Success(response))
//            } else {
//                Log.e(TAG, "Register Fail: ${response.message}")
//                emit(ResultResponse.Error(response.message))
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Register Exception: ${e.message.toString()} ")
//            emit(ResultResponse.Error(e.message.toString()))
//        }
//    }

//    fun getCategory(
//        token: String,
//        listCategoryItem: ListCategoryItem
//    ): LiveData<ResultResponse<AllCategoryResponse>> = liveData {
//        emit(ResultResponse.Loading)
//        try {
//            val response = apiService.getCategories("Bearer $token")
//            if (!response.error) {
//                emit(ResultResponse.Success(response))
//            } else {
//                Log.e(TAG, "Register Fail: ${response.message}")
//                emit(ResultResponse.Error(response.message))
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Register Exception: ${e.message.toString()} ")
//            emit(ResultResponse.Error(e.message.toString()))
//        }
//    }