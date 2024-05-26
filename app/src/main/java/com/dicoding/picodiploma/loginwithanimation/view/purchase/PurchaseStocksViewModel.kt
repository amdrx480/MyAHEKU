package com.dicoding.picodiploma.loginwithanimation.view.purchase

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository
import com.dicoding.picodiploma.loginwithanimation.service.data.category.AllCategoryResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.category.ListCategoryItem
import com.dicoding.picodiploma.loginwithanimation.service.data.units.AllUnitsResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.units.ListUnitsItem
import com.dicoding.picodiploma.loginwithanimation.service.data.vendors.AllVendorsResponse
import com.dicoding.picodiploma.loginwithanimation.service.data.vendors.ListVendorsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseStocksViewModel(private val repository: AppRepository): ViewModel() {
    private val _categoryList = MutableLiveData<List<ListCategoryItem>>()
    val categoryList: LiveData<List<ListCategoryItem>> = _categoryList

// LiveData untuk menyimpan kategori yang dipilih
    private val _selectedCategory = MutableLiveData<ListCategoryItem?>()
    val selectedCategory: LiveData<ListCategoryItem?> = _selectedCategory

    // LiveData untuk menyimpan daftar vendors
    private val _vendorList = MutableLiveData<List<ListVendorsItem>>()
    val vendorList: LiveData<List<ListVendorsItem>> = _vendorList

    // LiveData untuk menyimpan vendor dan unit yang dipilih
    private val _selectedVendor = MutableLiveData<ListVendorsItem?>()
    val selectedVendor: LiveData<ListVendorsItem?> = _selectedVendor

    // LiveData untuk menyimpan daftar units
    private val _unitList = MutableLiveData<List<ListUnitsItem>>()
    val unitList: LiveData<List<ListUnitsItem>> = _unitList

    private val _selectedUnit = MutableLiveData<ListUnitsItem?>()
    val selectedUnit: LiveData<ListUnitsItem?> = _selectedUnit

    // Fungsi untuk mengambil data vendors dari API
    fun getVendors(token: String) {
        val client = ApiConfig.ApiService()
            .getVendors("Bearer $token")

        client.enqueue(object : Callback<AllVendorsResponse> {
            override fun onResponse(
                call: Call<AllVendorsResponse>,
                response: Response<AllVendorsResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        // Set data vendors ke LiveData
                        _vendorList.value = responseBody.token
                        Log.e(TAG, "onSuccessVendors: ${response.message()}")
                    } else {
                        Log.e(TAG, "Failed to fetch vendors: ${response.message()}")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch vendors: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AllVendorsResponse>, t: Throwable) {
                Log.e(TAG, "API call failure: ${t.message}")
            }
        })
    }

    // Fungsi untuk mengambil data units dari API
    fun getUnits(token: String) {
        val client = ApiConfig.ApiService().getUnits("Bearer $token")

        client.enqueue(object : Callback<AllUnitsResponse> {
            override fun onResponse(
                call: Call<AllUnitsResponse>,
                response: Response<AllUnitsResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        // Set data units ke LiveData
                        _unitList.value = responseBody.token
                        Log.e(TAG, "onSuccessUnits: ${response.message()}")
                    } else {
                        Log.e(TAG, "Failed to fetch units: ${response.message()}")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch units: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AllUnitsResponse>, t: Throwable) {
                Log.e(TAG, "API call failure: ${t.message}")
            }
        })
    }

    fun getCategories(token: String) {
        val client = ApiConfig
            .ApiService()
            .getCategories("Bearer $token")

        client.enqueue(object : Callback<AllCategoryResponse> {
            override fun onResponse(
                call: Call<AllCategoryResponse>,
                response: Response<AllCategoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _categoryList.value = responseBody.token
                            _categoryList.value = response.body()?.token
                            Log.e(TAG, "onSuccessCategory: ${response.message()}")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AllCategoryResponse>, t: Throwable) {
//                mIsLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
//                mSnackBarText.value = Event(t.message.toString())
            }
        })
    }

    // Fungsi untuk menyimpan kategori yang dipilih
    fun setSelectedCategory(category: ListCategoryItem?) {
        _selectedCategory.value = category
    }


    // Metode untuk menyimpan vendor yang dipilih
    fun setSelectedVendor(vendor: ListVendorsItem?) {
        _selectedVendor.value = vendor
    }

    // Metode untuk menyimpan unit yang dipilih
    fun setSelectedUnit(unit: ListUnitsItem?) {
        _selectedUnit.value = unit
    }

    fun uploadPurchaseStocks(
        token: String,
        purchaseRequest: PurchaseRequest
    ) = repository.postPurchaseStocks(token, purchaseRequest)

    companion object {
        private const val TAG = "ViewModel"
    }
}

//fun getCategoryStocks(
//    token: String,
//) = repository.getCategoryStocks(token)
//
//fun getVendorStocks(
//    token: String,
//) = repository.getVendorStocks(token)