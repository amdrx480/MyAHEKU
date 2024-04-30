package com.dicoding.picodiploma.loginwithanimation.view.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.service.api.Event
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.ListSalesStocksItem
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.SalesStocksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class SalesStocksViewModel(private val repository: AppRepository): ViewModel() {
class SalesStocksViewModel: ViewModel() {
    private val mItemSalesStock = MutableLiveData<List<ListSalesStocksItem>>()
    val itemSalesStock: LiveData<List<ListSalesStocksItem>> = mItemSalesStock

//    private val mIsLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = mIsLoading
//
//    private val misHaveData = MutableLiveData<Boolean>()
//    val isHaveData: LiveData<Boolean> = misHaveData

//    private val mSnackBarText = MutableLiveData<Event<String>>()
//    val snackBarText: LiveData<Event<String>> = mSnackBarText

    fun showListSalesStock(token: String) {
//        mIsLoading.value = true
//        misHaveData.value = true

        val client = ApiConfig
            .ApiService()
            .getSalesStocks("Bearer $token")

        client.enqueue(object : Callback<SalesStocksResponse> {
            override fun onResponse(
                call: Call<SalesStocksResponse>,
                response: Response<SalesStocksResponse>
            ) {
//                mIsLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            mItemSalesStock.value = response.body()?.token
//                            misHaveData.value = responseBody.message == "Stories fetched successfully"
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
//                    mSnackBarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<SalesStocksResponse>, t: Throwable) {
//                mIsLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
//                mSnackBarText.value = Event(t.message.toString())
            }
        })
    }

    companion object {
        private const val TAG = "ListStoryViewModel"
    }



//    fun uploadSalesStocks(
//        token: String,
//        salesStocksRequest: SalesStocksRequest
//    ) = repository.postSalesStocks(token, salesStocksRequest)

//    fun getSalesStocks(
//        token: String,
//    ) = repository.getSalesStocks(token)


}