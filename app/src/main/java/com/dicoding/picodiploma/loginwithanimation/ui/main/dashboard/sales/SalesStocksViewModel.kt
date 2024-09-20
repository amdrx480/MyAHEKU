package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.CartItemsResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.CartItemsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.customers.AllCustomersResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.customers.ListCustomersItem
import com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders.PurchaseOrderRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class SalesStocksViewModel(private val repository: AppRepository): ViewModel() {
//class SalesStocksViewModel(private val repository: AppRepository) : ViewModel() {
//class SalesStocksViewModel(private val repository: AppRepository) : ViewModel() {
class SalesStocksViewModel(private val repository: AuthRepository) : ViewModel() {


    // AddStockActivity & CartActivity
    private val _customersList = MutableLiveData<List<ListCustomersItem>>()
    val customersList: LiveData<List<ListCustomersItem>> = _customersList

    // CartActivity
    private val _cartItems = MutableLiveData<List<CartItemsModel>>()
    val cartItems: LiveData<List<CartItemsModel>> = _cartItems

    // CartActivity
    private val _itemDelete = MutableLiveData<List<CartItemsResponse>>()
    val itemDelete: LiveData<List<CartItemsResponse>> = _itemDelete

    // AddStockActivity
    private val _itemStock = MutableLiveData<List<StocksEntity>>()
    val itemStock: LiveData<List<StocksEntity>> = _itemStock



    private val _customerList = MutableLiveData<List<ListCustomersItem>>()
    val customerList: LiveData<List<ListCustomersItem>> = _customerList

    private val _selectedCustomers = MutableLiveData<ListCustomersItem?>()
    val selectedCustomers: LiveData<ListCustomersItem?> = _selectedCustomers

    fun setSelectedCustomers(customers: ListCustomersItem?) {
        _selectedCustomers.value = customers
    }

    private val _selectedItemStock = MutableLiveData<StocksEntity?>()
    val selectedItemStock: LiveData<StocksEntity?> = _selectedItemStock
    fun setSelectedStockItem(stock: StocksEntity?) {
        _selectedItemStock.value = stock
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isHaveData = MutableLiveData<Boolean>()
    val isHaveData: LiveData<Boolean> = _isHaveData

    fun getCustomers(token: String) {
        val client = ApiConfig.ApiService()
            .getCustomers("Bearer $token")

        client.enqueue(object : Callback<AllCustomersResponse> {
            override fun onResponse(
                call: Call<AllCustomersResponse>,
                response: Response<AllCustomersResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        // Set data customer ke LiveData
                        _customersList.value = responseBody.data
//                        _customersList.value = listOf(responseBody.token)
                        Log.e(TAG, "onSuccessCustomers: ${response.message()}")
                    } else {
                        Log.e(TAG, "Failed to fetch Customers: ${response.message()}")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch Customers: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AllCustomersResponse>, t: Throwable) {
                Log.e(TAG, "API call failure: ${t.message}")
            }
        })
    }

    fun getCartItemsForCustomer(token: String, customerId: Int) {
        _isLoading.value = true
        _isHaveData.value = true

        val client = ApiConfig
            .ApiService()
            .getCartItemsByCostumerId("Bearer $token", customerId)

        client.enqueue(object : Callback<CartItemsResponse> {
            override fun onResponse(
                call: Call<CartItemsResponse>,
                response: Response<CartItemsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _cartItems.value = responseBody.data
//                        _isHaveData.value = responseBody.customer_name == "customer_name CartItem fetched successfully"
                        _isHaveData.value = responseBody.message == "customer_name CartItem fetched successfully"
//                        _cartItems.value = response.body()?.cart_items
                        Log.e(TAG, "onSuccess getCartItemsForCustomer: ${response.message()}")
                    }
                } else {
                    Log.e(TAG, "onFailure getCartItemsForCustomer: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CartItemsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure : ${t.message}")
            }
        })
    }

    fun deleteListItems(token: String, itemsId: Int) {
        val client = ApiConfig
            .ApiService()
            .deleteCartItems("Bearer $token", itemsId)

        client.enqueue(object : Callback<CartItemsResponse> {
            override fun onResponse(
                call: Call<CartItemsResponse>,
                response: Response<CartItemsResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
//                        _cartItems.value = responseBody.token
//                        _isHaveData.value = responseBody.customer_name == "customer_name CartItem fetched successfully"
//                        _isHaveData.value = responseBody.message == "customer_name CartItem fetched successfully"
//                        if (!responseBody.error) {
//                            _itemDelete.value = responseBody
//                            _itemDelete.value = response.body()
//                            _isHaveData.value = responseBody.message == "CartItem deleted successfully"
//                            itemsId.removeAt
//                            _itemDelete.value = responseBody.error
//                            _itemDelete.value = responseBody.error == "CartItem deleted successfully"
//                            _itemDelete.value = responseBody.message == "CartItem deleted successfully"

                            Log.e(TAG, "onSuccessDeleteCartItems: ${response.message()}")
//                        }
                    }
                } else {
                    Log.e(TAG, "onFailureDeleteCartItems: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<CartItemsResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun showListStock(token: String) {
        val client = ApiConfig
            .ApiService()
            .getStocks("Bearer $token")

        client.enqueue(object : Callback<StocksResponse> {
            override fun onResponse(
                call: Call<StocksResponse>,
                response: Response<StocksResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _itemStock.value = responseBody.data
                            Log.e(TAG, "onSuccessSales: ${response.message()}")
                        }
                    }
                } else {
                    Log.e(TAG, "onFailureSales: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StocksResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun uploadItems(
        token: String,
        salesStocksRequest: SalesStocksRequest
    ) = repository.postItems(token, salesStocksRequest)

    fun postPurchaseOrders(
        token: String,
        customerId: Int,
        purchaseOrderRequest: PurchaseOrderRequest
    ) = repository.postPurchaseOrders(token, customerId, purchaseOrderRequest)

    fun postItemTransactions(
        token: String,
        customerId: Int,
    ) = repository.postItemTransactions(token, customerId)

//        itemTransactionsRequest: ItemTransactionsRequest
//    ) = repository.postItemTransactions(token, itemTransactionsRequest)




//    fun fetchCustomerById(token: String, customerId: Int) {
//        val client = ApiConfig
//            .ApiService()
//            .getCustomerById("Bearer $token", customerId)
//
//        client.enqueue(object : Callback<ListCustomersItem> {
//            override fun onResponse(
//                call: Call<ListCustomersItem>,
//                response: Response<ListCustomersItem>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    _customerList.value = responseBody?.cart_items
//                    Log.e(TAG, "onSuccess getCartItemsForCustomer: ${response.message()}")
////                    }
//                } else {
//                    Log.e(TAG, "onFailure getCartItemsForCustomer: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ListCustomersItem>, t: Throwable) {
//                Log.e(TAG, "onFailure : ${t.message}")
//            }
//        })
//    }

//    fun uploadSalesToHistory(
//        token: String,
//    ) = repository.postSalesToHistory(token)

    //    fun addSalesStock(token: String) {
//        val client = ApiConfig
//            .ApiService()
//            .addSalesStocks("Bearer $token")
//
//        client.enqueue(object : Callback<SalesStocksResponse> {
//            override fun onResponse(
//                call: Call<SalesStocksResponse>,
//                response: Response<SalesStocksResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        if (!responseBody.error) {
//                            _itemSalesStock.value = responseBody.token
//                            Log.e(TAG, "onSuccessSales: ${response.message()}")
//                        }
//                    }
//                } else {
//                    Log.e(TAG, "onFailureSales: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<SalesStocksResponse>, t: Throwable) {
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//        })
//    }
    companion object {
        private const val TAG = "ListCartViewModel"
    }

    //    fun showListSales(
//        token: String,
//    ) = repository.getSalesStocks(token)

//    fun showListSalesStock(token: String) {
////        _isLoading.value = true
////        _isHaveData.value = true
//
//        val client = ApiConfig
//            .ApiService()
//            .getSalesStocks("Bearer $token")
//
//        client.enqueue(object : Callback<SalesStocksResponse> {
//            override fun onResponse(
//                call: Call<SalesStocksResponse>,
//                response: Response<SalesStocksResponse>
//            ) {
////                _isLoading.value = false
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        if (!responseBody.error) {
//                            mItemSalesStock.value = response.body()?.token
////                            _isHaveData.value = responseBody.message == "Stories fetched successfully"
//                        }
//                    }
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
////                    mSnackBarText.value = Event(response.message())
//                }
//            }
//
//            override fun onFailure(call: Call<SalesStocksResponse>, t: Throwable) {
////                _isLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
////                mSnackBarText.value = Event(t.message.toString())
//            }
//        })
//    }


//    fun uploadSalesStocks(
//        token: String,
//        salesStocksRequest: SalesStocksRequest
//    ) = repository.postSalesStocks(token, salesStocksRequest)

//    fun getSalesStocks(
//        token: String,
//    ) = repository.getSalesStocks(token)

//    private val _customer = MutableLiveData<ListCustomersItem>()
//    val customer: LiveData<ListCustomersItem> get() = _customer
//
//    fun fetchCustomerById(token: String) {
//        viewModelScope.launch {
//            val customer = repository.getCustomers(token)
//            customer?.let {
//                _customer.postValue(it)
//            }
//        }
//    }
}


//                    if (responseBody != null && !responseBody.error) {
//                          _cartItems.value = responseBody?.cart_items


//                        val cartItemsList = responseBody.token.firstOrNull()?.cart_items ?: emptyList()
//                        _cartItems.postValue(cartItemsList) // Perbarui LiveData dengan nilai yang baru
//                        val cartItemsList = mutableListOf<CartItem>()
//                        for (customer in responseBody.token) {
//                            cartItemsList.addAll(customer.cart_items)
//                        }
//                        _cartItems.value = cartItemsList
//
//                        _cartItems.value = responseBody.token.flatMap { it.cart_items }
//                        _cartItems.value = responseBody.token.firstOrNull()?.cart_items
//                        check Point
//                        _cartItems.value = responseBody.token.firstOrNull()?.cart_items ?: emptyList()
//                        _cartItems.value = response.body()?.token?.firstOrNull()?.cart_items ?: emptyList()