package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.datamaster

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.category.CategoryModel
import com.dicoding.picodiploma.loginwithanimation.data.model.customers.customers.CustomersModel
import com.dicoding.picodiploma.loginwithanimation.data.model.units.UnitsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DataMasterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _vendors = MutableLiveData<List<VendorsModel>>()
    val vendors: LiveData<List<VendorsModel>> get() = _vendors

    private val _customerNames = MutableLiveData<List<CustomersModel>>()
    val customerNames: LiveData<List<CustomersModel>> get() = _customerNames

    private val _categoryNames = MutableLiveData<List<CategoryModel>>()
    val categoryNames: LiveData<List<CategoryModel>> get() = _categoryNames

    private val _unitNames = MutableLiveData<List<UnitsModel>>()
    val unitNames: LiveData<List<UnitsModel>> get() = _unitNames

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

    init {
        loadAuthToken()
        authToken.observeForever { token ->
            token?.let {
                loadCategoriesAndUnits(it)
                fetchCustomers(it)
                fetchVendors(it)
            }
        }
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e ->
                    Log.e("DataMasterViewModel", "Error loading auth token", e)
                }
                .collect { token ->
                    _authToken.postValue(token)
                }
        }
    }

    private fun loadCategoriesAndUnits(token: String) {
        viewModelScope.launch {
            authRepository.fetchCategories(token).observeForever { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Handle loading state if needed
                    }
                    is ResultResponse.Success -> {
                        _categoryNames.value = result.data
                    }
                    is ResultResponse.Error -> {
                        Log.e("DataMasterViewModel", "Error loading categories: ${result.error}")
                    }
                }
            }

            authRepository.fetchUnits(token).observeForever { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Handle loading state if needed
                    }
                    is ResultResponse.Success -> {
                        _unitNames.value = result.data
                    }
                    is ResultResponse.Error -> {
                        Log.e("DataMasterViewModel", "Error loading units: ${result.error}")
                    }
                }
            }
        }
    }

    fun postVendors(
        token: String,
        vendorsRequest: VendorsRequest
    ): LiveData<ResultResponse<VendorsResponse>> {
        // Ambil token dari _authToken
        val token = _authToken.value
        // Pastikan token tidak null sebelum melakukan pemanggilan
        requireNotNull(token) { "Auth token should not be null" }
        return authRepository.postVendors(token, vendorsRequest)
    }

    fun fetchVendors(token: String?) {
        viewModelScope.launch {
            authRepository.fetchVendors(token).observeForever { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Handle loading state if needed
                        Log.d("DataMasterViewModel", "Fetching vendors: Loading...")
                    }
                    is ResultResponse.Success -> {
                        _vendors.value = result.data
                        Log.d("DataMasterViewModel", "Fetching vendors: Success")
                    }
                    is ResultResponse.Error -> {
                        Log.e("DataMasterViewModel", "Error loading vendors: ${result.error}")
                    }
                }
            }
        }
    }

    fun fetchCustomers(token: String?) {
        viewModelScope.launch {
            authRepository.fetchCustomers(token).observeForever { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Handle loading state if needed
                        Log.d("DataMasterViewModel", "Fetching Customers: Loading...")
                    }
                    is ResultResponse.Success -> {
                        _customerNames.value = result.data
                        Log.d("DataMasterViewModel", "Fetching Customers: Success")
                    }
                    is ResultResponse.Error -> {
                        Log.e("DataMasterViewModel", "Error loading Customers: ${result.error}")
                    }
                }
            }
        }
    }


    fun refreshVendors() {
        _authToken.value?.let { fetchVendors(it) }
    }
}

//    fun fetchVendors(token: String?) {
//        viewModelScope.launch {
//            _vendors.value = ResultResponse.Loading
//            try {
//                val response = apiService.fetchVendors("Bearer $token")
//                _vendors.value = ResultResponse.Success(response.data)
//            } catch (e: Exception) {
//                _vendors.value = ResultResponse.Error(e.message ?: "Unknown error")
//            }
//        }
//    }

//    fun fetchVendors(token: String) {
//        viewModelScope.launch {
//            authRepository.fetchVendors(token).observeForever { result ->
//                when (result) {
//                    is ResultResponse.Loading -> {
//                        // Handle loading state if needed
//                    }
//                    is ResultResponse.Success -> {
//                        _vendors.value = result.data
//                    }
//                    is ResultResponse.Error -> {
//                        Log.e("DataMasterViewModel", "Error loading vendors: ${result.error}")
//                    }
//                }
//            }
//        }
//    }
