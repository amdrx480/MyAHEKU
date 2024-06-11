package com.dicoding.picodiploma.loginwithanimation.ui.splash

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository

class SplashScreenViewModel(private val authRepository: AuthRepository) :
    ViewModel() {

    fun getAuthStateToken(): LiveData<Boolean>{
        return authRepository.getAuthStateToken().asLiveData()
    }
}



//fun saveAuthToken(tokenVoucher: String){
//    viewModelScope.launch {
//        authRepository.saveAuthToken(tokenVoucher)
//    }
//}
//
//fun saveLoginState(loginState: String){
//    viewModelScope.launch {
//        authRepository.saveAuthToken(loginState)
//    }
//}
//
//fun saveAuthStateToken(stateVoucher: Boolean){
//    viewModelScope.launch {
//        authRepository.saveAuthStateToken(stateVoucher)
//    }
//}



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

//    fun saveAuthToken(token: String) {
//        viewModelScope.launch {
//            authRepository.saveAuthToken(token)
//        }
//    }

//    fun getAuthToken(): LiveData<Boolean>{
//        return authRepository.getAuthToken().asLiveData()
//    }

//    fun getAuthToken(): LiveData<Boolean> {
////        return authRepository.getAuthToken().asLiveData()
//
//        return authRepository.getAuthToken()
////        viewModelScope.launch {
////            authRepository.getAuthToken()
////        }
////            authRepository.getAuthToken(token)
//        }
//    }

//    private val _loginResult = MutableLiveData<ResultResponse<LoginWithVoucherResponse>>()
//    val loginResult: LiveData<ResultResponse<LoginWithVoucherResponse>> = _loginResult
//
//    fun loginVoucher(pass: String) {
//        viewModelScope.launch {
//            try {
//                _loginResult.value = ResultResponse.Loading // Menampilkan loading state
//                authRepository.loginVoucher(pass).collect { result ->
//                    _loginResult.value = result // Mengirim hasil operasi login ke LiveData
//                }
//            } catch (e: Exception) {
//                _loginResult.value = ResultResponse.Error(e.message ?: "An error occurred") // Menyimpan pesan error jika terjadi kesalahan
//            }
//        }
//    }
//
//    fun saveAuthToken(token: String) {
//        viewModelScope.launch {
//            authRepository.saveAuthToken(token)
//        }
//    }

//}