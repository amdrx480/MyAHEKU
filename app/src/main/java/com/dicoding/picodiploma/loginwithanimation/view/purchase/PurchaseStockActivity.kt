package com.dicoding.picodiploma.loginwithanimation.view.purchase

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPurchaseStockBinding
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.model.purchase.PurchaseModel
import com.dicoding.picodiploma.loginwithanimation.service.database.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.stocks.StocksActivity

//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PurchaseStockActivity : AppCompatActivity() {

    private lateinit var user: UserModel
    private lateinit var binding: ActivityPurchaseStockBinding

    private val purchaseStocksViewModel: PurchaseStocksViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val cart = mutableListOf<PurchaseModel>() // List untuk menyimpan produk yang akan dibeli
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(StocksActivity.EXTRA_USER)!!

        val units = arrayOf("Pcs", "Pack", "Roll", "Meter")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.itemUnitSpinner.adapter = adapter

//        setupViewModel()
        setupAction()
    }

    private fun setupAction() {
        binding.saveButton.setOnClickListener {
            val supplierVendor = binding.supplierNameEditText.text.toString()
            val stockName = binding.itemNameEditText.text.toString()
            val stockCode = binding.itemCodeEditText.text.toString()
            val stockCategory = binding.itemCategoryEditText.text.toString()

            val selectedUnit = binding.itemUnitSpinner.selectedItem.toString()
            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0

//            if (selectedUnit.isNotEmpty() && enteredQuantity > 0 && supplierVendor.isNotEmpty() && stockName.isNotEmpty() && stockCode.isNotEmpty() && stockCategory.isNotEmpty()) {
            val purchaseRequest = createPurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, selectedUnit, enteredQuantity)
            addToCart(purchaseRequest)
//            purchaseStocksViewModel.uploadPurchaseStocks(user.token, supplierVendor, stockName, stockCode, stockCategory, purchaseModel.stock_Pcs, purchaseModel.stock_Pack, purchaseModel.stock_Roll, purchaseModel.stock_Meter)
            purchaseStocksViewModel.uploadPurchaseStocks(user.token, supplierVendor, stockName, stockCode, stockCategory)
                    .observe(this) {
                        if (it != null) {
                            when (it) {
                                is ResultResponse.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is ResultResponse.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    helper.showToast(this, getString(R.string.upload_success))
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.data_success))
                                        setMessage(getString(R.string.upload_success))
                                        setPositiveButton(getString(R.string.continue_)) { _, _ ->
                                            binding.progressBar.visibility = View.GONE
                                        }
                                        create()
                                        show()
                                    }
                                }
                                is ResultResponse.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.upload_failed))
                                        setMessage(getString(R.string.upload_failed) + ", ${it.error}")
                                        setPositiveButton(getString(R.string.continue_)) { _, _ ->
                                            binding.progressBar.visibility = View.GONE
                                        }
                                        create()
                                        show()
                                    }
                                }
                            }
                        }
                    }
//            } else {
//                // Tampilkan pesan bahwa jumlah harus lebih dari 0
//                showAlertDialog("Gagal", "Harap Isi Semua.")
//            }
        }
    }

    private fun createPurchaseRequest(supplierVendor: String, stockName: String, stockCode: String, stockCategory: String, unit: String, quantity: Int): PurchaseModel {
        return when (unit) {
            "Pcs" -> PurchaseModel(supplierVendor, stockName, stockCode, stockCategory, quantity, 0, 0, 0)
            "Pack" -> PurchaseModel(supplierVendor, stockName, stockCode, stockCategory, 0, quantity, 0, 0)
            "Roll" -> PurchaseModel(supplierVendor, stockName, stockCode, stockCategory, 0, 0, quantity, 0)
            "Meter" -> PurchaseModel(supplierVendor, stockName, stockCode, stockCategory, 0, 0, 0, quantity)
            else -> PurchaseModel(supplierVendor, stockName, stockCode, stockCategory, 0, 0, 0, 0)
        }
    }

    private fun addToCart(purchaseRequest: PurchaseModel) {
        cart.add(purchaseRequest)
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        const val EXTRA_USER = "user"
    }

}