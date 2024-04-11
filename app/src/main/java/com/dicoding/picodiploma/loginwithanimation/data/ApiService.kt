package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.login.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.data.login.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.stocks.StocksRequest
import com.dicoding.picodiploma.loginwithanimation.model.stocks.AllStocksResponse
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
//    @FormUrlEncoded
//    @POST("login")
//    fun login(
//        @Field("password") pass: String
//    ): Call<LoginResponse>
    @POST("login")
    fun login( @Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("stocks")
    fun getAllStocks(
        @Header("Authorization") token: String
    ): Call<AllStocksResponse>

    //bisa
//    @GET("stocks")
//    fun getAllStocks(): Call<AllStocksResponse>


//    @GET("stocks")
//    fun getAllStocks( @Body stocksRequest: StocksRequest): Call<AllStocksResponse>
}
