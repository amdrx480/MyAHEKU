package com.dicoding.picodiploma.loginwithanimation.data.remote

import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.category.CategoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.customers.AllCustomersResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher.LoginWithVoucherRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher.LoginWithVoucherResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.CartItemsResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.units.UnitsResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsResponse
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("auth/voucher")
    suspend fun login(
        @Body loginRequest: LoginWithVoucherRequest,
    ): LoginWithVoucherResponse

    @POST("auth/login_voucher")
    suspend fun loginVoucher(
        @Body loginRequest: LoginWithVoucherRequest,
    ): LoginWithVoucherResponse

    @GET("admin/profile")
    suspend fun fetchAdminProfile(
        @Header("Authorization") data: String,
    ): ProfileResponse

    @POST("purchases")
    suspend fun addPurchaseStocks(
        @Header("Authorization") data: String,
        @Body purchaseRequest: PurchasesRequest
    ): ApiResponse

    @GET("purchases")
    suspend fun getAllPurchase(
        @Header("Authorization") data: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): PurchasesResponse

    @GET("item_transactions")
    suspend fun getAllItemTransactions(
        @Header("Authorization") data: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): ItemTransactionsResponse

    @POST("cart_items")
    suspend fun addItems(
        @Header("Authorization") data: String,
        @Body salesStocksRequest: SalesStocksRequest,
    ): ApiResponse

    @GET("customers")
    fun getCustomers(
        @Header("Authorization") data: String,
    ): Call<AllCustomersResponse>

    @GET("cart_items/customer/{id}")
    fun getCartItemsByCostumerId(
        @Header("Authorization") data: String,
        @Path("id") customerId: Int  // ID item yang ingin di panggil
    ): Call<CartItemsResponse>

    // Metode DELETE untuk menghapus data dari endpoint sales/{id}
    @DELETE("cart_items/{id}")
    fun deleteCartItems(
        @Header("Authorization") data: String,  // data otorisasi
        @Path("id") salesId: Int  // ID item yang ingin dihapus
    ): Call<CartItemsResponse>

    @POST("item_transactions/{customer_id}")
    suspend fun postItemTransactions(
        @Header("Authorization") data: String,
        @Path("customer_id") customerId: Int  // ID item yang ingin di panggil
    ): ApiResponse

    @GET("stocks")
    suspend fun getAllStocks(
        @Header("Authorization") data: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("search") search: String? = null,
        @Query("category_name") categoryName: String? = null,
        @Query("unit_name") unitName: String? = null,
        @Query("selling_price_min") sellingPriceMin: Int? = null,
        @Query("selling_price_max") sellingPriceMax: Int? = null
    ): StocksResponse

    @GET("stocks")
    fun getStocks(
        @Header("Authorization") data: String,
    ): Call<StocksResponse>

    @GET("vendors")
    suspend fun fetchVendors(
        @Header("Authorization") data: String,
    ): VendorsResponse

    @GET("units")
    suspend fun getUnits(
        @Header("Authorization") data: String,
    ): UnitsResponse

    @GET("categories")
    suspend fun getCategories(
        @Header("Authorization") token: String,
    ): CategoryResponse


}



//        @Body categoryRequest: CategoryRequest,
//    ): CategoryResponse
//    @GET("categories")
//    fun getCategories(
//        @Header("Authorization") data: String,
//    ): Call<AllCategoryResponse>
//    @GET("categories")
//    suspend fun getCategorylifecycleScope(
//        @Header("Authorization") data: String,
////        @Body listCategoryItem: ListCategoryItem
//    ): AllCategoryResponse

//    @GET("categories")
//    suspend fun getCategory(
//        @Header("Authorization") data: String,
////        @Body listCategoryItem: ListCategoryItem
//    ): ApiResponse


//    @GET("customers")
////    @GET("customers")
//    suspend fun getCustomer(
//        @Header("Authorization") data: String,
////        @Path("id") id: Int  // ID item yang ingin di panggil
//    ): AllCustomersResponse

//@GET("cart_items/{id}")
//    @GET("customers/{id}")
//    fun getCustomerById(
//        @Header("Authorization") data: String,
//        @Path("id") customerId: Int  // ID item yang ingin di panggil
////        @Query("id") customerId: Int  // ID item yang ingin di panggil
//    ): Call<ListCustomersItem>

//    @POST("sales/to_history")
//    suspend fun addSalesToHistory(
//        @Header("Authorization") data: String,
////        @Body salesStocksRequest: SalesStocksRequest,
//    ): ApiResponse