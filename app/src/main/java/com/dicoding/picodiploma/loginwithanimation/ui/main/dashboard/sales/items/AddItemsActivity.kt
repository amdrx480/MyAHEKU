package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales.items

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddSalesBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.SalesStocksRequest
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.DashboardFragment
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales.CartActivity
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales.SalesStocksViewModel

class AddItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSalesBinding

    //    private lateinit var user: UserModel
    private var token: String = ""

    private var customerId: Int? = null
    private var stockId: Int? = null

    private val salesStockViewModel: SalesStocksViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        user = intent.getParcelableExtra(CartActivity.EXTRA_USER)!!
        token = intent.getStringExtra(CartActivity.EXTRA_TOKEN) ?: ""
        setListStockItem()
    }

    private fun setListStockItem() {
        val customerNameAutoComplete: AutoCompleteTextView =
            binding.customerNameAutocompleteTextView
        val nameItemAutoComplete: AutoCompleteTextView = binding.itemNameAutoCompleteTextView

        // Observasi pelanggan
        salesStockViewModel.getCustomers(token)
        salesStockViewModel.customersList.observe(this, Observer { customers ->
            val customerNames = customers.map { it.customer_name }
            val customerNameAdapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, customerNames)
            customerNameAutoComplete.setAdapter(customerNameAdapter)
        })

        // Observasi stok item
        salesStockViewModel.showListStock(token)
        salesStockViewModel.itemStock.observe(this, Observer { stockItems ->
            val itemNames = stockItems.map { it.stockName }
            val itemAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNames)
            nameItemAutoComplete.setAdapter(itemAdapter)
        })

        // Penanganan pemilihan pelanggan
        customerNameAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
//            val selectedCustomer = salesStockViewModel.customersList.value?.getOrNull(position)
                val selectedCustomer = salesStockViewModel.customersList.value?.find {
                    it.customer_name == parent.getItemAtPosition(position) as String
                }
                selectedCustomer?.let {
                    customerId = it.id
                    Toast.makeText(
                        this@AddItemsActivity,
                        "Customer selected: ${it.customer_name}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i(
                        "AddItemsActivity",
                        "Customer selected: ID=${it.id}, Name=${it.customer_name}"
                    )
                    Log.e(
                        "AddItemsActivity",
                        "Customer selected: ID=${it.id}, Name=${it.customer_name}"
                    )
                }
            }

        // Penanganan pemilihan item stok
        nameItemAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // Mendapatkan nama item yang dipilih
                val selectedItemName = parent.getItemAtPosition(position) as String
                // Menemukan item yang dipilih berdasarkan nama
                val selectedItem = salesStockViewModel.itemStock.value?.find { it.stockName == selectedItemName }
                selectedItem?.let {
                    stockId = it.id.toInt()
                    binding.itemCodeEditText.setText(it.stockCode)
                    binding.itemUnitEditText.setText(it.unitName)
                    binding.itemCategoryEditText.setText(it.categoryName)
//                    binding.itemSellingEditText.setText(it.sellingPrice.toString())
//                    val formattedPrice = helper.formatToRupiah(it.sellingPrice.toDouble())
//                    binding.itemSellingEditText.setText(formattedPrice)
                    // Format harga menjadi Rupiah dan set di itemSellingEditText
                    val formattedPrice = helper.formatToRupiah(it.sellingPrice.toDouble())
                    binding.itemSellingEditText.setText(formattedPrice)

                    binding.itemQuantityEditText.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            // Tambahkan logika untuk menghitung subtotal saat teks berubah
                            val enteredQuantity = s.toString().toIntOrNull() ?: 0
                            val price = it.sellingPrice.toDouble()
                            calculateAndSetSubtotal(enteredQuantity, price)
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // Tidak perlu diimplementasikan
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            // Tidak perlu diimplementasikan
                        }
                    })

                    Toast.makeText(
                        this@AddItemsActivity,
                        "Item selected: ${it.stockName}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("AddItemsActivity", "Item selected: ID=${it.id}, Name=${it.stockName}")
                }
            }

        // Penanganan klik tombol simpan
        binding.saveButton.setOnClickListener {
            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
            if (customerId != null && stockId != null) {
                val salesStocksRequest =
                    createAddSalesRequest(customerId!!, stockId!!, enteredQuantity)
                salesStockViewModel.uploadItems(token, salesStocksRequest).observe(this) {
                    when (it) {
                        is ResultResponse.Loading -> {
                            // Handle loading state
                        }
                        is ResultResponse.Success -> {
                            nameItemAutoComplete.text.clear()
                            customerNameAutoComplete.text.clear()
                            binding.itemCodeEditText.text.clear()
                            binding.itemUnitEditText.text.clear()
                            binding.itemQuantityEditText.text.clear()
                            binding.itemCategoryEditText.text.clear()
                            binding.itemSellingEditText.text.clear()

                            helper.showToast(this, getString(R.string.upload_success))
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.berhasil))
                                setMessage(getString(R.string.data_success_sales))
                                setPositiveButton(getString(R.string.continue_)) { _, _ -> }
                                create()
                                show()
                            }
                        }
                        is ResultResponse.Error -> {
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.gagal))
                                setMessage(getString(R.string.data_failed_sales) + ", ${it.error}")
                                setPositiveButton(getString(R.string.continue_)) { _, _ -> }
                                create()
                                show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Please select both a customer and an item.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun calculateAndSetSubtotal(quantity: Int, price: Double) {
        val subtotal = quantity * price
        val formattedSubtotal = helper.formatToRupiah(subtotal)
        binding.itemTotalPriceEditText.setText(formattedSubtotal)
    }

    private fun createAddSalesRequest(
        customer_id: Int,
        stock_id: Int,
        quantity: Int,
    ): SalesStocksRequest {
        return SalesStocksRequest(
            customer_id = customer_id,
            stock_id = stock_id,
            quantity = quantity,
        )
    }

    companion object {
        const val EXTRA_USER = "user"
    }

}

