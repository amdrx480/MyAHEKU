package com.dicoding.picodiploma.loginwithanimation.service.api

import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.model.stocks.AllStocksResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginRequest
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
//        @Field("password") pass: String
    ): LoginResponse

    @POST("stocks")
    suspend fun addPurchaseStocks(
        @Header("Authorization") token: String,
        @Body purchaseRequest: PurchaseRequest
    ): ApiResponse
//    ): Call<PurchaseResponse>

    @GET("stocks")
    suspend fun getAllStocks(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): AllStocksResponse

//    @POST("stocks")
//    fun setPurchaseStocks( @Body loginRequest: PurchaseRequest): Call<PurchaseResponse>

    //bisa
//    @GET("stocks")
//    fun getAllStocks(): Call<AllStocksResponse>


//    @GET("stocks")
//    fun getAllStocks( @Body stocksRequest: StocksRequest): Call<AllStocksResponse>
}
