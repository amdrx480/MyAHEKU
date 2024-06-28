package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StocksViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val currentFilter = MutableLiveData("")
    val currentSort = MutableLiveData("id") // Default sort
    private val currentQuery = MutableLiveData("")
    val currentOrder = MutableLiveData("asc") // Default order
    // MutableLiveData untuk menyimpan daftar category name dan unit name yang dipilih
    private val categoryName = MutableLiveData<List<String>>(emptyList())
    private val unitName = MutableLiveData<List<String>>(emptyList())
    private val sellingPriceMin = MutableLiveData<Int?>(null)
    private val sellingPriceMax = MutableLiveData<Int?>(null)
    val isFilterApplied = MutableLiveData(false)


    private val _categoryNames = MutableLiveData<List<String>>()
    val categoryNamesLiveData: LiveData<List<String>> get() = _categoryNames

    private val _unitNames = MutableLiveData<List<String>>()
    val unitNamesLiveData: LiveData<List<String>> get() = _unitNames

    private val _stocksFlow = MediatorLiveData<PagingData<StocksEntity>>()
    val stocksFlow: LiveData<PagingData<StocksEntity>> get() = _stocksFlow

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

    fun searchStocks(query: String?) {
        currentQuery.value = query ?: ""
    }

    fun filterAndSortStocks(search: String, sort: String, order: String) {
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
        getStocks()
    }

    private fun getStocks() {
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

            authRepository.getPagingStocks(
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

        _stocksFlow.addSource(source) { data ->
            _stocksFlow.value = data
        }
    }

//    private fun getStocks() {
//        val source = combine(
//            currentSort.asFlow(),
//            currentQuery.asFlow(),
//            currentOrder.asFlow(),
//            categoryName.asFlow(),
//            unitName.asFlow(),
//            sellingPriceMin.asFlow(),
//            sellingPriceMax.asFlow()
//        ) { sort, search, order, category, unit, sellingPriceMin, sellingPriceMax ->
//            authRepository.getPagingStocks(
//                token = _authToken.value ?: "",
//                sort = sort,
//                search = search,
//                order = order,
//                categoryName = category,
//                unitName = unit,
//                sellingPriceMin = sellingPriceMin,
//                sellingPriceMax = sellingPriceMax
//            )
//        }.flatMapLatest { it }
//            .cachedIn(viewModelScope)
//            .asLiveData()
//
//        _stocksFlow.addSource(source) { data ->
//            _stocksFlow.value = data
//        }
//    }

    fun loadCategoriesAndUnits(token: String) {
        viewModelScope.launch {
            authRepository.fetchCategories(token).observeForever { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Handle loading state if needed
                    }
                    is ResultResponse.Success -> {
                        _categoryNames.value = result.data.map { it.category_Name }
                    }
                    is ResultResponse.Error -> {
                        // Handle error state if needed
                        Log.e("StocksViewModel", "Error loading categories: ${result.error}")
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
                    }
                    is ResultResponse.Error -> {
                        // Handle error state if needed
                        Log.e("StocksViewModel", "Error loading units: ${result.error}")
                    }
                }
            }
        }
    }

    init {
        loadAuthToken()
        authToken.observeForever { token ->
            token?.let {
                loadCategoriesAndUnits(it)
                getStocks()
            }
        }
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e ->
                    Log.e("StocksViewModel", "Error loading auth token", e)
                }
                .collect { token ->
                    _authToken.postValue(token)
                }
        }
    }
}


//    fun filterByCategoryAndUnit(category: String?, unit: String?) {
//        categoryName.value = category
//        unitName.value = unit
//    }
//
//    fun filterByPriceRange(min: Double?, max: Double?) {
//        minPrice.value = min
//        maxPrice.value = max
//    }

//        val source = combine(
//            currentFilter.asFlow(),
//            currentSort.asFlow(),
//            currentQuery.asFlow(),
//            currentOrder.asFlow(),
//            minPrice.asFlow(),
//            maxPrice.asFlow(),
//            categoryName.asFlow(),
//            unitName.asFlow()
//        ) { filter, sort, query, order, min, max, category, unit ->
//            authRepository.getPagingStocks(
//                token = _authToken.value ?: "", // Use actual token value here
//                filter = filter,
//                sort = sort,
//                query = query,
//                order = order,
//                categoryName = category,
//                unitName = unit,
//                minSellingPrice = min,
//                maxSellingPrice = max,
//            )
//        }.flatMapLatest { it }
//            .cachedIn(viewModelScope)
//            .asLiveData()
//
//        _stocksFlow.addSource(source) { data ->
//            _stocksFlow.value = data
//        }

