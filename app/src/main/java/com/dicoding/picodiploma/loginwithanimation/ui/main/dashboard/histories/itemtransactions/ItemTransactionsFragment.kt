package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itemtransactions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.BottomSheetFilterBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.BottomSheetSortBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentItemTransactionsBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.LoadingStateAdapter
import com.dicoding.picodiploma.loginwithanimation.utils.ExcelHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ItemTransactionsFragment : Fragment() {

    private var _binding: FragmentItemTransactionsBinding? = null
    private val binding get() = _binding

    private val itemTransactionsViewModel: ItemTransactionsViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }

    private lateinit var itemTransactionsAdapter: ItemTransactionsAdapter
    private val selectedCategories = mutableSetOf<String>()
    private val selectedUnits = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemTransactionsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemTransactionsAdapter = ItemTransactionsAdapter()

        setupRecyclerView()
        observeItemTransactions()
        setupSwipeToRefresh()
        setupSearch()
        setupSortButton()
        setupFilterButton()

        binding?.fabExport?.setOnClickListener {
            exportVisibleItemTransactionsToExcel()
        }
    }

    private fun setupRecyclerView() {
        binding?.rvItemTransactions?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvItemTransactions?.adapter = itemTransactionsAdapter.withLoadStateHeaderAndFooter(
            footer = LoadingStateAdapter { itemTransactionsAdapter.retry() },
            header = LoadingStateAdapter { itemTransactionsAdapter.retry() }
        )

        binding?.rvItemTransactions?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding!!.fabExport.isVisible) {
                    binding!!.fabExport.hide()
                } else if (dy < 0 && !binding!!.fabExport.isVisible) {
                    binding!!.fabExport.show()

                    binding?.fabExport?.setOnClickListener {
                        exportVisibleItemTransactionsToExcel()
                    }
                }
            }
        })
    }

    private fun observeItemTransactions() {
        itemTransactionsViewModel.itemTransactionsFlow.observe(this) { pagingData ->
            itemTransactionsAdapter.submitData(lifecycle, pagingData)
        }

        lifecycleScope.launch {
            itemTransactionsAdapter.loadStateFlow.collectLatest { loadStates ->
                binding?.swipeRefresh?.isRefreshing = loadStates.refresh is LoadState.Loading
                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
        }
    }

    private fun setupSwipeToRefresh() {
        binding?.swipeRefresh?.setOnRefreshListener {
            itemTransactionsAdapter.refresh()
        }
    }

    private fun setupSearch() {
        binding?.etSearch?.apply {
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
                itemTransactionsViewModel.searchItemTransactions(query)
            }
        }
    }

    private fun setupSortButton() {
        binding?.btnSort?.setOnClickListener {
            showSortOptions()
        }
    }

    private fun showSortOptions() {
        val dialogBinding = BottomSheetSortBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val currentSort = itemTransactionsViewModel.currentSort.value
        val currentOrder = itemTransactionsViewModel.currentOrder.value
        when (currentSort) {
            "stock_name" -> {
                if (currentOrder == "asc") dialogBinding.radioAscName.isChecked = true
                else dialogBinding.radioDescName.isChecked = true
            }
            "sub_total" -> {
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
                R.id.radioAscPrice -> "sub_total" to "asc"
                R.id.radioDescPrice -> "sub_total" to "desc"
                else -> "stock_name" to "asc"
            }

            itemTransactionsViewModel.filterAndSortTransactions(
                search = binding?.etSearch?.text.toString().trim(),
                sort = selectedSortOrder.first,
                order = selectedSortOrder.second
            )
            dialog.dismiss()
            updateSortButtonIndicator(true)
        }

        dialog.setOnDismissListener {
            // Update the button indicator based on the current sort state
            val isSorting = itemTransactionsViewModel.currentOrder.value != "asc"
            updateSortButtonIndicator(isSorting)
        }
        dialog.show()
    }

    private fun setupFilterButton() {
        binding?.btnFilter?.setOnClickListener {
            showFilterOptions()
        }
    }

    private fun showFilterOptions() {
        val bottomSheetBinding = BottomSheetFilterBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Setup category chips
        itemTransactionsViewModel.categoryNamesLiveData.observe(this) { categories ->
            bottomSheetBinding.chipGroupCategory.removeAllViews()
            categories.forEach { categoryName ->
                val chip = Chip(requireContext())
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
        itemTransactionsViewModel.unitNamesLiveData.observe(this) { units ->
            bottomSheetBinding.chipGroupUnit.removeAllViews()
            units.forEach { unitName ->
                val chip = Chip(requireContext())
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

            itemTransactionsViewModel.filterByCategoryAndUnit(
                selectedCategories.toList(),
                selectedUnits.toList()
            )
            itemTransactionsViewModel.setSellingPriceRange(minPrice, maxPrice)
            itemTransactionsViewModel.applyFilters()

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
            binding?.btnSort?.setImageResource(R.drawable.ic_sort_active) // Change to an active indicator icon
        } else {
            binding?.btnSort?.setImageResource(R.drawable.ic_sort) // Change to a default indicator icon
        }
    }

    private fun exportVisibleItemTransactionsToExcel() {
        val context = requireContext()
        val visibleItemStocks = itemTransactionsAdapter.snapshot().items

        lifecycleScope.launch {
            val result = ExcelHelper.saveItemTransactionsToExcel(context, visibleItemStocks)
            // Handle the result if needed
            when (result) {
                is ResultResponse.Success -> {
                    Log.d(
                        "ItemTransactionsFragment",
                        "Exported ItemTransactions to Excel: ${result.data}"
                    )
                    Toast.makeText(context, "Exported to: ${result.data}", Toast.LENGTH_LONG).show()
                }
                is ResultResponse.Error -> {
                    Log.e(
                        "ItemTransactionsFragment",
                        "Failed to export ItemTransactions: ${result.error}"
                    )
                    Toast.makeText(context, "Failed to export: ${result.error}", Toast.LENGTH_LONG)
                        .show()
                }
                is ResultResponse.Loading -> {
                    // You can show a loading indicator if needed
                    Log.d("ItemTransactionsFragment", "Exporting ItemTransactions...")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


//
//            binding?.rvItemTransactions?.layoutManager =
//                LinearLayoutManager(requireContext())
//            binding?.rvItemTransactions?.setHasFixedSize(true)
//        }
//
//        lifecycleScope.launchWhenCreated {
//            itemTransactionsAdapter.loadStateFlow.collect {
//                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
//            }
//        }
//
//        lifecycleScope.launch {
//            itemTransactionsAdapter.loadStateFlow.collectLatest { loadStates ->
//                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
//            }
//            if (itemTransactionsAdapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
//            else binding?.tvInfo?.root?.visibility = View.VISIBLE
//        }
//
//        itemTransactionsViewModel.getItemTransactions(token).observe(viewLifecycleOwner) {
//            itemTransactionsAdapter.submitData(lifecycle, it)

//private fun setupAdapter() {
//    itemTransactionsAdapter = ItemTransactionsAdapter()
//    binding?.rvItemTransactions?.apply {
//        adapter = itemTransactionsAdapter.withLoadStateHeaderAndFooter(
//            footer = LoadingStateAdapter { itemTransactionsAdapter.retry() },
//            header = LoadingStateAdapter { itemTransactionsAdapter.retry() }
//        )
//        binding?.rvItemTransactions?.layoutManager =
//            LinearLayoutManager(requireContext())
//        binding?.rvItemTransactions?.setHasFixedSize(true)
//    }
//
//    lifecycleScope.launchWhenCreated {
//        itemTransactionsAdapter.loadStateFlow.collect {
//            binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
//        }
//    }
//
//    lifecycleScope.launch {
//        itemTransactionsAdapter.loadStateFlow.collectLatest { loadStates ->
//            binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
//        }
//        if (itemTransactionsAdapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
//        else binding?.tvInfo?.root?.visibility = View.VISIBLE
//    }
//
//    itemTransactionsViewModel.getItemTransactions(token).observe(viewLifecycleOwner) {
//        itemTransactionsAdapter.submitData(lifecycle, it)
//    }
//}

//    private fun setupObservers() {
//        itemPurchasesViewModel.getPurchases(user.token).observe(viewLifecycleOwner) {
//            lifecycleScope.launch {
//                itemTransactionsAdapter.submitData(it)
//            }
//        }
//    }


//    companion object {
//        private const val ARG_TOKEN = "extra_token"
//
//        //        fun newInstance(user: UserModel): ItemPurchasesFragment {
////            val fragment = ItemPurchasesFragment()
////            val bundle = Bundle().apply {
////                putParcelable(ARG_TOKEN, user)
////            }
////            fragment.arguments = bundle
////            return fragment
////        }
////        fun newInstance(token: String): ItemTransactionsFragment {
////            val fragment = ItemTransactionsFragment()
////            val bundle = Bundle().apply {
////                putString(ARG_TOKEN, token)
////            }
////            fragment.arguments = bundle
////            return fragment
////        }
//    }
//
//fun newInstance(token: String): ItemPurchasesFragment {
//    val fragment = ItemPurchasesFragment()
//    val bundle = Bundle().apply {
//        putString(ItemPurchasesFragment.ARG_TOKEN, token)
//    }
//    fragment.arguments = bundle
//    return fragment
//}