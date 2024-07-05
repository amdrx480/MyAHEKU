package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itempurchases

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ItemPurchasesViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val currentSort = MutableLiveData("id") // Default sort
    private val currentQuery = MutableLiveData("")
    val currentOrder = MutableLiveData("asc") // Default order

    // MutableLiveData untuk menyimpan daftar category name dan unit name yang dipilih
    private val categoryName = MutableLiveData<List<String>>(emptyList())
    private val unitName = MutableLiveData<List<String>>(emptyList())
    private val sellingPriceMin = MutableLiveData<Int?>(null)
    private val sellingPriceMax = MutableLiveData<Int?>(null)
    val isFilterApplied = MutableLiveData(false)

    // LiveData untuk mengamati perubahan pada daftar category name dan unit name yang dipilih
//    val selectedCategories: LiveData<List<String>> get() = categoryName
//    val selectedUnits: LiveData<List<String>> get() = unitName

    // Variabel untuk menyimpan status terakhir pilihan kategori dan unit
    private var lastSelectedCategories: List<String> = emptyList()
    private var lastSelectedUnits: List<String> = emptyList()

    private val _categoryNames = MutableLiveData<List<String>>()
    val categoryNamesLiveData: LiveData<List<String>> get() = _categoryNames

    private val _unitNames = MutableLiveData<List<String>>()
    val unitNamesLiveData: LiveData<List<String>> get() = _unitNames

    private val _purchasesFlow = MediatorLiveData<PagingData<PurchasesEntity>>()
    val purchasesFlow: LiveData<PagingData<PurchasesEntity>> get() = _purchasesFlow

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

    init {
        loadAuthToken()
        authToken.observeForever { token ->
            token?.let {
                loadCategoriesAndUnits(it)
                getPurchases()
            }
        }
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e ->
                    Log.e("PurchasesViewModel", "Error loading auth token", e)
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
                        _categoryNames.value = result.data.map { it.category_Name }
                        // Restore last selected categories
//                        categoryName.value = lastSelectedCategories
                        // Restore last selected categories after fetching
                        restoreLastSelectedCategories()
                    }
                    is ResultResponse.Error -> {
                        // Handle error state if needed
                        Log.e("ItemPurchasesViewModel", "Error loading categories: ${result.error}")
                    }
                }
            }

            authRepository.fetchUnits(token).observeForever { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Handle loading state if needed
                    }
                    is ResultResponse.Success -> {
                        _unitNames.value = result.data.map { it.unit_name }
                        // Restore last selected units
//                        unitName.value = lastSelectedUnits
                        // Restore last selected units after fetching
                        restoreLastSelectedUnits()
                    }
                    is ResultResponse.Error -> {
                        // Handle error state if needed
                        Log.e("ItemPurchasesViewModel", "Error loading units: ${result.error}")
                    }
                }
            }
        }
    }

    private fun restoreLastSelectedCategories() {
        categoryName.value = lastSelectedCategories
    }

    private fun restoreLastSelectedUnits() {
        unitName.value = lastSelectedUnits
    }

    fun searchPurchases(query: String?) {
        currentQuery.value = query ?: ""
    }

    fun filterAndSortPurchases(search: String, sort: String, order: String) {
        currentQuery.value = search
        currentSort.value = sort
        currentOrder.value = order
    }

    fun filterByCategoryAndUnit(categories: List<String>, units: List<String>) {
        categoryName.value = categories
        unitName.value = units
    }

    fun setSellingPriceRange(minPrice: Int?, maxPrice: Int?) {
        sellingPriceMin.value = minPrice
        sellingPriceMax.value = maxPrice
    }

    fun applyFilters() {
        isFilterApplied.value = !(categoryName.value.isNullOrEmpty() && unitName.value.isNullOrEmpty() && sellingPriceMin.value == null && sellingPriceMax.value == null)
        getPurchases()

        // Update last selected categories and units
        lastSelectedCategories = categoryName.value ?: emptyList()
        lastSelectedUnits = unitName.value ?: emptyList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getPurchases() {
        val source = combine(
            currentSort.asFlow(),
            currentQuery.asFlow(),
            currentOrder.asFlow(),
            categoryName.asFlow(),
            unitName.asFlow(),
            sellingPriceMin.asFlow(),
            sellingPriceMax.asFlow()
        ) { params: Array<Any?> ->
            val sort = params[0] as? String
            val search = params[1] as? String
            val order = params[2] as? String
            val category = params[3] as? List<String>
            val unit = params[4] as? List<String>
            val sellingPriceMin = params[5] as? Int
            val sellingPriceMax = params[6] as? Int

            authRepository.getPagingPurchases(
                token = _authToken.value ?: "",
                sort = sort,
                search = search,
                order = order,
                categoryName = category,
                unitName = unit,
                sellingPriceMin = sellingPriceMin,
                sellingPriceMax = sellingPriceMax
            )
        }.flatMapLatest { it }
            .cachedIn(viewModelScope)
            .asLiveData()

        _purchasesFlow.addSource(source) { data ->
            _purchasesFlow.value = data
        }
    }
}
//    fun getPurchases(token: String): LiveData<PagingData<PurchasesEntity>> {
//        return repository.pagingPurchases(token).cachedIn(viewModelScope).asLiveData()
//    }