//class StocksViewModel(private val authRepository: AuthRepository) : ViewModel() {
//
//    private val currentFilter = MutableLiveData("")
//    val currentSort = MutableLiveData("stock_name")
//    private val currentQuery = MutableLiveData("")
//    val currentOrder = MutableLiveData("asc") // Default order
//    val categoryName = MutableLiveData("category_name")
//    val unitName = MutableLiveData("unit_name")
//
//    private val _categoryNames = MutableLiveData<List<String>>()
//    val categoryName: LiveData<List<String>> get() = _categoryNames
//
//    private val _unitNames = MutableLiveData<List<String>>()
//    val unitName: LiveData<List<String>> get() = _unitNames
//
//    private val _stocksFlow = MediatorLiveData<PagingData<StocksEntity>>()
//    val stocksFlow: LiveData<PagingData<StocksEntity>> get() = _stocksFlow
//
//    // LiveData to observe auth token (example)
//    private val _authToken = MutableLiveData<String?>()
//    val authToken: LiveData<String?> = _authToken
//
//    fun searchStocks(query: String?) {
//        currentQuery.value = query ?: ""
//    }
//
//    fun filterAndSortStocks(filter: String, sort: String, order: String) {
//        currentFilter.value = filter
//        currentSort.value = sort
//        currentOrder.value = order
//    }
//
//    fun filterByCategoryAndUnit(category: String?, unit: String?) {
//        categoryName.value = category
//        unitName.value = unit
//    }
//
////    fun filterByPriceRange(min: Double?, max: Double?) {
////        minPrice.value = min
////        maxPrice.value = max
////    }
//
//    private fun getStocks() {
//        val source = combine(
//            currentFilter.asFlow(),
//            currentSort.asFlow(),
//            currentQuery.asFlow(),
//            currentOrder.asFlow(),
//            categoryName.asFlow(),
//        ) { filter, sort, query, order, category ->
//            authRepository.getPagingStocks(
//                token = "",
//                filter = filter,
//                sort = sort,
//                query = query,
//                order = order,
//                categoryName = category,
//            )
//        }.flatMapLatest { it }
//            .cachedIn(viewModelScope)
//            .asLiveData()
//
//        _stocksFlow.addSource(source) { data ->
//            _stocksFlow.value = data
//        }
//
//    }
//
//
//    fun loadCategoriesAndUnits(token: String) {
//        viewModelScope.launch {
//            authRepository.fetchCategories(token).observeForever { result ->
//                when (result) {
//                    is ResultResponse.Loading -> {
//                        // Handle loading state if needed
//                    }
//                    is ResultResponse.Success -> {
//                        _categoryNames.value = result.data.map { it.category_Name }
//                    }
//                    is ResultResponse.Error -> {
//                        // Handle error state if needed
//                        Log.e("StocksViewModel", "Error loading categories: ${result.error}")
//                    }
//                }
//            }
//
//            authRepository.fetchUnits(token).observeForever { result ->
//                when (result) {
//                    is ResultResponse.Loading -> {
//                        // Handle loading state if needed
//                    }
//                    is ResultResponse.Success -> {
//                        _unitNames.value = result.data.map { it.unit_name }
//                    }
//                    is ResultResponse.Error -> {
//                        // Handle error state if needed
//                        Log.e("StocksViewModel", "Error loading units: ${result.error}")
//                    }
//                }
//            }
//        }
//    }
//
//    // Example of loading auth token (if needed)
//    init {
//        loadAuthToken()
//        getStocks()
//        authToken.observeForever { token ->
//            token?.let {
//                loadCategoriesAndUnits(it)
//            }
//        }
//    }
//
//    private fun loadAuthToken() {
//        viewModelScope.launch {
//            authRepository.getAuthToken()
//                .catch { e ->
//                    // Handle error loading auth token
//                    Log.e("StocksViewModel", "Error loading auth token", e)
//                }
//                .collect { token ->
//                    // Post the loaded token to live data
//                    _authToken.postValue(token)
//                }
//        }
//    }
//}

//        val source = combine(
//            currentFilter.asFlow(),
//            currentSort.asFlow(),
//            currentQuery.asFlow(),
//            currentOrder.asFlow()
//        ) { filter, sort, query, order ->
//            authRepository.getPagingStocks(
//                token = "",
//                filter = filter,
//                sort = sort,
//                query = query,
//                order = order
//            )
//        }.flatMapLatest { it }
//            .cachedIn(viewModelScope)
//            .asLiveData()
//
//        _stocksFlow.addSource(source) { data ->
//            _stocksFlow.value = data
//        }

