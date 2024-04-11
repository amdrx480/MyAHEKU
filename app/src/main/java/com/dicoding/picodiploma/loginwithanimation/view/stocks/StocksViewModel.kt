package com.dicoding.picodiploma.loginwithanimation.view.stocks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.Event
import com.dicoding.picodiploma.loginwithanimation.model.stocks.AllStocksResponse
import com.dicoding.picodiploma.loginwithanimation.model.stocks.ListStocksItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
//v
class StocksViewModel : ViewModel() {
    private val mItemStory = MutableLiveData<List<ListStocksItem>>()
    val itemStory: LiveData<List<ListStocksItem>> = mItemStory

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val misHaveData = MutableLiveData<Boolean>()
    val isHaveData: LiveData<Boolean> = misHaveData

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

        fun showListStocks(token: String) {
        mIsLoading.value = true
        misHaveData.value = true

            val service = ApiConfig
            .ApiService()
            .getAllStocks("Bearer $token")
            //bisa
//                .getAllStocks()


            service.enqueue(object : Callback<AllStocksResponse> {
            override fun onResponse(
                call: Call<AllStocksResponse>,
                response: Response<AllStocksResponse>
            ) {
                mIsLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d(TAG, "Response body: $responseBody")
                        if (!responseBody.error) {
                            Log.d(TAG, "Response message: ${responseBody.message}")
                            mItemStory.value = response.body()?.token
                            misHaveData.value = responseBody.message == "Stories fetched successfully"
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    mSnackBarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<AllStocksResponse>, t: Throwable) {
                mIsLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                mSnackBarText.value = Event(t.message.toString())
            }
        })
    }

    companion object {
        private const val TAG = "ListStockViewModel"
    }
}