//class AddItemsActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityAddSalesBinding
//
//    //    private lateinit var user: UserModel
//    private var token: String = ""
//
//    private var customerId: Int? = null
//    private var stockId: Int? = null
//
//    private val salesStockViewModel: SalesStocksViewModel by viewModels {
//        ViewModelUserFactory.getInstance(this)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAddSalesBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
////        user = intent.getParcelableExtra(CartActivity.EXTRA_USER)!!
//        token = intent.getStringExtra(CartActivity.EXTRA_TOKEN) ?: ""
//        setListStockItem()
//    }
//
//    private fun setListStockItem() {
//        val customerNameAutoComplete: AutoCompleteTextView =
//            binding.customerNameAutocompleteTextView
//        val nameItemAutoComplete: AutoCompleteTextView = binding.itemNameAutoCompleteTextView
//
//        // Observasi pelanggan
//        salesStockViewModel.getCustomers(token)
//        salesStockViewModel.customersList.observe(this, Observer { customers ->
//            val customerNames = customers.map { it.customer_name }
//            val customerNameAdapter =
//                ArrayAdapter(this, android.R.layout.simple_list_item_1, customerNames)
//            customerNameAutoComplete.setAdapter(customerNameAdapter)
//        })
//
//        // Observasi stok item
//        salesStockViewModel.showListStock(token)
//        salesStockViewModel.itemStock.observe(this, Observer { stockItems ->
//            val itemNames = stockItems.map { it.stockName }
//            val itemAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNames)
//            nameItemAutoComplete.setAdapter(itemAdapter)
//        })
//
//        // Penanganan pemilihan pelanggan
//        customerNameAutoComplete.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
////            val selectedCustomer = salesStockViewModel.customersList.value?.getOrNull(position)
//                val selectedCustomer = salesStockViewModel.customersList.value?.find {
//                    it.customer_name == parent.getItemAtPosition(position) as String
//                }
//                selectedCustomer?.let {
//                    customerId = it.id
//                    Toast.makeText(
//                        this@AddItemsActivity,
//                        "Customer selected: ${it.customer_name}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    Log.i(
//                        "AddItemsActivity",
//                        "Customer selected: ID=${it.id}, Name=${it.customer_name}"
//                    )
//                    Log.e(
//                        "AddItemsActivity",
//                        "Customer selected: ID=${it.id}, Name=${it.customer_name}"
//                    )
//                }
//            }
//
//        // Penanganan pemilihan item stok
//        nameItemAutoComplete.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
//                val selectedItem = salesStockViewModel.itemStock.value?.getOrNull(position)
//                selectedItem?.let {
//                    stockId = it.id.toInt()
//                    binding.itemCodeEditText.setText(it.stockCode)
//                    binding.itemUnitEditText.setText(it.unitsName)
//                    binding.itemCategoryEditText.setText(it.categoryName)
//                    binding.itemSellingEditText.setText(it.sellingPrice.toString())
//
//                    binding.itemQuantityEditText.addTextChangedListener(object : TextWatcher {
//                        override fun afterTextChanged(s: Editable?) {
//                            // Tambahkan logika untuk menghitung subtotal saat teks berubah
//                            val enteredQuantity = s.toString().toIntOrNull() ?: 0
//                            val price =
//                                binding.itemSellingEditText.text.toString().toDoubleOrNull() ?: 0.0
//                            calculateAndSetSubtotal(enteredQuantity, price)
//                        }
//
//                        override fun beforeTextChanged(
//                            s: CharSequence?,
//                            start: Int,
//                            count: Int,
//                            after: Int
//                        ) {
//                            // Tidak perlu diimplementasikan
//                        }
//
//                        override fun onTextChanged(
//                            s: CharSequence?,
//                            start: Int,
//                            before: Int,
//                            count: Int
//                        ) {
//                            // Tidak perlu diimplementasikan
//                        }
//                    })
//
//                    Toast.makeText(
//                        this@AddItemsActivity,
//                        "Item selected: ${it.stockName}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    Log.i("AddItemsActivity", "Item selected: ID=${it.id}, Name=${it.stockName}")
//                }
//            }
//
//        // Penanganan klik tombol simpan
//        binding.saveButton.setOnClickListener {
//            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
//            if (customerId != null && stockId != null) {
//                val salesStocksRequest =
//                    createAddSalesRequest(customerId!!, stockId!!, enteredQuantity)
//                salesStockViewModel.uploadItems(token, salesStocksRequest).observe(this) {
//                    when (it) {
//                        is ResultResponse.Loading -> {
//                            // Handle loading state
//                        }
//                        is ResultResponse.Success -> {
//                            nameItemAutoComplete.text.clear()
//                            customerNameAutoComplete.text.clear()
//                            binding.itemCodeEditText.text.clear()
//                            binding.itemUnitEditText.text.clear()
//                            binding.itemQuantityEditText.text.clear()
//                            binding.itemCategoryEditText.text.clear()
//                            binding.itemSellingEditText.text.clear()
//
//                            helper.showToast(this, getString(R.string.upload_success))
//                            AlertDialog.Builder(this).apply {
//                                setTitle(getString(R.string.upload_success))
//                                setMessage(getString(R.string.data_success))
//                                setPositiveButton(getString(R.string.continue_)) { _, _ -> }
//                                create()
//                                show()
//                            }
//                        }
//                        is ResultResponse.Error -> {
//                            AlertDialog.Builder(this).apply {
//                                setTitle(getString(R.string.upload_failed))
//                                setMessage(getString(R.string.upload_failed) + ", ${it.error}")
//                                setPositiveButton(getString(R.string.continue_)) { _, _ -> }
//                                create()
//                                show()
//                            }
//                        }
//                    }
//                }
//            } else {
//                Toast.makeText(
//                    this,
//                    "Please select both a customer and an item.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    private fun calculateAndSetSubtotal(quantity: Int, price: Double) {
//        val subtotal = quantity * price
//        val formattedSubtotal = helper.formatToRupiah(subtotal)
//        binding.itemTotalPriceEditText.setText(formattedSubtotal)
//    }
//
//    private fun createAddSalesRequest(
//        customer_id: Int,
//        stock_id: Int,
//        quantity: Int,
//    ): SalesStocksRequest {
//        return SalesStocksRequest(
//            customer_id = customer_id,
//            stock_id = stock_id,
//            quantity = quantity,
//        )
//    }
//
//    companion object {
//        const val EXTRA_USER = "user"
//    }
//
//}