//class StocksViewModel(private val authRepository: AuthRepository) : ViewModel() {
//
//    private val currentFilter = MutableLiveData("")
//    private val currentSort = MutableLiveData("stock_name")
//    private val currentQuery = MutableLiveData("")
//    private val _currentOrder = MutableLiveData("asc") // Default order
//    val currentOrder: LiveData<String> = _currentOrder
//
//    private val _stocksFlow = MediatorLiveData<PagingData<StocksEntity>>()
//    val stocksFlow: LiveData<PagingData<StocksEntity>> get() = _stocksFlow
//
//    // LiveData to observe auth token (example)
//    private val _authToken = MutableLiveData<String?>()
//    val authToken: LiveData<String?> = _authToken
//
//    fun searchStocks(query: String?) {
//        currentQuery.value = query ?: ""
//    }
//
//    fun filterAndSortStocks(filter: String, sort: String, order: String) {
//        currentFilter.value = filter
//        currentSort.value = sort
//        _currentOrder.value = order
//    }
//
//    //    // Example of loading auth token (if needed)
//    init {
//        loadAuthToken()
//
//        val source = combine(
//            currentFilter.asFlow(),
//            currentSort.asFlow(),
//            currentQuery.asFlow(),
//            _currentOrder.asFlow()
//        ) { filter, sort, query, order ->
//            authRepository.getPagingStocks(
//                token = "",
//                filter = filter,
//                sort = sort,
//                query = query,
//                order = order
//            )
//        }.flatMapLatest { it }
//            .cachedIn(viewModelScope)
//            .asLiveData()
//
//        _stocksFlow.addSource(source) { data ->
//            _stocksFlow.value = data
//        }
//    }
//
//    private fun loadAuthToken() {
//        viewModelScope.launch {
//            authRepository.getAuthToken()
//                .catch { e ->
//                    // Handle error loading auth token
//                    Log.e("StocksViewModel", "Error loading auth token", e)
//                }
//                .collect { token ->
//                    // Post the loaded token to live data
//                    _authToken.postValue(token)
//                }
//        }
//    }
//}


//    // Example of loading auth token (if needed)
//    init {
//        loadAuthToken()
//
//        viewModelScope.launch {
//            combine(
//                currentFilter.asFlow(),
//                currentSort.asFlow(),
//                currentQuery.asFlow(),
//                _currentOrder.asFlow()
//            ) { filter, sort, query, order ->
//                authRepository.getPagingStocks(
//                    token = "",
//                    filter = filter,
//                    sort = sort,
//                    query = query,
//                    order = order
//                )
//            }.flatMapLatest { it }
//                .cachedIn(viewModelScope)
//                .collect()
//        }
//    }
//
//    private fun loadAuthToken() {
//        viewModelScope.launch {
//            authRepository.getAuthToken()
//                .catch { e ->
//                    // Handle error loading auth token
//                    // For example, log the error
//                    // Log.e("StocksViewModel", "Error loading auth token", e)
//                }
//                .collect { token ->
//                    // Post the loaded token to live data
//                    _authToken.postValue(token)
//                }
//        }
//    }
//}

//class StocksViewModel(private val authRepository: AuthRepository) : ViewModel() {
//
//    private val currentFilter = MutableLiveData("")
//    private val currentSort = MutableLiveData("stock_name")
//    private val currentQuery = MutableLiveData("")
//    private val _currentOrder = MutableLiveData("asc") // Default order
//    val currentOrder: LiveData<String> = _currentOrder
//
//
//    private val _stocksFlow = MediatorLiveData<PagingData<StocksEntity>>()
//    val stocksFlow: LiveData<PagingData<StocksEntity>> get() = _stocksFlow
//
//    // LiveData to observe auth token (example)
//    private val _authToken = MutableLiveData<String?>()
//    val authToken: LiveData<String?> = _authToken
//
//    // Function to set search query
//    fun searchStocks(query: String?) {
//        currentQuery.value = query ?: ""
//    }
//
//    // Function to set sort order
//    fun sortStocks(sort: String, order: String) {
//        currentSort.value = sort
//        _currentOrder .value = order
//    }
//
//    fun filterAndSortStocks(filter: String, sort: String, order: String) {
//        currentFilter.value = filter
//        currentSort.value = sort
//        _currentOrder .value = order
//    }
//
//    // Example of loading auth token (if needed)
//    init {
//        loadAuthToken()
//
//        val source = combine(
//            currentFilter.asFlow(),
//            currentSort.asFlow(),
//            currentQuery.asFlow(),
//            _currentOrder.asFlow()
//        ) { filter, sort, query, order ->
//            authRepository.getPagingStocks(
//                token = "",
//                filter = filter,
//                sort = sort,
//                query = query,
//                order = order
//            )
//        }.flatMapLatest { it }
//            .cachedIn(viewModelScope)
//            .asLiveData()
//
//        _stocksFlow.addSource(source) { data ->
//            _stocksFlow.value = data
//        }
//    }
//
//    private fun loadAuthToken() {
//        viewModelScope.launch {
//            authRepository.getAuthToken()
//                .catch { e ->
//                    // Handle error loading auth token
//                    Log.e("StocksViewModel", "Error loading auth token", e)
//                }
//                .collect { token ->
//                    // Post the loaded token to live data
//                    _authToken.postValue(token)
//                }
//        }
//    }
//}

