package com.dicoding.picodiploma.loginwithanimation.view.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.category.ListCategoryItem
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class CategoryViewModel: ViewModel() {
//    private val mIsLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = mIsLoading
//
//    fun getCategory(
//        user: UserModel,
//        listCategoryItem: ListCategoryItem,
//        callback: helper.ApiCallBackString
//
//    ) {
//        mIsLoading.value = true
//        val service = ApiConfig().ApiService().getCategories("Bearer ${user.token}", listCategoryItem)
//        service.enqueue(object : Callback<ApiResponse> {
//            override fun onResponse(
//                call: Call<ApiResponse>,
//                response: Response<ApiResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null && !responseBody.error) {
//                        callback.onResponse(response.body() != null, SUCCESS)
//                    }
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//
//                    // get message error
//                    val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
//                    val message = jsonObject.getString("message")
//                    callback.onResponse(false, message)
//                }
//            }
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                mIsLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
//                callback.onResponse(false, t.message.toString())
//            }
//        })
//
//    }
//
//    companion object {
//        private const val TAG = "AddStoryViewModel"
//        private const val SUCCESS = "success"
//    }
//}