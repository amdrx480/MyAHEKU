package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.datamaster

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsRequest
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentDataMasterBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.PopupAddVendorBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory

class DataMasterFragment : Fragment() {

    private var _binding: FragmentDataMasterBinding? = null
    private val binding get() = _binding!!

    private val dataMasterViewModel: DataMasterViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }

    private lateinit var dataMasterNestedAdapter: DataMasterNestedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDataMasterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefreshLayout()
        setupObservers()
    }

    private fun setupRecyclerView() {
        dataMasterNestedAdapter = DataMasterNestedAdapter()
        dataMasterNestedAdapter.setOnItemClickListener { item, position ->
            when (item.title) {
                "Vendors" -> showAddVendorPopup(position)
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Unknown item clicked: ${item.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.recyclerViewNested.adapter = dataMasterNestedAdapter
        binding.recyclerViewNested.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        binding.swipeRefreshLayout.isRefreshing = true
        dataMasterViewModel.authToken.value?.let { token ->
            dataMasterViewModel.fetchVendors(token)
        }
    }

    private fun showAddVendorPopup(position: Int) {
        val popupBinding = PopupAddVendorBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(popupBinding.root)
            .setTitle("Add Vendor")
            .setPositiveButton("Save") { dialog, _ ->
                val vendorName = popupBinding.etVendorName.text.toString().trim()
                val vendorAddress = popupBinding.etVendorAddress.text.toString().trim()
                val vendorEmail = popupBinding.etVendorEmail.text.toString().trim()
                val vendorPhone = popupBinding.etVendorPhone.text.toString().trim()

                if (vendorName.isNotEmpty() && vendorAddress.isNotEmpty() && vendorEmail.isNotEmpty() && vendorPhone.isNotEmpty()) {
                    val vendorsRequest =
                        VendorsRequest(vendorName, vendorAddress, vendorEmail, vendorPhone)
                    saveVendor(position, vendorsRequest)
                    dialog.dismiss()
                    refreshData() // Refresh data after saving
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Mohon lengkapi semua kolom",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        dialogBuilder.show()
    }

    private fun saveVendor(position: Int, vendorsRequest: VendorsRequest) {
        dataMasterViewModel.authToken.value?.let { token ->
            dataMasterViewModel.postVendors(token, vendorsRequest)
                .observe(viewLifecycleOwner, Observer { result ->
                    when (result) {
                        is ResultResponse.Loading -> {
                            // Handle loading state if needed
                        }
                        is ResultResponse.Success -> {
                            Log.d(TAG, "Vendor added successfully")
                            refreshData() // Refresh data after saving
                        }
                        is ResultResponse.Error -> {
                            Log.e(TAG, "Error adding vendor: ${result.error}")
                            binding.swipeRefreshLayout.isRefreshing =
                                false // Hide refreshing indicator
                        }
                    }
                })
        }
    }

    private fun updateRecyclerView(data: List<DataMasterItem>) {
        dataMasterNestedAdapter.setData(data)
    }

    private fun setupObservers() {

        dataMasterViewModel.customerNames.observe(viewLifecycleOwner, Observer { vendors ->
            binding.swipeRefreshLayout.isRefreshing = false
            vendors?.let {
                val dataMasterItems = listOf(
                    DataMasterItem("Customers", it, isExpanded = true), // Maintain the expanded state
                    DataMasterItem(
                        "Vendors",
                        dataMasterViewModel.vendors.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem(
                        "Categories",
                        dataMasterViewModel.categoryNames.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem(
                        "Units",
                        dataMasterViewModel.unitNames.value ?: emptyList(),
                        isExpanded = true
                    )
                )
                updateRecyclerView(dataMasterItems)
                toggleInfoVisibility(it.isEmpty())
            }
        })

        dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
            binding.swipeRefreshLayout.isRefreshing = false
            vendors?.let {
                val dataMasterItems = listOf(
                    DataMasterItem(
                        "Customers",
                        dataMasterViewModel.customerNames.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem("Vendors", it, isExpanded = true), // Maintain the expanded state
                    DataMasterItem(
                        "Categories",
                        dataMasterViewModel.categoryNames.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem(
                        "Units",
                        dataMasterViewModel.unitNames.value ?: emptyList(),
                        isExpanded = true
                    )
                )
                updateRecyclerView(dataMasterItems)
                toggleInfoVisibility(it.isEmpty())
            }
        })
        dataMasterViewModel.categoryNames.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                val dataMasterItems = listOf(
                    DataMasterItem(
                        "Customers",
                        dataMasterViewModel.customerNames.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem(
                        "Vendors",
                        dataMasterViewModel.vendors.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem("Categories", it, isExpanded = true),
                    DataMasterItem(
                        "Units",
                        dataMasterViewModel.unitNames.value ?: emptyList(),
                        isExpanded = true
                    )
                )
                updateRecyclerView(dataMasterItems)
            }
        })
        dataMasterViewModel.unitNames.observe(viewLifecycleOwner, Observer { units ->
            units?.let {
                val dataMasterItems = listOf(
                    DataMasterItem(
                        "Customers",
                        dataMasterViewModel.customerNames.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem(
                        "Vendors",
                        dataMasterViewModel.vendors.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem(
                        "Categories",
                        dataMasterViewModel.categoryNames.value ?: emptyList(),
                        isExpanded = true
                    ),
                    DataMasterItem("Units", it, isExpanded = true)
                )
                updateRecyclerView(dataMasterItems)
            }
        })
    }

    private fun toggleInfoVisibility(showInfo: Boolean) {
        binding.tvInfo.root.visibility = if (showInfo) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "DataMasterFragment"
    }
}

//class DataMasterFragment : Fragment() {
//
//    private var _binding: FragmentDataMasterBinding? = null
//    private val binding get() = _binding!!
//
//    private val dataMasterViewModel: DataMasterViewModel by viewModels {
//        ViewModelUserFactory.getInstance(requireContext())
//    }
//
//    private lateinit var dataMasterNestedAdapter: DataMasterNestedAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentDataMasterBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        setupSwipeRefreshLayout()
//        setupObservers()
//    }
//
//    private fun setupRecyclerView() {
//        dataMasterNestedAdapter = DataMasterNestedAdapter()
//        dataMasterNestedAdapter.setOnItemClickListener { item, position ->
//            when (item.title) {
//                "Vendors" -> showAddVendorPopup(position)
//                else -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Unknown item clicked: ${item.title}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//        binding.recyclerViewNested.adapter = dataMasterNestedAdapter
//        binding.recyclerViewNested.layoutManager = LinearLayoutManager(context)
//    }
//
//    private fun setupSwipeRefreshLayout() {
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            refreshData()
//        }
//    }
//
//    private fun refreshData() {
//        binding.swipeRefreshLayout.isRefreshing = true
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.fetchVendors(token)
//        }
//    }
//
//    private fun showAddVendorPopup(position: Int) {
//        val popupBinding = PopupAddVendorBinding.inflate(layoutInflater)
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//            .setView(popupBinding.root)
//            .setTitle("Add Vendor")
//            .setPositiveButton("Save") { dialog, _ ->
//                val vendorName = popupBinding.etVendorName.text.toString().trim()
//                val vendorAddress = popupBinding.etVendorAddress.text.toString().trim()
//                val vendorEmail = popupBinding.etVendorEmail.text.toString().trim()
//                val vendorPhone = popupBinding.etVendorPhone.text.toString().trim()
//
//                if (vendorName.isNotEmpty() && vendorAddress.isNotEmpty() && vendorEmail.isNotEmpty() && vendorPhone.isNotEmpty()) {
//                    val vendorsRequest = VendorsRequest(vendorName, vendorAddress, vendorEmail, vendorPhone)
//                    saveVendor(position, vendorsRequest)
//                    dialog.dismiss()
//                    refreshData() // Refresh data after saving
//
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Mohon lengkapi semua kolom",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .create()
//
//        dialogBuilder.show()
//    }
//
//    private fun saveVendor(position: Int, vendorsRequest: VendorsRequest) {
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.postVendors(token, vendorsRequest)
//                .observe(viewLifecycleOwner, Observer { result ->
//                    when (result) {
//                        is ResultResponse.Loading -> {
//                            // Handle loading state if needed
//                        }
//                        is ResultResponse.Success -> {
//                            Log.d(TAG, "Vendor added successfully")
//                            refreshData() // Refresh data after saving
//                        }
//                        is ResultResponse.Error -> {
//                            Log.e(TAG, "Error adding vendor: ${result.error}")
//                            binding.swipeRefreshLayout.isRefreshing = false // Hide refreshing indicator
//                        }
//                    }
//                })
//        }
//    }
//
//    private fun updateRecyclerView(data: List<Any>) {
//        val dataMasterItems = listOf(
//            DataMasterItem("Vendors", dataMasterViewModel.vendors.value ?: emptyList<Any>()),
//            DataMasterItem("Categories", dataMasterViewModel.categoryNames.value ?: emptyList<Any>()),
//            DataMasterItem("Units", dataMasterViewModel.unitNames.value ?: emptyList<Any>())
//        )
//        dataMasterNestedAdapter.setData(dataMasterItems)
//    }
//
//    private fun setupObservers() {
//        dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
//            binding.swipeRefreshLayout.isRefreshing = false
//            vendors?.let {
//                updateRecyclerView(it)
//                toggleInfoVisibility(it.isEmpty())
//            }
//        })
//        dataMasterViewModel.categoryNames.observe(viewLifecycleOwner, Observer { categories ->
//            categories?.let { updateRecyclerView(it) }
//        })
//        dataMasterViewModel.unitNames.observe(viewLifecycleOwner, Observer { units ->
//            units?.let { updateRecyclerView(it) }
//        })
//    }
//
//    private fun toggleInfoVisibility(showInfo: Boolean) {
//        binding.tvInfo.root.visibility = if (showInfo) View.VISIBLE else View.GONE
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        private const val TAG = "DataMasterFragment"
//    }
//}


//class DataMasterFragment : Fragment() {
//
//    private var _binding: FragmentDataMasterBinding? = null
//    private val binding get() = _binding!!
//
//    private val dataMasterViewModel: DataMasterViewModel by viewModels {
//        ViewModelUserFactory.getInstance(requireContext())
//    }
//
//    private lateinit var dataMasterNestedAdapter: DataMasterNestedAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentDataMasterBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        dataMasterNestedAdapter = DataMasterNestedAdapter()
//
//        dataMasterNestedAdapter.setOnItemClickListener { item, position ->
//            when (item.title) {
//                "Vendors" -> showAddVendorPopup(position)
//                else -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Unknown item clicked: ${item.title}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//        binding.recyclerViewNested.adapter = dataMasterNestedAdapter
//        binding.recyclerViewNested.layoutManager = LinearLayoutManager(context)
//
//        setupSwipeRefreshLayout()
//        setupObservers()
//    }
//
//    private fun setupSwipeRefreshLayout() {
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            // Refresh data here
//            refreshData()
//        }
//    }
//
//    private fun refreshData() {
//        // Fetch data again
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.fetchVendors(token)
//        }
//    }
//
//    private fun showAddVendorPopup(position: Int) {
//        val popupBinding = PopupAddVendorBinding.inflate(layoutInflater)
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//            .setView(popupBinding.root)
//            .setTitle("Add Vendor")
//            .setPositiveButton("Save") { dialog, _ ->
//                val vendorName = popupBinding.etVendorName.text.toString().trim()
//                val vendorAddress = popupBinding.etVendorAddress.text.toString().trim()
//                val vendorEmail = popupBinding.etVendorEmail.text.toString().trim()
//                val vendorPhone = popupBinding.etVendorPhone.text.toString().trim()
//
//                if (vendorName.isNotEmpty() && vendorAddress.isNotEmpty() && vendorEmail.isNotEmpty() && vendorPhone.isNotEmpty()) {
//                    val vendorsRequest =
//                        VendorsRequest(vendorName, vendorAddress, vendorEmail, vendorPhone)
//                    saveVendor(position, vendorsRequest)
//                    dialog.dismiss()
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Mohon lengkapi semua kolom",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .create()
//
//        dialogBuilder.show()
//    }
//
//    private fun saveVendor(position: Int, vendorsRequest: VendorsRequest) {
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.postVendors(token, vendorsRequest)
//                .observe(viewLifecycleOwner, Observer { result ->
//                    when (result) {
//                        is ResultResponse.Loading -> {
//                            // Handle loading state if needed
//                        }
//                        is ResultResponse.Success -> {
//                            Log.d(TAG, "Vendor added successfully")
//                            // No need to call fetchVendors here, as it's observed in setupObservers
//                            binding.swipeRefreshLayout.isRefreshing = false // Hide refreshing indicator
//                        }
//                        is ResultResponse.Error -> {
//                            Log.e(TAG, "Error adding vendor: ${result.error}")
//                            binding.swipeRefreshLayout.isRefreshing = false // Hide refreshing indicator
//                        }
//                    }
//                })
//        }
//    }
//
//    private fun updateRecyclerView(data: List<Any>) {
//        val dataMasterItems = listOf(
//            DataMasterItem("Vendors", dataMasterViewModel.vendors.value ?: emptyList<Any>()),
//            DataMasterItem(
//                "Categories",
//                dataMasterViewModel.categoryNames.value ?: emptyList<Any>()
//            ),
//            DataMasterItem("Units", dataMasterViewModel.unitNames.value ?: emptyList<Any>())
//        )
//        dataMasterNestedAdapter.setData(dataMasterItems)
//    }
//
//    private fun setupObservers() {
//        dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
//            binding.swipeRefreshLayout.isRefreshing = false
//            vendors?.let {
//                updateRecyclerView(it)
//                toggleInfoVisibility(it.isEmpty())
//            }
////            vendors?.let { updateRecyclerView() }
//        })
//        dataMasterViewModel.categoryNames.observe(viewLifecycleOwner, Observer { categories ->
//            categories?.let { updateRecyclerView(it) }
//        })
//        dataMasterViewModel.unitNames.observe(viewLifecycleOwner, Observer { units ->
//            units?.let { updateRecyclerView(it) }
//        })
//    }
//
//    private fun toggleInfoVisibility(showInfo: Boolean) {
//        binding.tvInfo.root.visibility = if (showInfo) View.VISIBLE else View.GONE
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        private const val TAG = "DataMasterFragment"
//    }
//}

//class DataMasterFragment : Fragment() {
//
//    private var _binding: FragmentDataMasterBinding? = null
//    private val binding get() = _binding!!
//
//    private val dataMasterViewModel: DataMasterViewModel by viewModels {
//        ViewModelUserFactory.getInstance(requireContext())
//    }
//
//    private lateinit var dataMasterNestedAdapter: DataMasterNestedAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentDataMasterBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        dataMasterNestedAdapter = DataMasterNestedAdapter()
//
//        dataMasterNestedAdapter.setOnItemClickListener { item, position ->
//            when (item.title) {
//                "Vendors" -> showAddVendorPopup(position)
//                else -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Unknown item clicked: ${item.title}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//        binding.recyclerViewNested.adapter = dataMasterNestedAdapter
//        binding.recyclerViewNested.layoutManager = LinearLayoutManager(context)
//
//        setupObservers()
//    }
//
//    private fun showAddVendorPopup(position: Int) {
//        val popupBinding = PopupAddVendorBinding.inflate(layoutInflater)
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//            .setView(popupBinding.root)
//            .setTitle("Add Vendor")
//            .setPositiveButton("Save") { dialog, _ ->
//                val vendorName = popupBinding.etVendorName.text.toString().trim()
//                val vendorAddress = popupBinding.etVendorAddress.text.toString().trim()
//                val vendorEmail = popupBinding.etVendorEmail.text.toString().trim()
//                val vendorPhone = popupBinding.etVendorPhone.text.toString().trim()
//
//                if (vendorName.isNotEmpty() && vendorAddress.isNotEmpty() && vendorEmail.isNotEmpty() && vendorPhone.isNotEmpty()) {
//                    val vendorsRequest =
//                        VendorsRequest(vendorName, vendorAddress, vendorEmail, vendorPhone)
//                    saveVendor(position, vendorsRequest)
//                    dialog.dismiss()
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Mohon lengkapi semua kolom",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .create()
//
//        dialogBuilder.show()
//    }
//
//    private fun saveVendor(position: Int, vendorsRequest: VendorsRequest) {
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.postVendors(token, vendorsRequest)
//                .observe(viewLifecycleOwner, Observer { result ->
//                    when (result) {
//                        is ResultResponse.Loading -> {
//                            // Handle loading state if needed
//                        }
//                        is ResultResponse.Success -> {
//                            Log.d(TAG, "Vendor added successfully")
//                            // Setelah menambahkan vendor, panggil fetchVendors untuk memperbarui data vendor
//                            dataMasterViewModel.fetchVendors(token)
//                            updateRecyclerView()
//                        }
//                        is ResultResponse.Error -> {
//                            Log.e(TAG, "Error adding vendor: ${result.error}")
//                        }
//                    }
//                })
//        }
//    }
//
//    private fun updateRecyclerView() {
//        val dataMasterItems = listOf(
//            DataMasterItem("Vendors", dataMasterViewModel.vendors.value ?: emptyList<Any>()),
//            DataMasterItem(
//                "Categories",
//                dataMasterViewModel.categoryNames.value ?: emptyList<Any>()
//            ),
//            DataMasterItem("Units", dataMasterViewModel.unitNames.value ?: emptyList<Any>())
//        )
//        dataMasterNestedAdapter.setData(dataMasterItems)
//    }
//
//    private fun setupObservers() {
//        dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
////            if (vendors != null) updateRecyclerView()
//            updateRecyclerView()
//        })
//        dataMasterViewModel.categoryNames.observe(viewLifecycleOwner, Observer { categories ->
//            if (categories != null) updateRecyclerView()
//        })
//        dataMasterViewModel.unitNames.observe(viewLifecycleOwner, Observer { units ->
//            if (units != null) updateRecyclerView()
//        })
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        private const val TAG = "DataMasterFragment"
//    }
//}


//    private fun saveVendor(position: Int, vendorsRequest: VendorsRequest) {
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.postVendors(token, vendorsRequest)
//                .observe(viewLifecycleOwner, Observer { result ->
//                    when (result) {
//                        is ResultResponse.Loading -> {
//                            // Handle loading state if needed
//                        }
//                        is ResultResponse.Success -> {
//                            Log.d(TAG, "Vendor added successfully")
//                            // Setelah menambahkan vendor baru, panggil kembali fetchVendors untuk memperbarui data
//                            dataMasterViewModel.fetchVendors(token).observe(viewLifecycleOwner, Observer { vendorsResult ->
//                                when (vendorsResult) {
//                                    is ResultResponse.Success -> {
//                                        // Update RecyclerView atau adapter dengan data vendorsResult.data
//                                        val updatedVendors = vendorsResult.data
//                                        // Update UI dengan data terbaru
//                                        // Misalnya:
//                                        // updateRecyclerView(updatedVendors)
//                                        // dataMasterNestedAdapter.setData(updatedVendors)
//                                        // Atau refresh tampilan dengan cara lain
//                                    }
//                                    is ResultResponse.Error -> {
//                                        Log.e(TAG, "Error fetching vendors after adding new vendor: ${vendorsResult.error}")
//                                        // Tangani error jika perlu
//                                    }
//                                    is ResultResponse.Loading -> {
//                                        // Handle loading state if needed
//                                    }
//                                }
//                            })
//                        }
////                        is ResultResponse.Success -> {
////                            Log.d(TAG, "Vendor added successfully")
////                            dataMasterViewModel.fetchVendors(token)
////                            dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer {
////                                updateRecyclerView()
////                                dataMasterNestedAdapter.notifyItemChanged(position, true)
////                            })
////                        }
//                        is ResultResponse.Error -> {
//                            Log.e(TAG, "Error adding vendor: ${result.error}")
//                        }
//                    }
//                })
//        }
//    }

//class DataMasterFragment : Fragment() {
//
//    private var _binding: FragmentDataMasterBinding? = null
//    private val binding get() = _binding!!
//
//    private val dataMasterViewModel: DataMasterViewModel by viewModels {
//        ViewModelUserFactory.getInstance(requireContext())
//    }
//
//    private lateinit var vendors: List<VendorsModel>
//
//
//    private lateinit var dataMasterNestedAdapter: DataMasterNestedAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentDataMasterBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        dataMasterNestedAdapter = DataMasterNestedAdapter()
//
//        dataMasterNestedAdapter.setOnItemClickListener { item, position ->
//            when (item.title) {
//                "Vendors" -> showAddVendorPopup(position)
//                // "Categories" -> showAddCategoryPopup(position)
//                // "Units" -> showAddUnitPopup(position)
//                else -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Unknown item clicked: ${item.title}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//
//        binding.recyclerViewNested.adapter = dataMasterNestedAdapter
//        binding.recyclerViewNested.layoutManager = LinearLayoutManager(context)
//
//        setupObservers()
//
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.fetchVendors(token)
//        }
//    }
//
//    private fun setupObservers() {
//        dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
//            if (vendors != null) updateRecyclerView()
//        })
//
//        dataMasterViewModel.categoryNames.observe(viewLifecycleOwner, Observer { categories ->
//            if (categories != null) updateRecyclerView()
//        })
//
//        dataMasterViewModel.unitNames.observe(viewLifecycleOwner, Observer { units ->
//            if (units != null) updateRecyclerView()
//        })
//    }
//
//    private fun updateRecyclerView() {
//        val dataMasterItems = listOf(
//            DataMasterItem("Vendors", dataMasterViewModel.vendors.value ?: emptyList<Any>()),
//            DataMasterItem(
//                "Categories",
//                dataMasterViewModel.categoryNames.value ?: emptyList<Any>()
//            ),
//            DataMasterItem("Units", dataMasterViewModel.unitNames.value ?: emptyList<Any>())
//        )
//
//        dataMasterNestedAdapter.setData(dataMasterItems)
//    }
//
//    private fun showAddVendorPopup(position: Int) {
//        val popupBinding = PopupAddVendorBinding.inflate(layoutInflater)
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//            .setView(popupBinding.root)
//            .setTitle("Add Vendor")
//            .setPositiveButton("Save") { dialog, _ ->
//                val vendorName = popupBinding.etVendorName.text.toString().trim()
//                val vendorAddress = popupBinding.etVendorAddress.text.toString().trim()
//                val vendorEmail = popupBinding.etVendorEmail.text.toString().trim()
//                val vendorPhone = popupBinding.etVendorPhone.text.toString().trim()
//
//                if (vendorName.isNotEmpty() && vendorAddress.isNotEmpty() && vendorEmail.isNotEmpty() && vendorPhone.isNotEmpty()) {
//                    val vendorsRequest =
//                        VendorsRequest(vendorName, vendorAddress, vendorEmail, vendorPhone)
//                    saveVendor(position, vendorsRequest)
//                    dialog.dismiss()
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Mohon lengkapi semua kolom",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .create()
//
//        dialogBuilder.show()
//    }
//
//    private fun saveVendor(position: Int, vendorsRequest: VendorsRequest) {
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.postVendors(token, vendorsRequest)
//                .observe(viewLifecycleOwner, Observer { result ->
//                    when (result) {
//                        is ResultResponse.Loading -> {
//                            // Handle loading state if needed
//                        }
//                        is ResultResponse.Success -> {
//                            Log.d(TAG, "Vendor added successfully")
//
//
////                            dataMasterViewModel.fetchVendors(token).observe(viewLifecycleOwner, Observer {
////                                updateRecyclerView()
////                                dataMasterNestedAdapter.notifyItemChanged(position, true)
////                            })
//                            dataMasterViewModel.fetchVendors(token)
//                            dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer {
//                                updateRecyclerView()
//                                dataMasterNestedAdapter.notifyItemChanged(position, true)
//                            })
//
////                            dataMasterViewModel.fetchVendors(token).observe(this) { result ->
////                                if (result != null) {
////                                    when (result) {
////                                        is ResultResponse.Loading -> {
////                                            // Tampilkan indikator loading jika diperlukan
////                                        }
////                                        is ResultResponse.Success -> {
////                                            vendors = result.data
////                                        }
////                                        is ResultResponse.Error -> {
////                                            Log.e("PurchaseStockActivity", "Error: ${result.error}")
////                                        }
////                                    }
////                                }
////                            }
////                            dataMasterViewModel.authToken.value?.let { token ->
////                                dataMasterViewModel.fetchVendors(token)
////
////                                updateRecyclerView()
////                                // Ensure the expanded state is maintained
////                                dataMasterNestedAdapter.notifyItemChanged(position, true)
////                            }
//
////                            dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
////                                updateRecyclerView()
////
////                                dataMasterNestedAdapter.notifyItemChanged(position, true)
////                            })
////                            dataMasterViewModel.fetchVendors(token)
////                                .observe(viewLifecycleOwner, Observer {
////                                    updateRecyclerView()
////                                    // Ensure the expanded state is maintained
////                                    dataMasterNestedAdapter.notifyItemChanged(position, true)
////                                })
//                        }
//                        is ResultResponse.Error -> {
//                            Log.e(TAG, "Error adding vendor: ${result.error}")
//                        }
//                    }
//                })
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class DataMasterFragment : Fragment() {
//
//    private var _binding: FragmentDataMasterBinding? = null
//    private val binding get() = _binding!!
//
//    private val dataMasterViewModel: DataMasterViewModel by viewModels {
//        ViewModelUserFactory.getInstance(requireContext())
//    }
//
//    private lateinit var dataMasterNestedAdapter: DataMasterNestedAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentDataMasterBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        dataMasterNestedAdapter = DataMasterNestedAdapter()
//
//        dataMasterNestedAdapter.setOnItemClickListener { item, position ->
//            when (item.title) {
//                "Vendors" -> showAddVendorPopup(position)
//                // "Categories" -> showAddCategoryPopup(position)
//                // "Units" -> showAddUnitPopup(position)
//                else -> {
//                    // Handle other item types if needed
//                    Toast.makeText(requireContext(), "Unknown item clicked: ${item.title}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        binding.recyclerViewNested.adapter = dataMasterNestedAdapter
//        binding.recyclerViewNested.layoutManager = LinearLayoutManager(context)
//
//        setupObservers()
//
//        // Fetch vendors when the fragment is created
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.fetchVendors(token)
//        }
//    }
//
//    private fun setupObservers() {
//        dataMasterViewModel.categoryNames.observe(viewLifecycleOwner, Observer { categories ->
//            if (categories != null) updateRecyclerView()
//        })
//
//        dataMasterViewModel.unitNames.observe(viewLifecycleOwner, Observer { units ->
//            if (units != null) updateRecyclerView()
//        })
//
//        dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
//            if (vendors != null) updateRecyclerView()
//        })
//    }
//
//    private fun updateRecyclerView() {
//        val dataMasterItems = listOf(
//            DataMasterItem("Vendors", dataMasterViewModel.vendors.value ?: emptyList<Any>()),
//            DataMasterItem("Categories", dataMasterViewModel.categoryNames.value ?: emptyList<Any>()),
//            DataMasterItem("Units", dataMasterViewModel.unitNames.value ?: emptyList<Any>())
//        )
//
//        dataMasterNestedAdapter.setData(dataMasterItems)
//    }
//
//    private fun showAddVendorPopup(position: Int) {
//        val popupBinding = PopupAddVendorBinding.inflate(layoutInflater)
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//            .setView(popupBinding.root)
//            .setTitle("Add Vendor")
//            .setPositiveButton("Save") { dialog, _ ->
//                val vendorName = popupBinding.etVendorName.text.toString().trim()
//                val vendorAddress = popupBinding.etVendorAddress.text.toString().trim()
//                val vendorEmail = popupBinding.etVendorEmail.text.toString().trim()
//                val vendorPhone = popupBinding.etVendorPhone.text.toString().trim()
//
//                if (vendorName.isNotEmpty() && vendorAddress.isNotEmpty() && vendorEmail.isNotEmpty() && vendorPhone.isNotEmpty()) {
//                    val vendorsRequest = VendorsRequest(vendorName, vendorAddress, vendorEmail, vendorPhone)
//                    saveVendor(position, vendorsRequest)
//
//                    // Fetch vendors when the fragment is created
//                    dataMasterViewModel.authToken.value?.let { token ->
//                        dataMasterViewModel.fetchVendors(token)}
//                    dialog.dismiss()
//                } else {
//                    Toast.makeText(requireContext(), "Mohon lengkapi semua kolom", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .create()
//
//        dialogBuilder.show()
//    }
//
//    private fun saveVendor(position: Int, vendorsRequest: VendorsRequest) {
//        dataMasterViewModel.authToken.value?.let { token ->
//            dataMasterViewModel.postVendors(token, vendorsRequest)
//                .observe(viewLifecycleOwner, Observer { result ->
//                    when (result) {
//                        is ResultResponse.Loading -> {
//                            // Handle loading state if needed
//                        }
//                        is ResultResponse.Success -> {
//                            Log.d(TAG, "Vendor added successfully")
//                            // No need to fetch vendors here as it is already done in the ViewModel
//                        }
//                        is ResultResponse.Error -> {
//                            Log.e(TAG, "Error adding vendor: ${result.error}")
//                        }
//                    }
//                })
//        }
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}


//private fun updateDataAfterVendorAdded(newVendors: List<VendorsModel>) {
//    val dataMasterItems = listOf(
//        DataMasterItem("Vendors", newVendors),
//        DataMasterItem("Categories", dataMasterViewModel.categoryNames.value ?: emptyList<Any>()),
//        DataMasterItem("Units", dataMasterViewModel.unitNames.value ?: emptyList<Any>())
//    )
//    dataMasterNestedAdapter.setData(dataMasterItems)
//}


//    private fun showAddVendorPopup(position: Int) {
//        val popupBinding = PopupAddVendorBinding.inflate(layoutInflater)
//        val popupWindow = PopupWindow(
//            popupBinding.root,
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            true
//        )
//
//        // Setup onClickListener untuk tombol "Simpan"
//        popupBinding.btnSaveVendor.setOnClickListener {
//            saveVendor(position, popupBinding, popupWindow)
//        }
//
//        // Handle dismissal of popup when touched outside
//        popupWindow.setOnDismissListener {
//            clearInputFields(popupBinding)
//        }
//
//        // Show popup di tengah layar
//        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
//    }

//    private fun saveVendor(position: Int, popupBinding: PopupAddVendorBinding, popupWindow: PopupWindow) {
//        // Ambil nilai dari EditText menggunakan data binding
//        val vendorName = popupBinding.etVendorName.text.toString().trim()
//        val vendorAddress = popupBinding.etVendorAddress.text.toString().trim()
//        val vendorEmail = popupBinding.etVendorEmail.text.toString().trim()
//        val vendorPhone = popupBinding.etVendorPhone.text.toString().trim()
//
//        // Validasi input
//        if (vendorName.isNotEmpty() && vendorAddress.isNotEmpty() && vendorEmail.isNotEmpty() && vendorPhone.isNotEmpty()) {
//            val vendorsRequest = VendorsRequest(
//                vendorName,
//                vendorAddress,
//                vendorEmail,
//                vendorPhone
//            )
//
//            // Ambil token dari ViewModel
//            dataMasterViewModel.authToken.value?.let { token ->
//                // Panggil fungsi postVendors dari ViewModel untuk menyimpan vendor
//                dataMasterViewModel.postVendors(token, vendorsRequest)
//                    .observe(viewLifecycleOwner, Observer { result ->
//                        when (result) {
//                            is ResultResponse.Loading -> {
//                                // Handle loading state if needed
//                            }
//                            is ResultResponse.Success -> {
//                                // Tambahkan logika sukses jika diperlukan
//                                Log.d(TAG, "Vendor added successfully")
//                                // Clear input fields setelah sukses
//                                clearInputFields(popupBinding)
//                                // Dismiss popup setelah berhasil menyimpan
//                                popupWindow.dismiss()
//                                // Update data setelah menambahkan vendor
//                                dataMasterViewModel.fetchVendors(token) // Fetch vendors after adding a new one
//                            }
//                            is ResultResponse.Error -> {
//                                // Tambahkan logika error jika diperlukan
//                                Log.e(TAG, "Error adding vendor: ${result.error}")
//                            }
//                        }
//                    })
//            }
//        } else {
//            // Tampilkan pesan jika ada field yang kosong
//            Toast.makeText(requireContext(), "Mohon lengkapi semua kolom", Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun clearInputFields(popupBinding: PopupAddVendorBinding) {
//        // Kosongkan semua input fields setelah data tersimpan atau popup dismissed
//        popupBinding.etVendorName.text.clear()
//        popupBinding.etVendorAddress.text.clear()
//        popupBinding.etVendorEmail.text.clear()
//        popupBinding.etVendorPhone.text.clear()
//    }

//    private fun updateDataAfterVendorAdded(position: Int, vendorRequest: VendorsRequest) {
//        // Update data setelah menambahkan vendor di posisi tertentu
//        val updatedData = dataMasterNestedAdapter.currentList.toMutableList()
//        val updatedVendors = updatedData[position].data.toMutableList()
//        updatedVendors.add(vendorRequest) // Menambahkan vendor baru ke dalam data yang ada
//        updatedData[position] = updatedData[position].copy(data = updatedVendors)
//        dataMasterNestedAdapter.submitList(updatedData)
//    }

//    private fun updateDataAfterVendorAdded(position: Int, vendorRequest: VendorsRequest) {
//        val updatedData = dataMasterNestedAdapter.getData().toMutableList()
//        val updatedVendors = updatedData[position].data.toMutableList()
//        updatedVendors.add(vendorRequest)
//        updatedData[position] = updatedData[position].copy(data = updatedVendors)
//        dataMasterNestedAdapter.setData(updatedData)
//    }