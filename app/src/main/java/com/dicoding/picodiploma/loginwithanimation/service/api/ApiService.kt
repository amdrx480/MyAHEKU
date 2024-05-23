package com.dicoding.picodiploma.loginwithanimation.service.api

import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.stocks.AllStocksResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.category.AllCategoryResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.customers.AllCustomersResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.login.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.CartItemsResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.units.AllUnitsResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.vendors.AllVendorsResponse
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("auth/voucher")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): LoginResponse

    @POST("purchases")
    suspend fun addPurchaseStocks(
        @Header("Authorization") token: String,
        @Body purchaseRequest: PurchaseRequest
    ): ApiResponse

    @POST("cart_items")
    suspend fun addItems(
        @Header("Authorization") token: String,
        @Body salesStocksRequest: SalesStocksRequest,
    ): ApiResponse

    @GET("customers")
    fun getCustomers(
        @Header("Authorization") token: String,
    ): Call<AllCustomersResponse>

    @GET("cart_items/customer/{id}")
    fun getCartItemsByCostumerId(
        @Header("Authorization") token: String,
        @Path("id") customerId: Int  // ID item yang ingin di panggil
    ): Call<CartItemsResponse>

    // Metode DELETE untuk menghapus data dari endpoint sales/{id}
    @DELETE("cart_items/{id}")
    fun deleteCartItems(
        @Header("Authorization") token: String,  // Token otorisasi
        @Path("id") salesId: Int  // ID item yang ingin dihapus
    ): Call<CartItemsResponse>

    //@GET("cart_items/{id}")
//    @GET("customers/{id}")
//    fun getCustomerById(
//        @Header("Authorization") token: String,
//        @Path("id") customerId: Int  // ID item yang ingin di panggil
////        @Query("id") customerId: Int  // ID item yang ingin di panggil
//    ): Call<ListCustomersItem>

//    @POST("sales/to_history")
//    suspend fun addSalesToHistory(
//        @Header("Authorization") token: String,
////        @Body salesStocksRequest: SalesStocksRequest,
//    ): ApiResponse
    @GET("stocks")
    suspend fun getAllStocks(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): AllStocksResponse

    @GET("stocks")
    fun getStocks(
        @Header("Authorization") token: String,
    ): Call<AllStocksResponse>

    @GET("vendors")
    fun getVendors(
        @Header("Authorization") token: String,
    ): Call<AllVendorsResponse>

//    @GET("customers")
////    @GET("customers")
//    suspend fun getCustomer(
//        @Header("Authorization") token: String,
////        @Path("id") id: Int  // ID item yang ingin di panggil
//    ): AllCustomersResponse

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
