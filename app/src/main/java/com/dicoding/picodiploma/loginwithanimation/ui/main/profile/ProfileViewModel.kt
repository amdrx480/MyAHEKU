package com.dicoding.picodiploma.loginwithanimation.ui.main.profile

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import kotlinx.coroutines.launch


class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {
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