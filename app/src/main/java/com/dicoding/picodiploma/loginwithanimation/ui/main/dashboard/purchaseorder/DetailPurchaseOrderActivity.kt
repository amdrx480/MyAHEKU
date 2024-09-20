package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchaseorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders.PurchaseOrderModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailPurchaseOrderBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPurchaseOrderBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.detail.DetailStocksActivity
import com.dicoding.picodiploma.loginwithanimation.utils.helper

class DetailPurchaseOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPurchaseOrderBinding

//    private val purchaseOrderViewModel: PurchaseOrderViewModel by viewModels {
//        ViewModelUserFactory.getInstance(this)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPurchaseOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan data dari Intent
        val purchaseOrder = intent.getParcelableExtra<PurchaseOrderModel>(EXTRA_DETAIL)

        // Menampilkan data di layout
        if (purchaseOrder != null) {
            binding.tvCustomerName.text = purchaseOrder.customerName
            binding.tvStockName.text = purchaseOrder.stockName
            binding.tvStockCode.text = purchaseOrder.stockCode
//            binding.tvCategoryName.text = purchaseOrder.categoryName
            binding.tvUnitName.text = purchaseOrder.unitName
            binding.tvQuantity.text = purchaseOrder.quantity.toString()
            binding.tvPrice.text = purchaseOrder.price.toString()
//            binding.tvPackagingOfficerId.text = purchaseOrder.packagingOfficerId.toString()
            binding.tvReminderTime.text = helper.formatTime(purchaseOrder.reminderTime)
            binding.tvDeliveryAddressValue.text = purchaseOrder.deliveryAddress
        } else {
            Log.e("DetailPurchaseOrderActivity", "No purchase order data received")
        }
    }

    companion object {
        const val EXTRA_DETAIL = "DetailPurchaseOrder"
    }
}