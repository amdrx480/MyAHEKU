package com.dicoding.picodiploma.loginwithanimation.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.data.model.category.CategoryModel
import com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher.LoginWithVoucherRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher.LoginWithVoucherResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileModel
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity
import com.dicoding.picodiploma.loginwithanimation.data.model.units.UnitsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.mediator.ItemTransactionsRemoteMediator
import com.dicoding.picodiploma.loginwithanimation.data.remote.mediator.PurchasesRemoteMediator
import com.dicoding.picodiploma.loginwithanimation.data.remote.mediator.StocksRemoteMediator
import com.dicoding.picodiploma.loginwithanimation.utils.RawQueryHelper
import com.dicoding.picodiploma.loginwithanimation.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow

//import javax.inject.Inject

//class AuthRepository @Inject constructor(
class AuthRepository(
    private val authPreferenceDataSource: AuthPreferenceDataSource,
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
) {

    suspend fun saveAuthVoucher(voucher: String) {
        authPreferenceDataSource.saveAuthVoucher(voucher)
    }

    suspend fun saveAuthToken(token: String) {
        authPreferenceDataSource.saveAuthToken(token)
    }

    suspend fun saveAuthStateToken(token: Boolean) {
        authPreferenceDataSource.saveAuthStateToken(token)
    }

    suspend fun logoutVoucher() {
        authPreferenceDataSource.logoutVoucher()
    }

    fun getAuthVoucher(): Flow<String?> = authPreferenceDataSource.getAuthVoucher()

    fun getAuthToken(): Flow<String?> = authPreferenceDataSource.getAuthToken()

    fun getAuthStateToken(): Flow<Boolean> = authPreferenceDataSource.getAuthStateToken()

    @OptIn(ExperimentalPagingApi::class)
    fun getPagingStocks(
        token: String,
        sort: String? = null,
        order: String? = null,
        search: String? = null,
        categoryName: List<String>? = null,
        unitName: List<String>? = null,
        sellingPriceMin: Int? = null,
        sellingPriceMax: Int? = null
    ): Flow<PagingData<StocksEntity>> {
        val rawQuery = RawQueryHelper.buildStocksQuery(sort, search, order, categoryName, unitName)

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = StocksRemoteMediator(
                appDatabase = appDatabase,
                apiService = apiService,
                token = token,
                sort = sort,
                search = search,
                order = order,
                categoryName = categoryName,
                unitName = unitName,
                sellingPriceMin = sellingPriceMin,
                sellingPriceMax = sellingPriceMax,
            ),
            pagingSourceFactory = { appDatabase.stocksDao().getStocksByRawQuery(rawQuery) }
        ).flow
    }

//    @OptIn(ExperimentalPagingApi::class)
//    fun getPagingStocks(
//        token: String,
//        filter: String? = null,
//        sort: String? = null,
//        query: String? = null,
//        order: String? = null,
//        categoryName: String? = null,
//        unitName: String? = null,
////        minSellingPrice: Int? = null,
////        maxSellingPrice: Int? = null
//    ): Flow<PagingData<StocksEntity>> {
//        // wrapEspressoIdlingResource digunakan di sini untuk mengelola idling resource
//        // saat pengujian dengan Espresso. Ini membantu dalam menjaga konsistensi
//        // dan stabilitas pengujian UI dengan memastikan bahwa resource idling
//        // diatur dengan baik selama operasi asynchronous.
//
//        // Menggunakan wrapEspressoIdlingResource untuk mengelola idling resource saat pengujian dengan Espresso
////        val rawQuery = RawQueryHelper.buildStocksQuery(filter, sort, query, order, categoryName, unitName, minSellingPrice, maxSellingPrice)
//        val rawQuery = RawQueryHelper.buildStocksQuery(
//            filter = filter,
//            sort = sort,
//            query = query,
//            order = order,
//            categoryName = categoryName,
//            unitName = unitName,
//        )
//
//        return wrapEspressoIdlingResource {
//            Pager(
//                config = PagingConfig(
//                    pageSize = 10,
//                    enablePlaceholders = false // Menonaktifkan placeholders untuk data yang belum dimuat
//                ),
//                // Menggunakan remoteMediator untuk menghubungkan data lokal dengan sumber data jarak jauh
//                remoteMediator = StocksRemoteMediator(
//                    appDatabase = appDatabase,
//                    apiService = apiService,
//                    token = token,
//                    filter = filter,
//                    sort = sort,
//                    search = query,
//                    order = order,
//                    categoryName = categoryName,
//                    unitName = unitName,
//                ),
//                // Menggunakan pagingSourceFactory untuk mendefinisikan sumber data paginasi dari database lokal
//                pagingSourceFactory = {
//                    appDatabase.stocksDao().getStocksByRawQuery(rawQuery)
//                }
//            ).flow
//        }
//    }

    //                pagingSourceFactory = {
