package com.dicoding.picodiploma.loginwithanimation.ui.login

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher.LoginWithVoucherRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher.LoginWithVoucherResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.launch

//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject

//@HiltViewModel
//class LoginVoucherViewModel @Inject constructor(private val authRepository: AuthRepository) :

class LoginVoucherViewModel(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _loginResponse = MutableLiveData<ResultResponse<LoginWithVoucherResponse>>()
    val loginResponse: LiveData<ResultResponse<LoginWithVoucherResponse>> = _loginResponse

    fun loginVoucher(
        loginWithVoucherRequest: LoginWithVoucherRequest
    ) = authRepository.loginVoucher(loginWithVoucherRequest)

    fun saveAuthVoucher(token: String) {
        viewModelScope.launch {
            authRepository.saveAuthVoucher(token)
        }
    }

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }

    fun saveAuthStateToken(token: Boolean) {
        viewModelScope.launch {
            authRepository.saveAuthStateToken(token)
        }
    }
}

//fun getAuthToken(): LiveData<String?> {
//    return authRepository.getAuthToken().asLiveData()
//}
//    fun saveAuthToken(token: String) {
//        viewModelScope.launch {
//            authRepository.saveAuthToken(token)
//        }
//    }
//    fun loginVoucher(voucher: String) = viewModelScope.launch {
//        _loginResponse.value = authRepository.loginVoucher(voucher)
//    }
//    {
//        viewModelScope.launch {
//            try {
//                _loginResult.value = ResultResponse.Loading // Menampilkan loading state
//                authRepository.loginVoucher(pass).collect { result ->
//                    _loginResult.value = result // Mengirim hasil operasi login ke LiveData
//                }
//            } catch (e: Exception) {
//                _loginResult.value = ResultResponse.Error(
//                    e.message ?: "An error occurred"
//                ) // Menyimpan pesan error jika terjadi kesalahan
//            }
//        }
//    }

//    fun loginVoucher(voucher: String) {
//        viewModelScope.launch {
//            authRepository.loginVoucher(voucher)
//        }
//    }

//    fun getAuthToken(): LiveData<String?> {
//        val authTokenLiveData = MutableLiveData<String?>()
//        viewModelScope.launch {
//            authRepository.getAuthToken().collect {
//                authTokenLiveData.value = it
//            }
//        }
//        return authTokenLiveData
//    }

//    private val _loginResult = MutableStateFlow<ResultResponse<AuthToken>>(ResultResponse.Idle)
//    val loginResult: StateFlow<ResultResponse> = _loginResult.asStateFlow()

//    suspend fun loginVoucher(password: String) = authRepository.loginVoucher(password)


//    fun loginVoucher(password: String) {
//        viewModelScope.launch {
//            authRepository.loginVoucher(password)
//        }
//    }

//    suspend fun loginVoucher(password: String) = authRepository.loginVoucher(password)