//    fun filterAndSortStocks(filter: String, sort: String) {
//        currentFilter.value = filter
//        currentSort.value = sort
//    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val stocksFlow = combine(
//        currentFilter,
//        currentSort,
//        currentQuery,
//        currentOrder
//    ) { filter, sort, query, order ->
//        authRepository.getPagingStocks(
//            token = "",
//            filter = filter,
//            sort = sort,
//            query = query,
//            order = order
//        )
//    }.flatMapLatest { it }
//        .cachedIn(viewModelScope)
//        .asLiveData()

//    fun searchStocks(query: String?) {
//        currentQuery.value = query ?: ""
//    }
//v
//class StocksViewModel(private val authRepository: AuthRepository) : ViewModel() {
//
//    private val _authToken = MutableLiveData<String?>()
//    val authToken: LiveData<String?> = _authToken
//
////    private val _stocksFlow = MutableStateFlow<PagingData<StocksEntity>?>(null)
////    val stocksFlow: StateFlow<PagingData<StocksEntity>?> = _stocksFlow
//
//    init {
//        loadAuthToken()
//    }
//
//    private fun loadAuthToken() {
//        viewModelScope.launch {
//            authRepository.getAuthToken()
//                .catch { e -> Log.e("StocksViewModel", "Error loading auth token", e) }
//                .collect { token ->
//                    _authToken.postValue(token)
//                    // _authToken.value = token as? String
//
//                    // Optionally handle null or non-String tokens here
//                    // _authToken.value = token?.toString() ?: ""
//                }
//        }
//    }
//
//    fun getStocks(
//        filter: String? = null,
//        sort: String? = null,
//        query: String? = null
//    ): LiveData<PagingData<StocksEntity>> {
//        return authToken.switchMap { token ->
//            authRepository.getPagingStocks(
//                token = token ?: "",
//                filter = filter,
//                sort = sort,
//                query = query
//            ).asLiveData()
////            ).cachedIn(viewModelScope).asLiveData()
//        }
//    }
//
//    private val currentFilter = MutableStateFlow("")
//    private val currentSort = MutableStateFlow("stock_name ASC")
//    private val currentQuery = MutableStateFlow("")
//
//    val stocksFlow = combine(
//        currentFilter,
//        currentSort,
//        currentQuery
//    ) { filter, sort, query ->
//        authRepository.getPagingStocks(filter, sort, query)
//    }.flatMapLatest { it }
//        .cachedIn(viewModelScope)
//        .asLiveData()
//
//    fun searchStocks(query: String?) {
//        currentQuery.value = query ?: ""
//    }
//
//    fun filterAndSortStocks(filter: String, sort: String) {
//        currentFilter.value = filter
//        currentSort.value = sort
//    }

//    fun getStocks(
//        filter: String? = null,
//        sort: String? = null,
//        query: String? = null
//    ): Flow<PagingData<StocksEntity>> {
//        val token = authToken.value ?: ""
//        return authRepository.getPagingStocks(
//            token = token,
//            filter = filter,
//            sort = sort,
//            query = query
//        ).cachedIn(viewModelScope)
//    }
//}

//fun getStocks(): LiveData<PagingData<StocksEntity>> {
//    return authToken.switchMap { token ->
//        authRepository.getPagingStocks(token ?: "").cachedIn(viewModelScope).asLiveData()
//    }
//}


//    override fun onCleared() {
//        super.onCleared()
//        // Clean up resources if needed, e.g., canceling coroutines, disposing subscriptions
//    }

//fun getStocks(token: String): LiveData<PagingData<StocksEntity>> {
//    return authRepository.pagingStories(token).cachedIn(viewModelScope).asLiveData()
//}