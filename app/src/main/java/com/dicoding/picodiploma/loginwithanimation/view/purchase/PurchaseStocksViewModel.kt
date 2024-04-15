package com.dicoding.picodiploma.loginwithanimation.view.purchase

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.purchase.ListPurchaseItem
import com.dicoding.picodiploma.loginwithanimation.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.data.purchase.PurchaseResponse
import com.dicoding.picodiploma.loginwithanimation.helper.helper
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseStocksViewModel(private val repository: UserPreference): ViewModel() {
    private val _isHaveData = MutableLiveData<List<ListPurchaseItem>>()
//    val isHaveData: LiveData<List<ListPurchaseItem>> = _isHaveData

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun purchaseStocks(token: String, purchaseRequest: PurchaseRequest, param: helper.ApiCallBackString) {
        val service = ApiConfig
            .ApiService()
            .setPurchaseStocks("Bearer $token", purchaseRequest)

        service.enqueue(object : Callback<PurchaseResponse> {
            override fun onResponse(
                call: Call<PurchaseResponse>,
                response: Response<PurchaseResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d(TAG, "Response body: $responseBody")
                        if (!responseBody.error) {
                            Log.d(TAG, "Response message: ${responseBody.message}")
                            _isHaveData.value = response.body()?.token
                            param.onResponse(true, responseBody.message)
                        } else {
                            // Di sini Anda perlu memanggil callback onResponse untuk memberitahu kelas lain bahwa respons gagal dengan pesan error
                            param.onResponse(false, responseBody.message)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PurchaseResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "ListPurchaseViewModel"
    }
}