//                    if (!filter.isNullOrBlank() && !sort.isNullOrBlank()) {
//                        appDatabase.stocksDao().getFilteredStocks(filter = filter, sort = sort)
//                    } else if (!query.isNullOrBlank()) {
//                        appDatabase.stocksDao().searchStocksByName(search = query)
//                        //!!
//                    } else {
//                        appDatabase.stocksDao().getAllStocks()
//                    }
//                }
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

    fun pagingItemTransactions(token: String): Flow<PagingData<ItemTransactionsEntity>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = ItemTransactionsRemoteMediator(appDatabase, apiService, token),
                pagingSourceFactory = {
                    appDatabase.itemTransactionsDao().getAllItemTransactions()
                }
            ).flow
        }
    }

    fun loginVoucher(
        loginWithVoucherRequest: LoginWithVoucherRequest
    ): LiveData<ResultResponse<LoginWithVoucherResponse>> = liveData {
        try {
            emit(ResultResponse.Loading)
            val response = apiService.loginVoucher(loginWithVoucherRequest)
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

    // Metode untuk mengambil profil admin berdasarkan token yang tersimpan
    fun fetchAdminProfile(token: String?): LiveData<ResultResponse<ProfileModel>> = liveData {
        emit(ResultResponse.Loading)
        try {
            val response = apiService.fetchAdminProfile("Bearer $token")
            emit(ResultResponse.Success(response.data))
        } catch (e: Exception) {
            emit(ResultResponse.Error(e.message ?: "Unknown error"))
        }
    }


//    fun fetchAdminProfile(token: String?): LiveData<ResultResponse<List<ProfileModel>>> = liveData {
//        emit(ResultResponse.Loading)
//        try {
//            val response = apiService.fetchAdminProfile("Bearer $token")
//            emit(ResultResponse.Success(response.data))
//        } catch (e: Exception) {
//            emit(ResultResponse.Error(e.message ?: "Unknown error"))
//        }
//    }

    fun fetchVendors(token: String?): LiveData<ResultResponse<List<VendorsModel>>> = liveData {
        emit(ResultResponse.Loading)
        try {
            val response = apiService.fetchVendors("Bearer $token")
            emit(ResultResponse.Success(response.data))
        } catch (e: Exception) {
            emit(ResultResponse.Error(e.message ?: "Unknown error"))
        }
    }

    fun fetchUnits(token: String?): LiveData<ResultResponse<List<UnitsModel>>> = liveData {
        emit(ResultResponse.Loading)
        try {
            val response = apiService.getUnits("Bearer $token")
            emit(ResultResponse.Success(response.data))
        } catch (e: Exception) {
            emit(ResultResponse.Error(e.message ?: "Unknown error"))
        }
    }

    fun fetchCategories(token: String?): LiveData<ResultResponse<List<CategoryModel>>> = liveData {
        emit(ResultResponse.Loading)
        try {
            val response = apiService.getCategories("Bearer $token")
            emit(ResultResponse.Success(response.data))
        } catch (e: Exception) {
            emit(ResultResponse.Error(e.message ?: "Unknown error"))
        }
    }

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

    companion object {
        private const val TAG = "AuthRepository"
    }
}


//    fun getUnits(token: String?): LiveData<ResultResponse<UnitsResponse>> {
//        val resultLiveData = MutableLiveData<ResultResponse<UnitsResponse>>()
//        resultLiveData.value = ResultResponse.Loading
//
//        apiService.getUnits("Bearer $token").enqueue(object : Callback<UnitsResponse> {
//            override fun onResponse(call: Call<UnitsResponse>, response: Response<UnitsResponse>) {
//                if (response.isSuccessful) {
//                    response.body()?.let {
//                        resultLiveData.value = ResultResponse.Success(it)
//                    } ?: run {
//                        resultLiveData.value = ResultResponse.Error("Response body is null")
//                    }
//                } else {
//                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
//                    Log.e(TAG, "Get Categories Fail: $errorMessage")
//                    resultLiveData.value = ResultResponse.Error(errorMessage)
//                }
//            }
//
//            override fun onFailure(call: Call<UnitsResponse>, t: Throwable) {
//                Log.e(TAG, "Get Categories Exception: ${t.message}")
//                resultLiveData.value = ResultResponse.Error(t.message ?: "Unknown error")
//            }
//        })
//
//        return resultLiveData
//    }
//
//    fun getCategories(token: String?): LiveData<ResultResponse<CategoryResponse>> {
//        val resultLiveData = MutableLiveData<ResultResponse<CategoryResponse>>()
//        resultLiveData.value = ResultResponse.Loading
//
//        apiService.getCategories("Bearer $token").enqueue(object : Callback<CategoryResponse> {
//            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
//                if (response.isSuccessful) {
//                    response.body()?.let {
//                        resultLiveData.value = ResultResponse.Success(it)
//                    } ?: run {
//                        resultLiveData.value = ResultResponse.Error("Response body is null")
//                    }
//                } else {
//                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
//                    Log.e(TAG, "Get Categories Fail: $errorMessage")
//                    resultLiveData.value = ResultResponse.Error(errorMessage)
//                }
//            }
//
//            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
//                Log.e(TAG, "Get Categories Exception: ${t.message}")
//                resultLiveData.value = ResultResponse.Error(t.message ?: "Unknown error")
//            }
//        })
//
//        return resultLiveData
//    }

//    suspend fun saveVoucherSession(user: UserModel) {
//        authPreferenceDataSource.saveVoucherSession(user)
//    }
//
//    fun getVoucherSession(): Flow<UserModel?> = authPreferenceDataSource.getVoucherSession()

//    fun loginVoucher(
//        pass: String
//    ): Flow<ResultResponse<LoginWithVoucherResponse>> = flow {
//        try {
//            emit(ResultResponse.Loading)
//            val loginWithVoucherRequest =
//                LoginWithVoucherRequest(pass) // Buat objek LoginRequest dengan password yang diberikan
//            val response = apiService.loginVoucher(loginWithVoucherRequest)
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
//
//    }.flowOn(Dispatchers.IO)

//    fun getCategories(
//        token: String?,
//    ): LiveData<ResultResponse<CategoryResponse>> =
//        liveData {
//            emit(ResultResponse.Loading)
//            try {
//                val response = apiService.getCategories("Bearer $token")
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

//    fun getAuthToken(): Flow<Boolean> = authPreferenceDataSource.getAuthToken()

//    fun getAuthToken() {
//        authPreferenceDataSource.getAuthToken()
//    }