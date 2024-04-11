package com.dicoding.picodiploma.loginwithanimation.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.login.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.data.login.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.helper.helper
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserPreference) : ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = mIsLoading

    fun login(pass: String, callback: helper.ApiCallBackString){
        mIsLoading.value = true

        val loginRequest = LoginRequest(pass)
        val service =
            ApiConfig()
                .ApiService()
                .login(loginRequest)

        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback.onResponse(response.body() != null, SUCCESS)

                        val model = UserModel(
                            pass,
                            responseBody.token,
                            true
                        )
                        saveSession(model)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")

                    val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    callback.onResponse(false, message)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                mIsLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                callback.onResponse(false, t.message.toString())
            }
        })
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    companion object {
        private const val TAG = "SignInViewModel"
        private const val SUCCESS = "success"
    }
}