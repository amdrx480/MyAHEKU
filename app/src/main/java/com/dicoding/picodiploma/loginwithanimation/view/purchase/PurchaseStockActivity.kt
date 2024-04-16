package com.dicoding.picodiploma.loginwithanimation.view.purchase

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPurchaseStockBinding
import com.dicoding.picodiploma.loginwithanimation.helper.helper
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PurchaseStockActivity : AppCompatActivity() {

    private lateinit var purchaseStocksViewModel: PurchaseStocksViewModel
    private lateinit var user: UserModel

    private lateinit var binding: ActivityPurchaseStockBinding

    private val cart = mutableListOf<PurchaseRequest>() // List untuk menyimpan produk yang akan dibeli
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val units = arrayOf("Pcs", "Pack", "Roll", "Meter")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.itemUnitSpinner.adapter = adapter

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        purchaseStocksViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[PurchaseStocksViewModel::class.java]

        purchaseStocksViewModel.getSession().observe(this) { preferences ->
            user = UserModel(
                preferences.password,
                preferences.token,
                true
            )
        }
    }

    private fun setupAction() {
        binding.saveButton.setOnClickListener {
            val selectedUnit = binding.itemUnitSpinner.selectedItem.toString()
            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
            val supplierVendor = binding.supplierNameEditText.text.toString()
            val stockName = binding.itemNameEditText.text.toString()
            val stockCode = binding.itemCodeEditText.text.toString()
            val stockCategory = binding.itemCategoryEditText.text.toString()

            if (selectedUnit.isNotEmpty() && enteredQuantity > 0 && supplierVendor.isNotEmpty() && stockName.isNotEmpty() && stockCode.isNotEmpty() && stockCategory.isNotEmpty()) {
                val purchaseRequest = createPurchaseRequest(selectedUnit, enteredQuantity,supplierVendor, stockName, stockCode, stockCategory)
                addToCart(purchaseRequest)
                purchaseStocksViewModel.purchaseStocks(user.token, purchaseRequest, object : helper.ApiCallBackString {
                    override fun onResponse(success: Boolean, message: String) {
                        if (success) {
                            // Lakukan tindakan jika proses pembelian berhasil
                            // Misalnya, menampilkan pesan sukses atau mengubah tampilan
                            showAlertDialog("Berhasil", "Pembelian berhasil.")

                        } else {
                            // Lakukan tindakan jika proses pembelian gagal
                            // Misalnya, menampilkan pesan error atau memberi notifikasi
                            showAlertDialog("Gagal", "Pembelian gagal: $message")
                        }
                    }
                })
            } else {
                // Tampilkan pesan bahwa jumlah harus lebih dari 0
                showAlertDialog("Gagal", "Harap Isi Semua.")
            }
        }
    }

    private fun createPurchaseRequest(unit: String, quantity: Int, supplierVendor: String, stockName: String, stockCode: String, stockCategory: String): PurchaseRequest {
        return when (unit) {
            "Pcs" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, quantity, 0, 0, 0)
            "Pack" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, quantity, 0, 0)
            "Roll" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, 0, quantity, 0)
            "Meter" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, 0, 0, quantity)
            else -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, 0, 0, 0)
        }
    }

    private fun addToCart(purchaseRequest: PurchaseRequest) {
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
}