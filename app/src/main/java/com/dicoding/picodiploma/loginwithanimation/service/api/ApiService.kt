package com.dicoding.picodiploma.loginwithanimation.service.api

import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.stocks.AllStocksResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.category.AllCategoryResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.category.ListCategoryItem
import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.SalesStocksResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.units.AllUnitsResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.vendors.AllVendorsResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.vendors.ListVendorsItem
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
//    @FormUrlEncoded
//    @POST("login")
//    fun login(
//        @Field("password") pass: String
//    ): Call<LoginResponse>

//    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): LoginResponse
    @POST("purchases")
    suspend fun addPurchaseStocks(
        @Header("Authorization") token: String,
        @Body purchaseRequest: PurchaseRequest
    ): ApiResponse

    @POST("sales")
    suspend fun addSalesStocks(
        @Header("Authorization") token: String,
        @Body salesStocksRequest: SalesStocksRequest,
    ): ApiResponse

    @GET("sales")
    fun getSalesStocks(
        @Header("Authorization") token: String,
    ): Call<SalesStocksResponse>

    @GET("stocks")
    suspend fun getAllStocks(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): AllStocksResponse
    @GET("vendors")
    fun getVendors(
        @Header("Authorization") token: String,
    ): Call<AllVendorsResponse>

//    @POST("vendors")
//    suspend fun postVendors(
////        @Header("Authorization") token: String,
//        @Body listVendorsItem: ListVendorsItem
//    ): Call<Void>
    @GET("units")
     fun getUnits(
        @Header("Authorization") token: String,
    ): Call<AllUnitsResponse>

    @GET("category")
    suspend fun getCategorylifecycleScope(
        @Header("Authorization") token: String,
//        @Body listCategoryItem: ListCategoryItem
    ): AllCategoryResponse

    @GET("category")
    suspend fun getCategory(
        @Header("Authorization") token: String,
//        @Body listCategoryItem: ListCategoryItem
    ): ApiResponse

    @GET("category")
    fun getCategories(
        @Header("Authorization") token: String,
//        @Body listCategoryItem: ListCategoryItem
    ): Call<AllCategoryResponse>
////    ): Call<ApiResponse>

}
