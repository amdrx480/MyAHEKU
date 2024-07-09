package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itemtransactions

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ItemTransactionsViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val currentSort = MutableLiveData("id") // Default sort
    private val currentQuery = MutableLiveData("")
    val currentOrder = MutableLiveData("asc") // Default order

    // MutableLiveData untuk menyimpan daftar category name dan unit name yang dipilih
    private val customerName = MutableLiveData<List<String>>(emptyList())
    private val categoryName = MutableLiveData<List<String>>(emptyList())
    private val unitName = MutableLiveData<List<String>>(emptyList())
    private val subTotalMin = MutableLiveData<Int?>(null)
    private val subTotalMax = MutableLiveData<Int?>(null)
    val isFilterApplied = MutableLiveData(false)

    // Variabel untuk menyimpan status terakhir pilihan kategori dan unit
    private var lastSelectedCategories: List<String> = emptyList()
    private var lastSelectedUnits: List<String> = emptyList()

    private val _customerNames = MutableLiveData<List<String>>()
    val customerNamesLiveData: LiveData<List<String>> get() = _customerNames

    private val _categoryNames = MutableLiveData<List<String>>()
    val categoryNamesLiveData: LiveData<List<String>> get() = _categoryNames

    private val _unitNames = MutableLiveData<List<String>>()
    val unitNamesLiveData: LiveData<List<String>> get() = _unitNames

    private val _itemTransactionsFlow = MediatorLiveData<PagingData<ItemTransactionsEntity>>()
    val itemTransactionsFlow: LiveData<PagingData<ItemTransactionsEntity>> get() = _itemTransactionsFlow

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

    init {
        loadAuthToken()
        authToken.observeForever { token ->
            token?.let {
                loadCategoriesAndUnits(it)
                getItemTransactions()
            }
        }
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e ->
                    Log.e("TransactionsViewModel", "Error loading auth token", e)
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
                        Log.e("ItemTransactionsViewModel", "Error loading categories: ${result.error}")
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
                        Log.e("ItemTransactionsViewModel", "Error loading units: ${result.error}")
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

    fun searchItemTransactions(query: String?) {
        currentQuery.value = query ?: ""
    }

    fun filterAndSortTransactions(search: String, sort: String, order: String) {
        currentQuery.value = search
        currentSort.value = sort
        currentOrder.value = order
    }

    fun filterByCategoryAndUnit(categories: List<String>, units: List<String>) {
        categoryName.value = categories
        unitName.value = units
    }

    fun setSellingPriceRange(minPrice: Int?, maxPrice: Int?) {
        subTotalMin.value = minPrice
        subTotalMax.value = maxPrice
    }

    fun applyFilters() {
        isFilterApplied.value = !(categoryName.value.isNullOrEmpty() && unitName.value.isNullOrEmpty() && subTotalMin.value == null && subTotalMax.value == null)
        getItemTransactions()

        // Update last selected categories and units
        lastSelectedCategories = categoryName.value ?: emptyList()
        lastSelectedUnits = unitName.value ?: emptyList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getItemTransactions() {
        val source = combine(
            currentSort.asFlow(),
            currentQuery.asFlow(),
            currentOrder.asFlow(),
//            customerName.asFlow(),
            categoryName.asFlow(),
            unitName.asFlow(),
            subTotalMin.asFlow(),
            subTotalMax.asFlow()
        ) { params: Array<Any?> ->
            val sort = params[0] as? String
            val search = params[1] as? String
            val order = params[2] as? String
            val category = params[3] as? List<String>
            val unit = params[4] as? List<String>
            val subTotalMin = params[5] as? Int
            val subTotalMax = params[6] as? Int

            authRepository.getPagingItemTransactions(
                token = _authToken.value ?: "",
                sort = sort,
                search = search,
                order = order,
                categoryName = category,
                unitName = unit,
                subTotalMin = subTotalMin,
                subTotalMax = subTotalMax
            )
        }.flatMapLatest { it }
            .cachedIn(viewModelScope)
            .asLiveData()

        _itemTransactionsFlow.addSource(source) { data ->
            _itemTransactionsFlow.value = data
        }
    }
}

//    fun getItemTransactions(token: String): LiveData<PagingData<ItemTransactionsEntity>> {
//        return authRepository.pagingItemTransactions(token).cachedIn(viewModelScope).asLiveData()
//    }