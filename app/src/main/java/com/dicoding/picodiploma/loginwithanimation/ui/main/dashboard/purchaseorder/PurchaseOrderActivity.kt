package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchaseorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders.PurchaseOrderModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPurchaseOrderBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory

class PurchaseOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPurchaseOrderBinding
    private lateinit var purchaseOrderAdapter: PurchaseOrderAdapter

    private val purchaseOrderViewModel: PurchaseOrderViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        purchaseOrderAdapter = PurchaseOrderAdapter()

        setupRecyclerView()
        setupObserver()

//        val dummyData = generateDummyData()
//        purchaseOrderAdapter.submitList(dummyData)
    }

    private fun setupRecyclerView() {
        binding.rvPurchaseOrder.apply {
            layoutManager = LinearLayoutManager(this@PurchaseOrderActivity)
            setHasFixedSize(true)
            adapter = purchaseOrderAdapter
        }
    }

    private fun setupObserver() {
        purchaseOrderViewModel.purchaseOrder.observe(this) { purchaseOrders ->
            Log.d(TAG, "Purchase orders received: ${purchaseOrders.size}")
            purchaseOrderAdapter.submitList(purchaseOrders)
        }
    }

//    private fun generateDummyData(): List<PurchaseOrderModel> {
//        return listOf(
//            PurchaseOrderModel(
//                id = 1,
//                customerName = "Ajax",
//                stockName = "Produk A",
//                stockCode = "1001",
//                unitName = "Pcs",
//                categoryName = "Kategori 1",
//                quantity = 10,
//                price = 50000,
//                packagingOfficerName = "Officer A",
//                reminderTime = "2024-07-22T19:00:00+07:00"
//            ),
//            PurchaseOrderModel(
//                id = 2,
//                customerName = "Beatrix",
//                stockName = "Produk B",
//                stockCode = "1002",
//                unitName = "Pcs",
//                categoryName = "Kategori 2",
//                quantity = 5,
//                price = 75000,
//                packagingOfficerName = "Officer B",
//                reminderTime = "2024-07-22T19:00:00+07:00"
//            ),
//            PurchaseOrderModel(
//                id = 3,
//                customerName = "Cairo",
//                stockName = "Produk C",
//                stockCode = "1003",
//                unitName = "Pcs",
//                categoryName = "Kategori 3",
//                quantity = 8,
//                price = 60000,
//                packagingOfficerName = "Officer C",
//                reminderTime = "2024-07-22T19:00:00+07:00"
//            )
//        )
//    }


//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        private const val TAG = "PurchaseOrderActivity"
    }
}



//    private fun setupObservers() {
//        dataMasterViewModel.vendors.observe(viewLifecycleOwner, Observer { vendors ->
//            binding.swipeRefreshLayout.isRefreshing = false
//            vendors?.let {
//                val dataMasterItems = listOf(
//                    DataMasterItem(
//                        "Customers",
//                        dataMasterViewModel.customerNames.value ?: emptyList(),
//                        isExpanded = true
//                    ),
//                    DataMasterItem("Vendors", it, isExpanded = true), // Maintain the expanded state
//                    DataMasterItem(
//                        "Categories",
//                        dataMasterViewModel.categoryNames.value ?: emptyList(),
//                        isExpanded = true
//                    ),
//                    DataMasterItem(
//                        "Units",
//                        dataMasterViewModel.unitNames.value ?: emptyList(),
//                        isExpanded = true
//                    )
//                )
//                updateRecyclerView(dataMasterItems)
//                toggleInfoVisibility(it.isEmpty())
//            }
//        })
//    }