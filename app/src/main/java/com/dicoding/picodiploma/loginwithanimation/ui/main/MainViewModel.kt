package com.dicoding.picodiploma.loginwithanimation.ui.main

import androidx.lifecycle.*
//import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {

//    fun getAuthToken(): LiveData<String?> {
//        val authTokenLiveData = MutableLiveData<String?>()
//        viewModelScope.launch {
//            authRepository.getAuthToken().collect {
//                authTokenLiveData.value = it
//            }
//        }
//        return authTokenLiveData
//    }

    //    fun getAuthToken(): LiveData<Boolean>{
//        return authRepository.getAuthToken().asLiveData()
//    }
//    private val _authToken = MutableLiveData<String?>()
//    val authToken: LiveData<String?> = _authToken
//
//    init {
//        fun fetchAuthToken() {
//            viewModelScope.launch {
//                // Get auth token from repository
//                authRepository.getAuthToken().collect { token ->
//                    _authToken.value = token
//                }
//            }
//        }
//    }

    fun getAuthToken(): LiveData<String?> {
        val authTokenLiveData = MutableLiveData<String?>()
        viewModelScope.launch {
            authRepository.getAuthToken().collect {
                authTokenLiveData.value = it
            }
        }
        return authTokenLiveData
    }

//    fun getSession(): LiveData<UserModel> {
//        return repository.getSession().asLiveData()
//    }
}