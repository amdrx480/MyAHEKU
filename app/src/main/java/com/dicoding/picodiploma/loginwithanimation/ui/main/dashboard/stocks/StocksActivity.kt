package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStocksBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.BottomSheetFilterBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.BottomSheetSortBinding
//import com.dicoding.picodiploma.loginwithanimation.databinding.DialogSortOptionsBinding

import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StocksActivity : AppCompatActivity() {

    private val stocksViewModel: StocksViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    private lateinit var adapter: StocksAdapter

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
//        showFilterOptions()
    }

    private fun setupRecyclerView() {
        binding.rvStocks.layoutManager = LinearLayoutManager(this)
        binding.rvStocks.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { adapter.retry() },
            footer = LoadingStateAdapter { adapter.retry() }
        )
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
                searchStocks(query)
            }
        }

        binding.btnSort.setOnClickListener {
            showSortOptions()
        }

        binding.btnFilter.setOnClickListener {
            showFilterOptions()
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
                chip.setOnCheckedChangeListener { _, isChecked ->
                    // Handle chip selection logic here
                    if (isChecked) {
                        // Apply filter by category
                        val selectedCategories = getSelectedChips(bottomSheetBinding.chipGroupCategory)
                        stocksViewModel.filterByCategoryAndUnit(selectedCategories, getSelectedChips(bottomSheetBinding.chipGroupUnit))
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
                chip.setOnCheckedChangeListener { _, isChecked ->
                    // Handle chip selection logic here
                    if (isChecked) {
                        // Apply filter by unit
                        val selectedUnits = getSelectedChips(bottomSheetBinding.chipGroupUnit)
                        stocksViewModel.filterByCategoryAndUnit(getSelectedChips(bottomSheetBinding.chipGroupCategory), selectedUnits)
                    }
                }
                bottomSheetBinding.chipGroupUnit.addView(chip)
            }
        }

        bottomSheetBinding.btnApplyFilter.setOnClickListener {
            val minPrice = bottomSheetBinding.etMinPrice.text.toString().toIntOrNull()
            val maxPrice = bottomSheetBinding.etMaxPrice.text.toString().toIntOrNull()
            stocksViewModel.setSellingPriceRange(minPrice, maxPrice)

            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss() // Dismiss the bottom sheet
        }

        // Show the bottom sheet dialog
        bottomSheetDialog.show()
    }

    private fun getSelectedChips(chipGroup: ChipGroup): List<String> {
        val selectedChips = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedChips.add(chip.text.toString())
            }
        }
        return selectedChips
    }


    private fun updateSortButtonIndicator(isSorting: Boolean) {
        // Add logic to update the button indicator
        if (isSorting) {
            binding.btnSort.setImageResource(R.drawable.ic_sort_active) // Change to an active indicator icon
        } else {
            binding.btnSort.setImageResource(R.drawable.ic_sort) // Change to a default indicator icon
        }
    }

    private fun searchStocks(query: String?) {
        stocksViewModel.searchStocks(query)
    }

//    private fun showFilterOptions() {
//        val bottomSheetBinding = BottomSheetFilterBinding.inflate(layoutInflater)
//        val bottomSheetDialog = BottomSheetDialog(this)
//        bottomSheetDialog.setContentView(bottomSheetBinding.root)
//
//        // Setup category chips
//        stocksViewModel.categoryNames.observe(this) { categories ->
//            bottomSheetBinding.chipGroupCategory.removeAllViews()
//            categories.forEach { categoryName ->
//                val chip = Chip(this)
//                chip.text = categoryName
//                chip.isCheckable = true
//                chip.setOnCheckedChangeListener { _, isChecked ->
//                    // Handle chip selection logic here
//                    if (isChecked) {
//                        // Apply filter by category
//                        stocksViewModel.filterByCategoryAndUnit(categoryName, stocksViewModel.unitName.value)
//                    } else {
//                        // Handle deselection if needed
//                        stocksViewModel.filterByCategoryAndUnit(null, stocksViewModel.unitName.value)
//                    }
//                }
//                bottomSheetBinding.chipGroupCategory.addView(chip)
//            }
//        }
//
//        // Setup unit chips
//        stocksViewModel.unitNames.observe(this) { units ->
//            bottomSheetBinding.chipGroupUnit.removeAllViews()
//            units.forEach { unitName ->
//                val chip = Chip(this)
//                chip.text = unitName
//                chip.isCheckable = true
//                chip.setOnCheckedChangeListener { _, isChecked ->
//                    // Handle chip selection logic here
//                    if (isChecked) {
//                        // Apply filter by unit
//                        stocksViewModel.filterByCategoryAndUnit(stocksViewModel.categoryName.value, unitName)
//                    } else {
//                        // Handle deselection if needed
//                        stocksViewModel.filterByCategoryAndUnit(stocksViewModel.categoryName.value, null)
//                    }
//                }
//                bottomSheetBinding.chipGroupUnit.addView(chip)
//            }
//        }
//
//        bottomSheetBinding.btnApplyFilter.setOnClickListener {
//            // Handle apply filter button click
//            val minPrice = bottomSheetBinding.etMinPrice.text.toString().toDoubleOrNull() ?: 0.0
//            val maxPrice =
//                bottomSheetBinding.etMaxPrice.text.toString().toDoubleOrNull() ?: Double.MAX_VALUE
//
//            // Apply filter based on price range
////            stocksViewModel.filterByPriceRange(minPrice, maxPrice)
//
//            bottomSheetDialog.dismiss() // Dismiss the bottom sheet
//        }
//
//        bottomSheetBinding.btnCancel.setOnClickListener {
//            bottomSheetDialog.dismiss() // Dismiss the bottom sheet
//        }
//
//        // Show the bottom sheet dialog
//        bottomSheetDialog.show()
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}

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