package com.dicoding.picodiploma.loginwithanimation.view.purchase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPurchaseStockBinding
import com.dicoding.picodiploma.loginwithanimation.helper.helper
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

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

        val units = arrayOf("pcs", "pack", "roll", "meter")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.itemUnitSpinner.adapter = adapter

        setupViewModel()
        setupAction()
//        setupDropdownAction()
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

            if (enteredQuantity > 0) {
                val purchaseRequest = createPurchaseRequest(selectedUnit, enteredQuantity)
                addToCart(purchaseRequest)
                purchaseStocksViewModel.purchaseStocks(user.token, purchaseRequest, object : helper.ApiCallBackString {
                    override fun onResponse(success: Boolean, message: String) {
                        if (success) {
                            // Lakukan tindakan jika proses pembelian berhasil
                            // Misalnya, menampilkan pesan sukses atau mengubah tampilan
                        } else {
                            // Lakukan tindakan jika proses pembelian gagal
                            // Misalnya, menampilkan pesan error atau memberi notifikasi
                        }
                    }
                })
            } else {
                // Tampilkan pesan bahwa jumlah harus lebih dari 0
                Toast.makeText(this, "Quantity should be greater than 0", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createPurchaseRequest(unit: String, quantity: Int): PurchaseRequest {
        val supplierVendor = binding.supplierNameEditText.text.toString()
        val stockName = binding.itemNameEditText.text.toString()
        val stockCode = binding.itemCodeEditText.text.toString()
        val stockCategory = binding.itemCategoryEditText.text.toString()

        return when (unit) {
            "pcs" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, quantity, 0, 0, 0)
            "pack" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, quantity, 0, 0)
            "roll" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, 0, quantity, 0)
            "meter" -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, 0, 0, quantity)
            else -> PurchaseRequest(supplierVendor, stockName, stockCode, stockCategory, 0, 0, 0, 0)
        }
    }

    private fun addToCart(purchaseRequest: PurchaseRequest) {
        cart.add(purchaseRequest)
    }

//    private fun setupAction() {
//        binding.saveButton.setOnClickListener {
//            val purchaseRequest = PurchaseRequest(
//                binding.supplierNameEditText.text.toString(),
//                binding.itemNameEditText.text.toString(),
//                binding.itemCodeEditText.text.toString(),
//                binding.itemCategoryEditText.text.toString(),
//                stock_Pcs = 0,
//                stock_Pack = 0,
//                stock_Roll = 0,
//                stock_Meter = 50
//            )
//            purchaseStocksViewModel.purchaseStocks(user.token, purchaseRequest, object : helper.ApiCallBackString {
//                override fun onResponse(success: Boolean, message: String) {
//                    if (success) {
//                        // Lakukan tindakan jika proses pembelian berhasil
//                        // Misalnya, menampilkan pesan sukses atau mengubah tampilan
//                    } else {
//                        // Lakukan tindakan jika proses pembelian gagal
//                        // Misalnya, menampilkan pesan error atau memberi notifikasi
//                    }
//                }
//            })
//        }
//    }

//    private fun setupDropdownAction() {
//        val purchaseRequest = PurchaseRequest(
//            supplierVendor = "", // Berikan nilai default atau isi sesuai kebutuhan
//            stock_Name = "",
//            stock_Code = "",
//            stock_Category = "",
//            stock_Pcs = 0,
//            stock_Pack = 0,
//            stock_Roll = 25,
//            stock_Meter = 50
//        )
//
//        val dropdownItems = listOf(
//            StockUnitModel("Pcs", purchaseRequest.stock_Pcs),
//            StockUnitModel("Pack", purchaseRequest.stock_Pack),
//            StockUnitModel("Roll", purchaseRequest.stock_Roll),
//            StockUnitModel("Meter", purchaseRequest.stock_Meter)
//        )
//
//        val adapter = DropdownAdapter(this, dropdownItems)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        val spinner = findViewById<Spinner>(R.id.itemUnitSpinner)
//        spinner.adapter = adapter
//
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                // Update quantity based on selected item
//                val selectedItem = dropdownItems[position]
//                val selectedQuantity = when (selectedItem.label) {
//                    "Pcs" -> purchaseRequest.stock_Pcs
//                    "Pack" -> purchaseRequest.stock_Pack
//                    "Roll" -> purchaseRequest.stock_Roll
//                    "Meter" -> purchaseRequest.stock_Meter
//                    else -> 0
//                }
//                binding.itemQuantityEditText.setText(selectedQuantity.toString())
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Do nothing
//            }
//
//        }
//    }
}