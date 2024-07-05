package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStocksBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.BottomSheetFilterBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.BottomSheetSortBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.utils.ExcelHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StocksActivity : AppCompatActivity() {

    private val stocksViewModel: StocksViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    private lateinit var adapter: StocksAdapter
    private val selectedCategories = mutableSetOf<String>()
    private val selectedUnits = mutableSetOf<String>()

    private var _binding: ActivityStocksBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStocksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StocksAdapter()

        setupRecyclerView()
        observeStocks()
        setupSwipeToRefresh()
        setupSearch()
        setupSortButton()
        setupFilterButton()

        binding.fabExport.setOnClickListener {
            exportVisibleStocksToExcel()
        }
    }

    private fun setupRecyclerView() {
        binding.rvStocks.layoutManager = LinearLayoutManager(this)
        binding.rvStocks.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { adapter.retry() },
            footer = LoadingStateAdapter { adapter.retry() }
        )

        binding.rvStocks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fabExport.isVisible) {
                    binding.fabExport.hide()
                } else if (dy < 0 && !binding.fabExport.isVisible) {
                    binding.fabExport.show()
                }
            }
        })
    }

    private fun observeStocks() {
        stocksViewModel.stocksFlow.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
                binding.tvInfo.root.isVisible = loadStates.refresh is LoadState.Error
            }
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupSearch() {
        binding.etSearch.apply {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[2].bounds.width())) {
                        // Clear text
                        setText("")
                        // Call performClick to handle accessibility feedback
                        performClick()
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
            }

            doOnTextChanged { text, _, _, _ ->
                val query = text?.toString()?.trim()
                stocksViewModel.searchStocks(query)
            }
        }
    }

    private fun setupSortButton() {
        binding.btnSort.setOnClickListener {
            showSortOptions()
        }
    }

    private fun showSortOptions() {
        val dialogBinding = BottomSheetSortBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)

        val currentSort = stocksViewModel.currentSort.value
        val currentOrder = stocksViewModel.currentOrder.value
        when (currentSort) {
            "stock_name" -> {
                if (currentOrder == "asc") dialogBinding.radioAscName.isChecked = true
                else dialogBinding.radioDescName.isChecked = true
            }
            "selling_price" -> {
                if (currentOrder == "asc") dialogBinding.radioAscPrice.isChecked = true
                else dialogBinding.radioDescPrice.isChecked = true
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            val selectedSortOrder = when (dialogBinding.radioGroupSort.checkedRadioButtonId) {
                R.id.radioAscName -> "stock_name" to "asc"
                R.id.radioDescName -> "stock_name" to "desc"
                R.id.radioAscPrice -> "selling_price" to "asc"
                R.id.radioDescPrice -> "selling_price" to "desc"
                else -> "stock_name" to "asc"
            }

            stocksViewModel.filterAndSortStocks(
                search = binding.etSearch.text.toString().trim(),
                sort = selectedSortOrder.first,
                order = selectedSortOrder.second
            )
            dialog.dismiss()
            updateSortButtonIndicator(true)
        }

        dialog.setOnDismissListener {
            // Update the button indicator based on the current sort state
            val isSorting = stocksViewModel.currentOrder.value != "asc"
            updateSortButtonIndicator(isSorting)
        }
        dialog.show()
    }

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener {
            showFilterOptions()
        }
    }

    private fun showFilterOptions() {
        val bottomSheetBinding = BottomSheetFilterBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Setup category chips
        stocksViewModel.categoryNamesLiveData.observe(this) { categories ->
            bottomSheetBinding.chipGroupCategory.removeAllViews()
            categories.forEach { categoryName ->
                val chip = Chip(this)
                chip.text = categoryName
                chip.isCheckable = true
                chip.isChecked = selectedCategories.contains(categoryName)
                chip.setOnCheckedChangeListener { _, isChecked ->
                    // Handle chip selection logic here
                    if (isChecked) {
                        selectedCategories.add(categoryName)
                    } else {
                        selectedCategories.remove(categoryName)
                    }
                }
                bottomSheetBinding.chipGroupCategory.addView(chip)
            }
        }

        // Setup unit chips
        stocksViewModel.unitNamesLiveData.observe(this) { units ->
            bottomSheetBinding.chipGroupUnit.removeAllViews()
            units.forEach { unitName ->
                val chip = Chip(this)
                chip.text = unitName
                chip.isCheckable = true
                chip.isChecked = selectedUnits.contains(unitName)
                chip.setOnCheckedChangeListener { _, isChecked ->
                    // Handle chip selection logic here
                    if (isChecked) {
                        selectedUnits.add(unitName)
                    } else {
                        selectedUnits.remove(unitName)
                    }
                }
                bottomSheetBinding.chipGroupUnit.addView(chip)
            }
        }

        bottomSheetBinding.btnApplyFilter.setOnClickListener {
            val minPrice = bottomSheetBinding.etMinPrice.text.toString().toIntOrNull()
            val maxPrice = bottomSheetBinding.etMaxPrice.text.toString().toIntOrNull()

            stocksViewModel.filterByCategoryAndUnit(
                selectedCategories.toList(),
                selectedUnits.toList()
            )
            stocksViewModel.setSellingPriceRange(minPrice, maxPrice)
            stocksViewModel.applyFilters()

            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss() // Dismiss the bottom sheet
        }

        // Show the bottom sheet dialog
        bottomSheetDialog.show()
    }

    private fun updateSortButtonIndicator(isSorting: Boolean) {
        // Add logic to update the button indicator
        if (isSorting) {
            binding.btnSort.setImageResource(R.drawable.ic_sort_active) // Change to an active indicator icon
        } else {
            binding.btnSort.setImageResource(R.drawable.ic_sort) // Change to a default indicator icon
        }
    }

    private fun exportVisibleStocksToExcel() {
        val context = this
        val visibleStocks = adapter.snapshot().items

        lifecycleScope.launch {
            val result = ExcelHelper.saveStocksToExcel(context, visibleStocks)
            // Handle the result if needed
            when (result) {
                is ResultResponse.Success -> {
                    Log.d("StocksActivity", "Exported stocks to Excel: ${result.data}")
                    Toast.makeText(context, "Exported to: ${result.data}", Toast.LENGTH_LONG).show()
                }
                is ResultResponse.Error -> {
                    Log.e("StocksActivity", "Failed to export stocks: ${result.error}")
                    Toast.makeText(context, "Failed to export: ${result.error}", Toast.LENGTH_LONG).show()
                }
                is ResultResponse.Loading -> {
                    // You can show a loading indicator if needed
                    Log.d("StocksActivity", "Exporting stocks...")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}



//    private fun setupExportButton() {
//        binding.fabExport.setOnClickListener {
//            val page = 1 // Misalnya, untuk halaman pertama
//            val limit = 100 // Misalnya, batas data per halaman
//
////            stocksViewModel.exportStocksToExcel(page, limit)
//            stocksViewModel.exportVisibleStocksToExcel(this, adapter.snapshot().items)
//        }
//    }

//    private fun observeExportStatus() {
//        stocksViewModel.exportStatus.observe(this) { result ->
//            when (result) {
//                is ResultResponse.Loading -> {
//                    // Handle loading state, misalnya tampilkan progress atau feedback loading
//                    Toast.makeText(this, "Exporting data...", Toast.LENGTH_SHORT).show()
//                }
//                is ResultResponse.Success -> {
//                    // Handle success state, misalnya tampilkan pesan sukses atau notifikasi
//                    Toast.makeText(this, "Export successful!", Toast.LENGTH_SHORT).show()
//                    saveExcelFile(result.data) // Ensure result.data is the ResponseBody
//                }
//                is ResultResponse.Error -> {
//                    // Handle error state, misalnya tampilkan pesan error atau notifikasi
//                    Toast.makeText(this, "Export failed: ${result.error}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

//    private fun saveExcelFile(excelModel: ExcelModel) {
//        val fileName = "stocks_data.xlsx" // Nama file yang ingin Anda berikan
//        val file = File(getExternalFilesDir(null), fileName)
//
//        try {
//            // Tulis respons ke file
//            val inputStream = excelModel.responseBody.byteStream()
//            val outputStream = FileOutputStream(file)
//            inputStream.use { input ->
//                outputStream.use { output ->
//                    input.copyTo(output)
//                }
//            }
//            // File berhasil disimpan
//            Log.d("StocksActivity", "File saved: ${file.absolutePath}")
//            // Sekarang Anda dapat memberitahukan pengguna bahwa file telah disimpan atau memberikan akses untuk membukanya.
//        } catch (e: IOException) {
//            // Tangani kesalahan saat menyimpan file
//            Log.e("StocksActivity", "Error saving file", e)
//        }
//    }

//private fun observeFilterSelections() {
//    stocksViewModel.selectedCategories.observe(this) { categories ->
//        selectedCategories.clear()
//        selectedCategories.addAll(categories)
//    }
//
//    stocksViewModel.selectedUnits.observe(this) { units ->
//        selectedUnits.clear()
//        selectedUnits.addAll(units)
//    }
//}
//
//class StocksActivity : AppCompatActivity() {
//
//    private var _binding: ActivityStocksBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var adapter: StocksAdapter
//
//    private val stocksViewModel: StocksViewModel by viewModels {
//        ViewModelUserFactory.getInstance(this)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        _binding = ActivityStocksBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        adapter = StocksAdapter()
//
//        setupRecyclerView()
//        observeStocks()
//        setupSwipeToRefresh()
//        setupSearch()
//        setupSortButton()
//        setupFilterButton()
//    }
//
//    private fun setupRecyclerView() {
//        binding.rvStocks.layoutManager = LinearLayoutManager(this)
//        binding.rvStocks.adapter = adapter.withLoadStateHeaderAndFooter(
//            header = LoadingStateAdapter { adapter.retry() },
//            footer = LoadingStateAdapter { adapter.retry() }
//        )
//    }
//
//    private fun observeStocks() {
//        stocksViewModel.categoryNamesLiveData.observe(this) { categories ->
//            // Update UI with category names
//        }
//
//        stocksViewModel.unitNamesLiveData.observe(this) { units ->
//            // Update UI with unit names
//        }
//
//        stocksViewModel.stocksFlow.observe(this) { pagingData ->
//            adapter.submitData(lifecycle, pagingData)
//        }
//
//        lifecycleScope.launch {
//            adapter.loadStateFlow.collectLatest { loadStates ->
//                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
//                binding.tvInfo.root.isVisible = loadStates.refresh is LoadState.Error
//            }
//        }
//    }
//
//    private fun setupFilterButton() {
//        binding.btnFilter.setOnClickListener {
//            showFilterOptions()
//        }
//    }
//
//    private fun setupSwipeToRefresh() {
//        binding.swipeRefresh.setOnRefreshListener {
//            adapter.refresh()
//        }
//    }
//
//    private fun setupSearch() {
//        binding.etSearch.apply {
//            setOnTouchListener { _, event ->
//                if (event.action == MotionEvent.ACTION_UP) {
//                    if (event.rawX >= (right - compoundDrawables[2].bounds.width())) {
//                        // Clear text
//                        setText("")
//                        // Call performClick to handle accessibility feedback
//                        performClick()
//                        return@setOnTouchListener true
//                    }
//                }
//                return@setOnTouchListener false
//            }
//
//            doOnTextChanged { text, _, _, _ ->
//                val query = text?.toString()?.trim()
//                searchStocks(query)
//            }
//        }
//    }
//
//    private fun setupSortButton() {
//        binding.btnSort.setOnClickListener {
//            showSortOptions()
//        }
//    }
//
//    private fun showSortOptions() {
//        val dialogBinding = BottomSheetSortBinding.inflate(layoutInflater)
//        val dialog = BottomSheetDialog(this)
//        dialog.setContentView(dialogBinding.root)
//
//        val currentSort = stocksViewModel.currentSort.value
//        val currentOrder = stocksViewModel.currentOrder.value
//        when (currentSort) {
//            "stock_name" -> {
//                if (currentOrder == "asc") dialogBinding.radioAscName.isChecked = true
//                else dialogBinding.radioDescName.isChecked = true
//            }
//            "selling_price" -> {
//                if (currentOrder == "asc") dialogBinding.radioAscPrice.isChecked = true
//                else dialogBinding.radioDescPrice.isChecked = true
//            }
//        }
//
//        dialogBinding.btnCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialogBinding.btnConfirm.setOnClickListener {
//            val selectedSortOrder = when (dialogBinding.radioGroupSort.checkedRadioButtonId) {
//                R.id.radioAscName -> "stock_name" to "asc"
//                R.id.radioDescName -> "stock_name" to "desc"
//                R.id.radioAscPrice -> "selling_price" to "asc"
//                R.id.radioDescPrice -> "selling_price" to "desc"
//                else -> "stock_name" to "asc"
//            }
//
//            stocksViewModel.filterAndSortStocks(
//                search = binding.etSearch.text.toString().trim(),
//                sort = selectedSortOrder.first,
//                order = selectedSortOrder.second
//            )
//            dialog.dismiss()
//            updateSortButtonIndicator(true)
//        }
//
//        dialog.setOnDismissListener {
//            // Update the button indicator based on the current sort state
//            val isSorting = stocksViewModel.currentOrder.value != "asc"
//            updateSortButtonIndicator(isSorting)
//        }
//        dialog.show()
//    }
//
//    private fun showFilterOptions() {
//        val bottomSheetBinding = BottomSheetFilterBinding.inflate(layoutInflater)
//        val bottomSheetDialog = BottomSheetDialog(this)
//        bottomSheetDialog.setContentView(bottomSheetBinding.root)
//
//        val selectedCategories = mutableSetOf<String>()
//        val selectedUnits = mutableSetOf<String>()
//
//        // Setup category chips
//        stocksViewModel.categoryNamesLiveData.observe(this) { categories ->
//            bottomSheetBinding.chipGroupCategory.removeAllViews()
//            categories.forEach { categoryName ->
//                val chip = Chip(this)
//                chip.text = categoryName
//                chip.isCheckable = true
//                chip.setOnCheckedChangeListener { _, isChecked ->
//                    // Handle chip selection logic here
//                    if (isChecked) {
//                        selectedCategories.add(categoryName)
//                    } else {
//                        selectedCategories.remove(categoryName)
//                    }
//                }
//                bottomSheetBinding.chipGroupCategory.addView(chip)
//            }
//        }
//
//        // Setup unit chips
//        stocksViewModel.unitNamesLiveData.observe(this) { units ->
//            bottomSheetBinding.chipGroupUnit.removeAllViews()
//            units.forEach { unitName ->
//                val chip = Chip(this)
//                chip.text = unitName
//                chip.isCheckable = true
//                chip.setOnCheckedChangeListener { _, isChecked ->
//                    // Handle chip selection logic here
//                    if (isChecked) {
//                        selectedUnits.add(unitName)
//                    } else {
//                        selectedUnits.remove(unitName)
//                    }
//                }
//                bottomSheetBinding.chipGroupUnit.addView(chip)
//            }
//        }
//
//        bottomSheetBinding.btnApplyFilter.setOnClickListener {
//            val minPrice = bottomSheetBinding.etMinPrice.text.toString().toIntOrNull()
//            val maxPrice = bottomSheetBinding.etMaxPrice.text.toString().toIntOrNull()
//
//            stocksViewModel.filterByCategoryAndUnit(selectedCategories.toList(), selectedUnits.toList())
//            stocksViewModel.setSellingPriceRange(minPrice, maxPrice)
//            stocksViewModel.applyFilters()
//            bottomSheetDialog.dismiss()
//        }
//
//        bottomSheetBinding.btnCancel.setOnClickListener {
//            bottomSheetDialog.dismiss() // Dismiss the bottom sheet
//        }
//
//        // Show the bottom sheet dialog
//        bottomSheetDialog.show()
//    }
//
//    private fun updateSortButtonIndicator(isSorting: Boolean) {
//        // Add logic to update the button indicator
//        if (isSorting) {
//            binding.btnSort.setImageResource(R.drawable.ic_sort_active) // Change to an active indicator icon
//        } else {
//            binding.btnSort.setImageResource(R.drawable.ic_sort) // Change to a default indicator icon
//        }
//    }
//
//    private fun searchStocks(query: String?) {
//        stocksViewModel.searchStocks(query)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//
//    companion object {
//        const val EXTRA_TOKEN = "extra_token"
//    }
//}

//        stocksViewModel.currentOrder.observe(this) { order ->
//            binding.notificationDot.isVisible = order.isNotEmpty()
//            updateSortButtonIcon(order)
//        }

//    private fun filterAndSortStocks(filter: String, sort: String, order: String) {
//        stocksViewModel.filterAndSortStocks(filter, sort, order)
//    }

//    private fun updateSortButtonIcon(order: String) {
//        val iconRes = when (order) {
//            "asc" -> R.drawable.ic_sort_asc
//            "desc" -> R.drawable.ic_sort_desc
//            else -> R.drawable.ic_sort
//        }
//        binding.btnSort.setImageResource(iconRes)
//    }

//    private fun sortStocks(sort: String, order: String) {
//        lifecycleScope.launch {
//            stocksViewModel.sortStocks(sort, order).collectLatest { pagingData ->
//                adapter.submitData(pagingData)
//            }
//        }
//    }

//    private fun setupSearchFilterSort() {
//        binding.etSearch.doOnTextChanged { text, _, _, _ ->
//            val query = text?.toString()?.trim()
//            searchStocks(query)
//        }

//        binding.btnSearch.setOnClickListener {
//            val filter = binding.etFilter.text.toString().trim()
//            val sort = binding.etSort.text.toString().trim()
//            val order = "asc" // Default order
//            filterAndSortStocks(filter, sort, order)
//        }
//
//        binding.btnSortAsc.setOnClickListener {
//            val filter = binding.etFilter.text.toString().trim()
//            val sort = binding.etSort.text.toString().trim()
//            val order = "asc"
//            filterAndSortStocks(filter, sort, order)
//        }
//
//        binding.btnSortDesc.setOnClickListener {
//            val filter = binding.etFilter.text.toString().trim()
//            val sort = binding.etSort.text.toString().trim()
//            val order = "desc"
//            filterAndSortStocks(filter, sort, order)
//        }
//    }

//        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                val query = binding.etSearch.text.toString().trim()
//                searchStocks(query)
//                true
//            } else {
//                false
//            }
//        }

//        binding.btnSearch.setOnClickListener {
//            val filter = binding.etFilter.text.toString().trim()
//            val sort = binding.etSort.text.toString().trim()
//            filterAndSortStocks(filter, sort)
//        }
//
//        binding.btnSortAsc.setOnClickListener {
//            filterAndSortStocks(binding.etFilter.text.toString().trim(), "selling_price ASC")
//        }
//
//        binding.btnSortDesc.setOnClickListener {
//            filterAndSortStocks(binding.etFilter.text.toString().trim(), "selling_price DESC")
//        }
//}

//    private fun setupSearchFilterSort() {
//        binding.etSearch.doOnTextChanged { text, _, _, _ ->
//            val query = text?.toString()?.trim()
//            searchStocks(query)
//        }
//
//        binding.btnSearch.setOnClickListener {
//            val filter = binding.etFilter.text.toString().trim()
//            val sort = binding.etSort.text.toString().trim()
//            filterAndSortStocks(filter, sort)
//        }
//    }

//    private lateinit var etFilter: EditText
//    private lateinit var etSort: EditText
//    private lateinit var etSearch: EditText
//    private lateinit var btnSearch: Button

//    private fun setupAdapter() {
//        adapter = StocksAdapter()
//
//        binding?.rvStocks?.adapter = adapter.withLoadStateHeaderAndFooter(
//            footer = LoadingStateAdapter { adapter.retry() },
//            header = LoadingStateAdapter { adapter.retry() }
//        )
//        binding?.rvStocks?.layoutManager = LinearLayoutManager(this)
//        binding?.rvStocks?.setHasFixedSize(true)
//
//        lifecycleScope.launchWhenCreated {
//            adapter.loadStateFlow.collectLatest { loadStates ->
//                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
//                if (adapter.itemCount < 1) {
//                    binding?.tvInfo?.root?.visibility = View.VISIBLE
//                } else {
//                    binding?.tvInfo?.root?.visibility = View.GONE
//                }
//            }
//        }
//    }

//    private fun setupAdapter() {
//        adapter = StocksAdapter()
//        binding?.rvStocks?.adapter = adapter.withLoadStateHeaderAndFooter(
//            footer = LoadingStateAdapter { adapter.retry() },
//            header = LoadingStateAdapter { adapter.retry() }
//        )
//        binding?.rvStocks?.layoutManager = LinearLayoutManager(this)
//        binding?.rvStocks?.setHasFixedSize(true)
//
//        lifecycleScope.launchWhenCreated {
//            adapter.loadStateFlow.collect { loadState ->
//                binding?.swipeRefresh?.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
//            }
//        }
//
//        lifecycleScope.launch {
//            adapter.loadStateFlow.collectLatest { loadStates ->
//                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
//                if (adapter.itemCount < 1) {
//                    binding?.tvInfo?.root?.visibility = View.VISIBLE
//                } else {
//                    binding?.tvInfo?.root?.visibility = View.GONE
//                }
//            }
//        }
//
//        // Listen to filter, sort, and query changes
//        binding?.btnSearch?.setOnClickListener {
//            val filter = binding?.etFilter?.text.toString()
//            val sort = binding?.etSort?.text.toString()
//            val query = binding?.etSearch?.text.toString()
//            stocksViewModel.getStocks(
//                filter = filter,
//                sort = sort,
//                query = query
//            ).observe(this@StocksActivity) {
//                adapter.submitData(lifecycle, it)
//            }
//        }
//
//        stocksViewModel.getStocks().observe(this) {
//            adapter.submitData(lifecycle, it)
//        }

// Listen to filter, sort, and query changes
//        binding?.apply {
//            btnSearch.setOnClickListener {
//                val filter = etFilter.text.toString()
//                val sort = etSort.text.toString()
//                val query = etSearch.text.toString()
//                stocksViewModel.getStocks(
//                    filter = filter,
//                    sort = sort,
//                    query = query
//                ).observe(this@StocksActivity) {
//                    adapter.submitData(lifecycle, it)
//                }
//            }
//        }

//        lifecycleScope.launch {
//            stocksViewModel.getStocks().collect {
//                adapter.submitData(lifecycle, it)
//            }
//        }
//    }

//    private fun setupViews() {
//        etFilter = findViewById(R.id.etFilter)
//        etSort = findViewById(R.id.etSort)
//        etSearch = findViewById(R.id.etSearch)
//        btnSearch = findViewById(R.id.btnSearch)
//
//        btnSearch.setOnClickListener {
//            performSearch()
//        }
//    }

//    private fun performSearch() {
//        val filter = etFilter.text.toString()
//        val sort = etSort.text.toString()
//        val query = etSearch.text.toString()
//
//        stocksViewModel.getStocks(filter, sort, query).observe(this) { pagingData ->
//            adapter.submitData(lifecycle, pagingData)
//        }
//    }

//private fun setupAdapter() {
//    adapter = StocksAdapter()
//    binding?.rvStocks?.adapter = adapter.withLoadStateHeaderAndFooter(
//        footer = LoadingStateAdapter { adapter.retry() },
//        header = LoadingStateAdapter { adapter.retry() }
//    )
//    binding?.rvStocks?.layoutManager = LinearLayoutManager(this)
//    binding?.rvStocks?.setHasFixedSize(true)
//
//    lifecycleScope.launchWhenCreated {
//        adapter.loadStateFlow.collect {
//            binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
//        }
//    }
//    lifecycleScope.launch {
//        adapter.loadStateFlow.collectLatest { loadStates ->
//            binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
//        }
//        if (adapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
//        else binding?.tvInfo?.root?.visibility = View.GONE
//    }
//
//    stocksViewModel.getStocks().observe(this) {
//        adapter.submitData(lifecycle, it)
//    }
//}
//    private lateinit var user: UserModel
//        private lateinit var user: StocksEntity