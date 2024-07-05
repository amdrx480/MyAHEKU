package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StocksViewModel(private val authRepository: AuthRepository) : ViewModel() {

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

    // LiveData untuk status ekspor
//    private val _exportStatus = MutableLiveData<ResultResponse<ExcelResponse>>()
//    val exportStatus: LiveData<ResultResponse<ExcelResponse>> get() = _exportStatus


    // Variabel untuk menyimpan status terakhir pilihan kategori dan unit
    private var lastSelectedCategories: List<String> = emptyList()
    private var lastSelectedUnits: List<String> = emptyList()

    private val _categoryNames = MutableLiveData<List<String>>()
    val categoryNamesLiveData: LiveData<List<String>> get() = _categoryNames

    private val _unitNames = MutableLiveData<List<String>>()
    val unitNamesLiveData: LiveData<List<String>> get() = _unitNames

    private val _stocksFlow = MediatorLiveData<PagingData<StocksEntity>>()
    val stocksFlow: LiveData<PagingData<StocksEntity>> get() = _stocksFlow

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

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
                        // Restore last selected units
//                        unitName.value = lastSelectedUnits
                        // Restore last selected units after fetching
                        restoreLastSelectedUnits()
                    }
                    is ResultResponse.Error -> {
                        // Handle error state if needed
                        Log.e("StocksViewModel", "Error loading units: ${result.error}")
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
        isFilterApplied.value =
            !(categoryName.value.isNullOrEmpty() && unitName.value.isNullOrEmpty() && sellingPriceMin.value == null && sellingPriceMax.value == null)
        getStocks()

        // Update last selected categories and units
        lastSelectedCategories = categoryName.value ?: emptyList()
        lastSelectedUnits = unitName.value ?: emptyList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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

}



//    fun exportVisibleStocksToExcel(context: Context) {
//        val visibleStocks = _stocksFlow.value?.let { pagingData ->
//            pagingData.snapshot().items
//        } ?: emptyList()
//
//        val result = ExcelHelper.saveStocksToExcel(context, visibleStocks)
//        // Handle the result if needed
//        when (result) {
//            is ResultResponse.Success -> {
//                Log.d("StocksViewModel", "Exported stocks to Excel: ${result.data}")
//                // Handle success, e.g., show a toast with file path
//            }
//            is ResultResponse.Error -> {
//                Log.e("StocksViewModel", "Failed to export stocks: ${result.message}")
//                // Handle error, e.g., show a toast with error message
//            }
//        }
//    }
// Function to export visible stocks to Excel
//    fun exportVisibleStocksToExcel() {
//        viewModelScope.launch {
//            // Panggil repository untuk mendapatkan data stok yang sesuai
//            val stocks = authRepository.getStocksForExport()
//
//            // Panggil helper untuk menyimpan data ke Excel
//            excelHelper.saveStocksToExcel(stocks)
//        }
//    }

//    // Function to export visible stocks to Excel
//    fun exportVisibleStocksToExcel(page: Int, limit: Int) {
//        // Ambil nilai-nilai terbaru dari LiveData
//        val currentAuthToken = _authToken.value
//        val currentSortValue = currentSort.value
//        val currentOrderValue = currentOrder.value
//        val currentSearchValue = currentQuery.value
//        val currentCategoryValue = categoryName.value
//        val currentUnitValue = unitName.value
//        val currentSellingPriceMin = sellingPriceMin.value
//        val currentSellingPriceMax = sellingPriceMax.value
//
//        // Cek apakah nilai LiveData tidak null
//        if (currentAuthToken != null) {
//            viewModelScope.launch {
//                // Set status loading sebelum memanggil API
//                _exportStatus.postValue(ResultResponse.Loading)
//
//                try {
//                    // Panggil API untuk ekspor stocks ke Excel
//                    val response = authRepository.exportStocksToExcel(
//                        token = currentAuthToken,
//                        page = page,
//                        limit = limit,
//                        sort = currentSortValue,
//                        order = currentOrderValue,
//                        search = currentSearchValue,
//                        categoryName = currentCategoryValue,
//                        unitName = currentUnitValue,
//                        sellingPriceMin = currentSellingPriceMin,
//                        sellingPriceMax = currentSellingPriceMax
//                    )
//
//                    // Handle response
//                    if (response.isSuccessful) {
//                        // Jika sukses, simpan data Excel
//                        _exportStatus.postValue(ResultResponse.Success(response.body()!!))
//                    } else {
//                        // Jika tidak sukses, kirim error
//                        _exportStatus.postValue(ResultResponse.Error("Export failed: ${response.message()}"))
//                    }
//                } catch (e: Exception) {
//                    // Tangani exception jika terjadi kesalahan
//                    _exportStatus.postValue(ResultResponse.Error("Export failed: ${e.message}"))
//                }
//            }
//        } else {
//            // Jika token null, kirim error
//            _exportStatus.postValue(ResultResponse.Error("Authentication token is null"))
//        }
//    }
//
//}

// LiveData untuk status ekspor
//    private val _exportStatus = MutableLiveData<ResultResponse<ExcelResponse>>()
//    val exportStatus: LiveData<ResultResponse<ExcelResponse>> get() = _exportStatus


    // Function to export stocks to Excel using API endpoint
    // Function to export stocks to Excel using API endpoint
//    fun exportStocksToExcel(
//        token: String,
//        page: Int,
//        limit: Int,
//        sort: String? = null,
//        order: String? = null,
//        search: String? = null,
//        categoryName: String? = null,
//        unitName: String? = null,
//        sellingPriceMin: Int? = null,
//        sellingPriceMax: Int? = null,
//        outputFile: File
//    ) {
//        viewModelScope.launch {
//            _exportStatus.value = ResultResponse.Loading
//
//            try {
//                // Call API endpoint to export stocks
//                val response = authRepository.exportStocksToExcel(
//                    token = token,
//                    page = page,
//                    limit = limit,
//                    sort = sort,
//                    order = order,
//                    search = search,
//                    categoryName = categoryName,
//                    unitName = unitName,
//                    sellingPriceMin = sellingPriceMin,
//                    sellingPriceMax = sellingPriceMax,
//                    file = outputFile
//                ).single() // Menggunakan single karena hanya satu respons yang diharapkan
//
//                _exportStatus.value =
//            } catch (e: Exception) {
//                _exportStatus.value = ResultResponse.Error("Export failed: ${e.message}")
//                Log.e("StocksViewModel", "Error exporting stocks", e)
//            }
//        }
//    }
//}

//    private fun restoreLastSelectedCategories() {
//        categoryName.value?.let { currentCategories ->
//            val restoredCategories = currentCategories.filter { lastSelectedCategories.contains(it) }
//            categoryName.value = restoredCategories
//        }
//    }
//
//    private fun restoreLastSelectedUnits() {
//        unitName.value?.let { currentUnits ->
//            val restoredUnits = currentUnits.filter { lastSelectedUnits.contains(it) }
//            unitName.value = restoredUnits
//        }
//    }

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