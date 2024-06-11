package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchase

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.category.CategoryModel
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.units.UnitsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PurchaseStocksViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // LiveData untuk menyimpan token autentikasi
    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

    // LiveData untuk menyimpan kategori yang dipilih
    private val _selectedCategory = MutableLiveData<CategoryModel?>()
    val selectedCategory: LiveData<CategoryModel?> get() = _selectedCategory

    // LiveData untuk menyimpan vendor yang dipilih
    private val _selectedVendor = MutableLiveData<VendorsModel?>()
    val selectedVendor: LiveData<VendorsModel?> = _selectedVendor

    // LiveData untuk menyimpan unit yang dipilih
    private val _selectedUnit = MutableLiveData<UnitsModel?>()
    val selectedUnit: LiveData<UnitsModel?> get() = _selectedUnit

    init {
        loadAuthToken()
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e -> Log.e("StocksViewModel", "Error loading auth token", e) }
                .collect { token ->
                    _authToken.postValue(token)
                }
        }
    }

    //    fun fetchVendors(token: String?) = authRepository.getVendors(token)
    fun fetchVendors(token: String?) = authRepository.fetchVendors(token)

    fun fetchUnits(token: String?) = authRepository.fetchUnits(token)

    fun fetchCategories(token: String?) = authRepository.fetchCategories(token)

    // Metode untuk mengatur vendor yang dipilih
    fun setSelectedVendor(vendor: VendorsModel) {
        _selectedVendor.value = vendor
    }

    fun setSelectedUnit(unit: UnitsModel) {
        _selectedUnit.value = unit
    }

    // Fungsi untuk menyimpan kategori yang dipilih
    fun setSelectedCategory(category: CategoryModel?) {
        _selectedCategory.value = category
    }

    fun uploadPurchaseStocks(
        token: String,
        purchaseRequest: PurchasesRequest
    ) = authRepository.postPurchaseStocks(token, purchaseRequest)

//    companion object {
//        private const val TAG = "ViewModel"
//    }
}


// LiveData untuk menyimpan daftar units
//    private val _unitList = MutableLiveData<List<UnitsEntity>>()
//    val unitList: LiveData<List<UnitsEntity>> = _unitList

// LiveData untuk menyimpan daftar vendors
//    private val _vendorList = MutableLiveData<List<ListVendorsItem>>()
//    val vendorList: LiveData<List<ListVendorsItem>> = _vendorList

//    private val _categoryList = MutableLiveData<List<CategoryModel>>()
//    val categoryList: LiveData<List<CategoryModel>> = _categoryList

//    // Metode untuk menyimpan vendor yang dipilih
//    fun setSelectedVendor(vendor: ListVendorsItem?) {
//        _selectedVendor.value = vendor
//    }
//
//    // Metode untuk menyimpan unit yang dipilih
//    fun setSelectedUnit(unit: UnitsEntity?) {
//        _selectedUnit.value = unit
//    }

//    private fun fetchCategories() {
//        viewModelScope.launch {
//            authRepository.getCategories(token).observeForever { result ->
//                when (result) {
//                    is ResultResponse.Loading -> {
//                        // Tampilkan loading jika diperlukan
//                    }
//                    is ResultResponse.Success -> {
//                        _categoryList.value = result.data.categories
//                    }
//                    is ResultResponse.Error -> {
//                        Log.e(TAG, "Error: ${result.message}")
//                    }
//                }
//            }
//        }
//    }

// Fungsi untuk mengambil data vendors dari API
//    fun getVendors(token: String) {
//        val client = ApiConfig.ApiService()
//            .getVendors("Bearer $token")
//
//        client.enqueue(object : Callback<AllVendorsResponse> {
//            override fun onResponse(
//                call: Call<AllVendorsResponse>,
//                response: Response<AllVendorsResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null && !responseBody.error) {
//                        // Set data vendors ke LiveData
//                        _vendorList.value = responseBody.data
//                        Log.e(TAG, "onSuccessVendors: ${response.message()}")
//                    } else {
//                        Log.e(TAG, "Failed to fetch vendors: ${response.message()}")
//                    }
//                } else {
//                    Log.e(TAG, "Failed to fetch vendors: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<AllVendorsResponse>, t: Throwable) {
//                Log.e(TAG, "API call failure: ${t.message}")
//            }
//        })
//    }

// Fungsi untuk mengambil data units dari API
//    fun getUnits(token: String) {
//        val client = ApiConfig.ApiService().getUnits("Bearer $token")
//
//        client.enqueue(object : Callback<UnitsResponse> {
//            override fun onResponse(
//                call: Call<UnitsResponse>,
//                response: Response<UnitsResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null && !responseBody.error) {
//                        // Set data units ke LiveData
//                        _unitList.value = responseBody.data
//                        Log.e(TAG, "onSuccessUnits: ${response.message()}")
//                    } else {
//                        Log.e(TAG, "Failed to fetch units: ${response.message()}")
//                    }
//                } else {
//                    Log.e(TAG, "Failed to fetch units: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<UnitsResponse>, t: Throwable) {
//                Log.e(TAG, "API call failure: ${t.message}")
//            }
//        })
//    }

//    fun getCategories()

//    fun getCategories(token: String) {
//        val client = ApiConfig
//            .ApiService()
//            .getCategories("Bearer $token")
//
//        client.enqueue(object : Callback<CategoryResponse> {
//            override fun onResponse(
//                call: Call<CategoryResponse>,
//                response: Response<CategoryResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null && !responseBody.error) {
//                        _categoryList.value = responseBody.data
////                            _categoryList.value = response.body()?.token
//                            Log.e(TAG, "onSuccessCategory: ${response.message()}")
//                    }
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
////                mIsLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
////                mSnackBarText.value = Event(t.message.toString())
//            }
//        })
//    }

//fun getCategoryStocks(
//    token: String,
//) = authRepository.getCategoryStocks(token)
//
//fun getVendorStocks(
//    token: String,
//) = authRepository.getVendorStocks(token)