//    override fun onBackPressed() {
//        // Tambahkan untuk mengembalikan hasil resultAddItemsActivity ke CartActivity
////        val resultIntent = Intent().apply {
////            putExtra(EXTRA_CUSTOMER_ID, customerId)
////        }
////        setResult(Activity.RESULT_OK, resultIntent)
////        finish()
//        salesStockViewModel.getCartItemsForCustomer(user.token, customerId!!)
//        super.onBackPressed()
//    }
//        const val EXTRA_CUSTOMER_ID = "customer_id"
//        customerId = intent.getIntExtra(CartActivity.EXTRA_CUSTOMER_ID, -1)

//    private fun setListStockItem() {
//        val autoCompleteNameItem: AutoCompleteTextView = binding.autocompleteNameTextView
//        val autoCompleteCustomerName: AutoCompleteTextView = binding.customerNameAutocompleteTextView
//
//        salesStockViewModel.showListStock(user.token)
//        salesStockViewModel.itemStock.observe(this, Observer { salesItem ->
//            val adapter = ArrayAdapter(
//                this,
//                android.R.layout.simple_list_item_1,
//                salesItem.map { it.stock_Name }
//            )
//            val customerNameAdapter = ArrayAdapter(
//                this,
//                android.R.layout.simple_list_item_1,
//                salesItem.map { it.customer_Name }
//            )
//            autoCompleteNameItem.setAdapter(adapter)
//            autoCompleteCustomerName.setAdapter(customerNameAdapter)
////            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
////            autoCompleteNameItem.adapter = adapter
//        })
//
//        autoCompleteCustomerName.onItemClickListener =
//            AdapterView.OnItemClickListener{ parent, view, position, id ->
//                val selectedCustomer = salesStockViewModel.itemStock.value?.get(position)
//                selectedCustomer?.let {
//                    binding.itemCodeEditText.setText(selectedCustomer.customer_Name)
//
//                    Toast.makeText(
//                        this@AddItemsActivity,
//                        "Item selected: ${it.customer_Name}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    Log.i("AddSalesActivity", "Item selected: ID=${it.id}, Name=${it.customer_Name}")
//
//                    val customerId = selectedCustomer.id.toInt()
//                }
//
//            }
//        // Tangani pemilihan item dari vendorSpinner
//        autoCompleteNameItem.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
//                // Dapatkan objek Item pada posisi yang dipilih
//                val selectedItem = salesStockViewModel.itemStock.value?.get(position)
//                // Notifikasi atau log saat item dipilih
//                selectedItem?.let {
//
////                    binding.itemCodeEditText.setText(selectedItem.customer_Name)
//                    binding.itemCodeEditText.setText(selectedItem.stock_Code)
//                    binding.itemUnitEditText.setText(selectedItem.units_Name)
//                    binding.itemCategoryEditText.setText(selectedItem.category_Name)
//                    binding.itemSellingEditText.setText(selectedItem.selling_Price.toString())
//
//                    Toast.makeText(
//                        this@AddItemsActivity,
//                        "Item selected: ${it.stock_Name}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    Log.i("AddSalesActivity", "Item selected: ID=${it.id}, Name=${it.stock_Name}")
//
//                    // Simpan item yang dipilih di ViewModel atau lakukan tindakan lain sesuai kebutuhan
//                    val stockId = selectedItem.id.toInt()
//                }
//            }
//        binding.saveButton.setOnClickListener {
//            val enteredQuantity =
//                binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
//            val salesStocksRequest = createAddSalesRequest(customerId, stockId, enteredQuantity)
//            salesStockViewModel.uploadItems(user.token, salesStocksRequest)
//                .observe(this) {
//                    if (it != null) {
//                        when (it) {
//                            is ResultResponse.Loading -> {
////                                    binding.progressBar.visibility = View.VISIBLE
//                            }
//                            is ResultResponse.Success -> {
////                                    binding.progressBar.visibility = View.GONE
//                                autoCompleteNameItem.text.clear()
//                                binding.itemCodeEditText.text.clear()
//                                binding.itemUnitEditText.text.clear()
//                                binding.itemQuantityEditText.text.clear()
//                                binding.itemCategoryEditText.text.clear()
//                                binding.itemSellingEditText.text.clear()
//
//                                helper.showToast(
//                                    this,
//                                    getString(R.string.upload_success)
//                                )
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_success))
//                                    setMessage(getString(R.string.data_success))
//                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
////                                            binding.progressBar.visibility = View.GONE
//                                    }
//                                    create()
//                                    show()
//                                }
//                            }
//                            is ResultResponse.Error -> {
////                                    binding.progressBar.visibility = View.GONE
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_failed))
//                                    setMessage(getString(R.string.upload_failed) + ", ${it.error} stok tidak cukup untuk")
//
////                                                emit(ResultResponse.Error(e.message.toString()))
//
//                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
////                                            binding.progressBar.visibility = View.GONE
//                                    }
//                                    create()
//                                    show()
//                                }
//                            }
//                        }
//                    }
//                }
//        }
//    }

