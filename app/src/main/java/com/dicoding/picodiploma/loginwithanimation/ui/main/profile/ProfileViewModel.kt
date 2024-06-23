package com.dicoding.picodiploma.loginwithanimation.ui.main.profile

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> = _authToken

    init {
        loadAuthToken()
    }

    fun fetchAdminProfile(): LiveData<ResultResponse<ProfileModel>> = liveData {
        emit(ResultResponse.Loading)
        authToken.value?.let { token ->
            val result = authRepository.fetchAdminProfile(token)
            emitSource(result)
        } ?: run {
            emit(ResultResponse.Error("Token not found"))
        }
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e -> Log.e("ProfileViewModel", "Error loading auth token", e) }
                .collect { token ->
                    _authToken.postValue(token)
                }
        }
    }

    fun logoutVoucher() {
        viewModelScope.launch {
            authRepository.logoutVoucher()
        }
    }
}

//    private val _authToken = MutableLiveData<String?>()
//    val authToken: LiveData<String?> = _authToken
//
//    init {
//        viewModelScope.launch {
//            // Get auth token from repository
//            authRepository.getAuthToken().collect { token ->
//                _authToken.value = token
//            }
//        }
//    }

//class ProfileViewModel(private val repository: AuthPreferenceDataSource) : ViewModel() {
//    fun getSession(): LiveData<UserModel> {
//        return repository.getSession().asLiveData()
//    }
//    fun logout() {
//        viewModelScope.launch {
//            repository.logout()
//        }
//    }
//}