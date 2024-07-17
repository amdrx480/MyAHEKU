package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _profile  = MutableLiveData<ResultResponse<ProfileModel>>()
    val profile : LiveData<ResultResponse<ProfileModel>> get() = _profile

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

    private val _authVoucher = MutableLiveData<String?>()
    val authVoucher: LiveData<String?> get() = _authVoucher

    private val _isAuthTokenAvailable = MutableLiveData<Boolean>()
    val isAuthTokenAvailable: LiveData<Boolean> get() = _isAuthTokenAvailable

    init {
        loadAuthToken()
        loadAuthVoucher()
        checkAuthState()
        fetchAdminProfile()
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

//    fun fetchAdminProfile() {
//        viewModelScope.launch {
//            _profile.value = ResultResponse.Loading
//            authToken.value?.let { token ->
//                val result = authRepository.fetchAdminProfile(token)
//                _profile.value = result.value
//            } ?: run {
//                _profile.value = ResultResponse.Error("Token tidak ditemukan")
//            }
//        }
//    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e -> Log.e("DashboardViewModel", "Error loading auth token", e) }
                .collect { token ->
                    _authToken.postValue(token)
                }
        }
    }

    private fun loadAuthVoucher() {
        viewModelScope.launch {
            authRepository.getAuthVoucher()
                .catch { e -> Log.e("DashboardViewModel", "Error loading auth voucher", e) }
                .collect { voucher ->
                    _authVoucher.postValue(voucher)
                }
        }
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            authRepository.getAuthStateToken()
                .catch { e -> Log.e("DashboardViewModel", "Error checking auth state", e) }
                .collect { isAvailable ->
                    _isAuthTokenAvailable.postValue(isAvailable)
                }
        }
    }
}

//    private fun loadAuthToken() {
//        viewModelScope.launch {
//            val token = authRepository.getAuthToken()
//            _authToken.postValue(token)
//        }
//    }
//
//    private fun loadAuthVoucher() {
//        viewModelScope.launch {
//            val voucher = authRepository.getAuthVoucher()
//            _authVoucher.postValue(voucher)
//        }
//    }
//
//    private fun checkAuthState() {
//        viewModelScope.launch {
//            val isTokenAvailable = authRepository.isAuthTokenAvailable()
//            _isAuthTokenAvailable.postValue(isTokenAvailable)
//        }
//    }
//
//    fun clearAuthToken() {
//        viewModelScope.launch {
//            authRepository.clearAuthToken()
//            _authToken.postValue(null)
//            checkAuthState()
//        }
//    }
//
//    fun clearAuthVoucher() {
//        viewModelScope.launch {
//            authRepository.clearAuthVoucher()
//            _authVoucher.postValue(null)
//        }
//    }
//
//    fun saveAuthToken(token: String) {
//        viewModelScope.launch {
//            authRepository.saveAuthToken(token)
//            _authToken.postValue(token)
//            checkAuthState()
//        }
//    }
//
//    fun saveAuthVoucher(voucher: String) {
//        viewModelScope.launch {
//            authRepository.saveAuthVoucher(voucher)
//            _authVoucher.postValue(voucher)
//        }
//    }
//
//    fun saveAuthStateToken(isAvailable: Boolean) {
//        viewModelScope.launch {
//            authRepository.saveAuthStateToken(isAvailable)
//            _isAuthTokenAvailable.postValue(isAvailable)
//        }
//    }
//}
//
//    fun getAuthToken(): LiveData<String?> {
//        val authTokenLiveData = MutableLiveData<String?>()
//        viewModelScope.launch {
//            authRepository.getAuthToken().collect {
//                authTokenLiveData.value = it
//            }
//        }
//        return authTokenLiveData
//    }

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
//
//    }
//}
//    fun getAuthToken(): LiveData<Boolean>{
//        return authRepository.getAuthToken().asLiveData()
//    }

//    fun getSession(): LiveData<UserModel> {
//        return userPref.getSession().asLiveData()
//    }
//    }