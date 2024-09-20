package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.category.CategoryModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPurchaseStockBinding
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesRequest
import com.dicoding.picodiploma.loginwithanimation.data.model.units.UnitsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.DashboardFragment

class PurchaseStockActivity : AppCompatActivity() {

    private lateinit var vendors: List<VendorsModel>
    private lateinit var units: List<UnitsModel>
    private lateinit var categories: List<CategoryModel>

    private lateinit var token: String

    private lateinit var binding: ActivityPurchaseStockBinding

    private val purchaseStocksViewModel: PurchaseStocksViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(DashboardFragment.EXTRA_TOKEN) ?: ""
//        token = intent.getStringExtra(DashboardFragment.EXTRA_TOKEN)!!


        setupAction()
//        setupObservers()
    }


    private fun setupAction() {
        val spinnerVendors: Spinner = findViewById(R.id.itemVendorSpinner)
        val spinnerUnits: Spinner = findViewById(R.id.itemUnitSpinner)
        val spinnerCategory: Spinner = findViewById(R.id.itemCategorySpinner)

        purchaseStocksViewModel.authToken.observe(this) { token ->
            // Vendors
            purchaseStocksViewModel.fetchVendors(token).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultResponse.Loading -> {
                            // Tampilkan indikator loading jika diperlukan
                        }
                        is ResultResponse.Success -> {
                            vendors = result.data
                            if (vendors.isNotEmpty()) {
                                val adapter = ArrayAdapter(
                                    this@PurchaseStockActivity,
                                    android.R.layout.simple_spinner_item,
                                    vendors.map { it.vendor_Name }
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerVendors.adapter = adapter
                            } else {
                                Log.e("PurchaseStockActivity", "Vendor list is empty")
                            }
                        }
                        is ResultResponse.Error -> {
                            Log.e("PurchaseStockActivity", "Error: ${result.error}")
                        }
                    }
                }
            }

            spinnerVendors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedVendor = vendors.getOrNull(position)
                    selectedVendor?.let {
                        Toast.makeText(
                            this@PurchaseStockActivity,
                            "Vendor selected: ${it.vendor_Name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(
                            "PurchaseStockActivity",
                            "Vendor selected: ID=${it.id}, Name=${it.vendor_Name}"
                        )
                        purchaseStocksViewModel.setSelectedVendor(it)
                    }
                        ?: run {
                            Log.e("PurchaseStockActivity", "Selected vendor is null")
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Tindakan jika tidak ada vendor yang dipilih (opsional)
                }
            }

            // Units
            purchaseStocksViewModel.fetchUnits(token).observe(this, Observer { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Tampilkan indikator loading jika diperlukan
                    }
                    is ResultResponse.Success -> {
                        units = result.data
                        val adapter = ArrayAdapter(
                            this@PurchaseStockActivity,
                            android.R.layout.simple_spinner_item,
                            units.map { it.unit_name }
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerUnits.adapter = adapter
                    }
                    is ResultResponse.Error -> {
                        Log.e("PurchaseStockActivity", "Error: ${result.error}")
                    }
                }
            })

            spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedUnit = units.getOrNull(position)
                    selectedUnit?.let {
                        Toast.makeText(
                            this@PurchaseStockActivity,
                            "Unit selected: ${it.unit_name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(
                            "PurchaseStockActivity",
                            "Unit selected: ID=${it.id}, Name=${it.unit_name}"
                        )
                        purchaseStocksViewModel.setSelectedUnit(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Tindakan jika tidak ada unit yang dipilih (opsional)
                }
            }

            // Categories
            purchaseStocksViewModel.fetchCategories(token).observe(this, Observer { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        // Tampilkan indikator loading jika diperlukan
                    }
                    is ResultResponse.Success -> {
                        categories = result.data
                        val adapter = ArrayAdapter(
                            this@PurchaseStockActivity,
                            android.R.layout.simple_spinner_item,
                            categories.map { it.category_Name }
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCategory.adapter = adapter
                    }
                    is ResultResponse.Error -> {
                        Log.e("PurchaseStockActivity", "Error: ${result.error}")
                    }
                }
            })

            spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCategory = categories.getOrNull(position)
                    selectedCategory?.let {
                        Toast.makeText(
                            this@PurchaseStockActivity,
                            "Category selected: ${it.category_Name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(
                            "PurchaseStockActivity",
                            "Category selected: ID=${it.id}, Name=${it.category_Name}"
                        )
                        purchaseStocksViewModel.setSelectedCategory(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Tindakan jika tidak ada kategori yang dipilih (opsional)
                }
            }

            // Log the token value
            Log.d("PurchasesStockActivity", "Auth Token: $token")
        }

        binding.saveButton.setOnClickListener {
            // Ambil vendor yang dipilih dari ViewModel
            val selectedVendor = purchaseStocksViewModel.selectedVendor.value
            // Ambil unit yang dipilih dari ViewModel
            val selectedUnit = purchaseStocksViewModel.selectedUnit.value
            // Ambil category yang dipilih dari ViewModel
            val selectedCategory = purchaseStocksViewModel.selectedCategory.value

            if (selectedCategory == null || selectedVendor == null || selectedUnit == null) {
                Toast.makeText(this, "Please select Item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val enteredStockName = binding.itemNameEditText.text.toString()
            val enteredStockCode = binding.itemCodeEditText.text.toString()
            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
            val enteredPurchasePrice = binding.itemPurchaseEditText.text.toString().toIntOrNull() ?: 0
            val enteredSellingPrice = binding.itemSellingEditText.text.toString().toIntOrNull() ?: 0

            // Periksa apakah semua data sudah diisi
            if (enteredStockName.isNotEmpty() && enteredStockCode.isNotEmpty() && enteredQuantity > 0) {
                val purchaseUnitRequest = createPurchaseRequest(
                    selectedVendor.id,
                    enteredStockName,
                    enteredStockCode,
                    selectedCategory.id,
                    selectedUnit.id,
                    enteredQuantity,
                    enteredPurchasePrice,
                    enteredSellingPrice
                )

                purchaseStocksViewModel.uploadPurchaseStocks(token, purchaseUnitRequest)
                    .observe(this) {
                        when (it) {
                            is ResultResponse.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is ResultResponse.Success -> {
                                binding.progressBar.visibility = View.GONE
                                helper.showToast(this, getString(R.string.upload_success))
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.upload_success))
                                    setMessage(getString(R.string.data_success))
                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
                                        binding.progressBar.visibility = View.GONE
                                    }
                                    create()
                                    show()
                                }
                                // Set semua EditText menjadi kosong setelah data dikirim
                                binding.itemNameEditText.text.clear()
                                binding.itemCodeEditText.text.clear()
                                binding.itemQuantityEditText.text.clear()
                                binding.itemPurchaseEditText.text.clear()
                                binding.itemSellingEditText.text.clear()
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
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle("Gagal")
                    setMessage("Harap Isi Semua")
                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
                        binding.progressBar.visibility = View.GONE
                    }
                    create()
                    show()
                }
            }
        }
    }


//        binding.saveButton.setOnClickListener {
//            // Ambil vendor yang dipilih dari ViewModel
//            val selectedVendor = purchaseStocksViewModel.selectedVendor.value
//            // Ambil unit yang dipilih dari ViewModel
//            val selectedUnit = purchaseStocksViewModel.selectedUnit.value
//            // Ambil category yang dipilih dari ViewModel
//            val selectedCategory = purchaseStocksViewModel.selectedCategory.value
//
//            if (selectedCategory == null || selectedVendor == null || selectedUnit == null) {
//                Toast.makeText(this, "Please select Item", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val enteredStockName = binding.itemNameEditText.text.toString()
//            val enteredStockCode = binding.itemCodeEditText.text.toString()
//            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
////            val enteredQuantity = binding.itemQuantityEditText.text.toString().toInt()
//            val enteredPurchasePrice =
//                binding.itemPurchaseEditText.text.toString().toIntOrNull() ?: 0
////                binding.itemPurchaseEditText.text.toString().toInt()
//
//            val enteredSellingPrice = binding.itemSellingEditText.text.toString().toIntOrNull() ?: 0
////            val enteredSellingPrice = binding.itemSellingEditText.text.toString().toInt()
//
////            if (selectedUnit.isNotEmpty() && selectedCategory.isNotEmpty() && enteredQuantity > 0 && supplierVendor.isNotEmpty() && stockName.isNotEmpty() && stockCode.isNotEmpty()) {
//            val purchaseUnitRequest = createPurchaseRequest(
//                selectedVendor.id,
//                enteredStockName,
//                enteredStockCode,
//                selectedCategory.id,
//                selectedUnit.id,
//                enteredQuantity,
//                enteredPurchasePrice,
//                enteredSellingPrice
//            )
//            purchaseStocksViewModel.uploadPurchaseStocks(token, purchaseUnitRequest)
//                .observe(this) {
//                    if (it != null) {
//                        when (it) {
//                            is ResultResponse.Loading -> {
//                                binding.progressBar.visibility = View.VISIBLE
//                            }
//                            is ResultResponse.Success -> {
//                                binding.progressBar.visibility = View.GONE
//                                helper.showToast(this, getString(R.string.upload_success))
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_success))
//                                    setMessage(getString(R.string.data_success))
//                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
//                                        binding.progressBar.visibility = View.GONE
//                                    }
//                                    create()
//                                    show()
//                                }
//                            }
//                            is ResultResponse.Error -> {
//                                binding.progressBar.visibility = View.GONE
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_failed))
//                                    setMessage(getString(R.string.upload_failed) + ", ${it.error}")
//                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
//                                        binding.progressBar.visibility = View.GONE
//                                    }
//                                    create()
//                                    show()
//                                }
//                            }
//                        }
//                    }
//                }


            // Set semua EditText menjadi kosong setelah data dikirim
//                binding.itemNameEditText.text.clear()
//                binding.itemCodeEditText.text.clear()
//                binding.itemQuantityEditText.text.clear()
//            } else {
//                // Tampilkan pesan bahwa jumlah harus lebih dari 0
//                AlertDialog.Builder(this).apply {
//                    setTitle("Gagal")
//                    setMessage("Harap Isi Semua")
//                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
//                        binding.progressBar.visibility = View.GONE
//                    }
//                    create()
//                    show()
//                }
//            }
//        }
//    }

//    private fun setupObservers() {
//        purchaseStocksViewModel.authToken.observe(this) { token ->
//            // Log the token value
//            Log.d("PurchasesStockActivity", "Auth Token: $token")
//        }
//    }

    private fun createPurchaseRequest(
        vendorId: Int,
        stockName: String,
        stockCode: String,
        categoryId: Int,
        unitId: Int,
        quantity: Int,
        purchasePrice: Int,
        sellingPrice: Int,
    ): PurchasesRequest {
        return PurchasesRequest(
            vendorId = vendorId,
            stockName = stockName,
            stockCode = stockCode,
            categoryId = categoryId,
            unitId = unitId,
            quantity = quantity,
            purchasePrice = purchasePrice,
            sellingPrice = sellingPrice,
        )
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}


//        purchaseStocksViewModel.getCategories(token)
//        purchaseStocksViewModel.categoryList.observe(this, Observer { categories ->
//            categories?.let {
//                val adapter = ArrayAdapter(
//                    this@PurchaseStockActivity,
//                    android.R.layout.simple_spinner_item,
//                    categories.map { it.category_Name }
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                spinnerCategory.adapter = adapter
//            } ?: run {
//                Log.e("PurchaseStockActivity", "Category list is null")
//            }
//        })
//        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                // Dapatkan kategori yang dipilih berdasarkan posisi
////                val selectedCategory = parent.getItemAtPosition(position) as? ListCategoryItem
//                val selectedCategory =
//                    purchaseStocksViewModel.categoryList.value?.get(position)
//                if (selectedCategory != null) {
//                    Toast.makeText(
//                        this@PurchaseStockActivity,
//                        "Category selected: ${selectedCategory.category_Name}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    Log.i(
//                        "PurchaseStockActivity",
//                        "Category selected: ID=${selectedCategory.id}, Name=${selectedCategory.category_Name}"
//                    )
//                    purchaseStocksViewModel.setSelectedCategory(selectedCategory)
//                }
////                val selectedCategory = purchaseStocksViewModel.categoryList.value?.get(position)
////                // Set kategori yang dipilih di ViewModel
////                Toast.makeText(
////                    this@PurchaseStockActivity,
////                    "Category selected: ${selectedCategory?.category_Name}",
////                    Toast.LENGTH_SHORT
////                ).show()
////                Log.i(
////                    "PurchaseStockActivity",
////                    "Category selected: ID=${selectedCategory?.id}, Name=${selectedCategory?.category_Name}"
////                )
////
////                purchaseStocksViewModel.setSelectedCategory(selectedCategory)
////                selectedCategory?.let {
////                    Toast.makeText(this@PurchaseStockActivity, "Category selected: ${it.category_Name}", Toast.LENGTH_SHORT).show()
////                    Log.i("PurchaseStockActivity", "Category selected: ID=${it.id}, Name=${it.category_Name}")
////
////                    purchaseStocksViewModel.setSelectedCategory(it)
////                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada kategori yang dipilih (opsional)
//            }
//        }

//        purchaseStocksViewModel.getVendors(token)
//        purchaseStocksViewModel.vendorList.observe(this, Observer { vendors ->
//            vendors?.let {
//                val vendor = vendors.map { it.vendor_Name }
//                val adapter = ArrayAdapter(
//                    this@PurchaseStockActivity,
//                    android.R.layout.simple_spinner_item,
//                    vendor
////                vendors.map { it.vendor_Name }
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                spinnerVendors.adapter = adapter
//            } ?: run {
//                Log.e("PurchaseStockActivity", "Vendor list is null")
//            }
//        })
//        // Tangani pemilihan item dari vendorSpinner
//        spinnerVendors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
////                val selectedVendor = purchaseStocksViewModel.vendorList.value?.get(position)
////                if (selectedVendor != null) {
////                    Toast.makeText(
////                        this@PurchaseStockActivity,
////                        "Vendor selected: ${selectedVendor.vendor_Name}",
////                        Toast.LENGTH_SHORT
////                    ).show()
////                    Log.i(
////                        "PurchaseStockActivity",
////                        "Vendors selected: ID=${selectedVendor.id}, Name=${selectedVendor.vendor_Name}"
////                    )
////                    purchaseStocksViewModel.setSelectedVendor(selectedVendor)
////                }
//                val selectedVendor = purchaseStocksViewModel.vendorList.value?.get(position)
//
//                // Notifikasi atau log saat vendor dipilih
//                Toast.makeText(
//                    this@PurchaseStockActivity,
//                    "Vendor selected: ${selectedVendor?.vendor_Name}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.i(
//                    "PurchaseStockActivity",
//                    "Vendors selected: ID=${selectedVendor?.id}, Name=${selectedVendor?.vendor_Name}"
//                )
//
//
//                // Simpan vendor yang dipilih di ViewModel
//                purchaseStocksViewModel.setSelectedVendor(selectedVendor)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada vendor yang dipilih (opsional)
//            }
//        }
//
//        purchaseStocksViewModel.getUnits(token)
//        purchaseStocksViewModel.unitList.observe(this, Observer { units ->
//            units?.let {
//                val unitNames = units.map { it.units_name }
//                val adapter = ArrayAdapter(
//                    this@PurchaseStockActivity,
//                    android.R.layout.simple_spinner_item,
//                    unitNames
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                spinnerUnits.adapter = adapter
////                unitName = unitNames.firstOrNull()
//            } ?: run {
//                Log.e("PurchaseStockActivity", "Unit list is null")
//            }
//        })
//        // Tangani pemilihan item dari unitSpinner
//        spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedUnit = purchaseStocksViewModel.unitList.value?.get(position)
//                if (selectedUnit != null) {
//                    Toast.makeText(
//                        this@PurchaseStockActivity,
//                        "Unit selected: ${selectedUnit.units_name}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    Log.i(
//                        "PurchaseStockActivity",
//                        "Units selected: ID=${selectedUnit.id}, Name=${selectedUnit.units_name}"
//                    )
//                    purchaseStocksViewModel.setSelectedUnit(selectedUnit)
//                }
////                val selectedUnit = purchaseStocksViewModel.unitList.value?.get(position)
////
////                // Notifikasi atau log saat unit dipilih
////                Toast.makeText(
////                    this@PurchaseStockActivity,
////                    "Unit selected: ${selectedUnit?.units_name}",
////                    Toast.LENGTH_SHORT
////                ).show()
////                Log.i(
////                    "PurchaseStockActivity",
////                    "Units selected: ID=${selectedUnit?.id}, Name=${selectedUnit?.units_name}"
////                )
////
////                // Simpan unit yang dipilih di ViewModel
////                purchaseStocksViewModel.setSelectedUnit(selectedUnit)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada unit yang dipilih (opsional)
//            }
//        }


//class PurchaseStockActivity : AppCompatActivity() {
//
//    //    private lateinit var user: UserModel
//    private var token: String = ""
//    private lateinit var binding: ActivityPurchaseStockBinding
//
//    private val purchaseStocksViewModel: PurchaseStocksViewModel by viewModels {
//        ViewModelUserFactory.getInstance(this)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPurchaseStockBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        token = intent.getStringExtra(DashboardFragment.EXTRA_TOKEN) ?: ""
//
////        user = intent.getParcelableExtra(StocksActivity.EXTRA_TOKEN)!!
//
//        setupAction()
//    }
//
//
//    private fun setupAction() {
//        val spinner: Spinner = findViewById(R.id.itemCategorySpinner)
//        val spinnerVendors: Spinner = findViewById(R.id.itemVendorSpinner)
//        val spinnerUnits: Spinner = findViewById(R.id.itemUnitSpinner)
//
//        purchaseStocksViewModel.getCategories(token)
//        purchaseStocksViewModel.categoryList.observe(this, Observer { categories ->
//            val adapter = ArrayAdapter(
//                this@PurchaseStockActivity,
//                android.R.layout.simple_spinner_item,
//                categories.map { it.category_Name }
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = adapter
//        })
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                // Dapatkan kategori yang dipilih berdasarkan posisi
////                val selectedCategory = parent.getItemAtPosition(position) as? ListCategoryItem
//                val selectedCategory = purchaseStocksViewModel.categoryList.value?.get(position)
//                // Set kategori yang dipilih di ViewModel
//                Toast.makeText(
//                    this@PurchaseStockActivity,
//                    "Category selected: ${selectedCategory?.category_Name}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.i(
//                    "PurchaseStockActivity",
//                    "Category selected: ID=${selectedCategory?.id}, Name=${selectedCategory?.category_Name}"
//                )
//
//                purchaseStocksViewModel.setSelectedCategory(selectedCategory)
////                selectedCategory?.let {
////                    Toast.makeText(this@PurchaseStockActivity, "Category selected: ${it.category_Name}", Toast.LENGTH_SHORT).show()
////                    Log.i("PurchaseStockActivity", "Category selected: ID=${it.id}, Name=${it.category_Name}")
////
////                    purchaseStocksViewModel.setSelectedCategory(it)
////                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada kategori yang dipilih (opsional)
//            }
//        }
//
//        purchaseStocksViewModel.getVendors(token)
//        purchaseStocksViewModel.vendorList.observe(this, Observer { vendors ->
//            val adapter = ArrayAdapter(
//                this@PurchaseStockActivity,
//                android.R.layout.simple_spinner_item,
//                vendors.map { it.vendor_Name }
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinnerVendors.adapter = adapter
//        })
//        // Tangani pemilihan item dari vendorSpinner
//        spinnerVendors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedVendor = purchaseStocksViewModel.vendorList.value?.get(position)
//
//                // Notifikasi atau log saat vendor dipilih
//                Toast.makeText(
//                    this@PurchaseStockActivity,
//                    "Vendor selected: ${selectedVendor?.vendor_Name}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.i(
//                    "PurchaseStockActivity",
//                    "Vendors selected: ID=${selectedVendor?.id}, Name=${selectedVendor?.vendor_Name}"
//                )
//
//
//                // Simpan vendor yang dipilih di ViewModel
//                purchaseStocksViewModel.setSelectedVendor(selectedVendor)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada vendor yang dipilih (opsional)
//            }
//        }
//
//        purchaseStocksViewModel.getUnits(token)
//        purchaseStocksViewModel.unitList.observe(this, Observer { units ->
//            val adapter = ArrayAdapter(
//                this@PurchaseStockActivity,
//                android.R.layout.simple_spinner_item,
//                units.map { it.units_name }
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinnerUnits.adapter = adapter
//        })
//        // Tangani pemilihan item dari unitSpinner
//        spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedUnit = purchaseStocksViewModel.unitList.value?.get(position)
//
//                // Notifikasi atau log saat unit dipilih
//                Toast.makeText(
//                    this@PurchaseStockActivity,
//                    "Unit selected: ${selectedUnit?.units_name}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.i(
//                    "PurchaseStockActivity",
//                    "Units selected: ID=${selectedUnit?.id}, Name=${selectedUnit?.units_name}"
//                )
//
//                // Simpan unit yang dipilih di ViewModel
//                purchaseStocksViewModel.setSelectedUnit(selectedUnit)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada unit yang dipilih (opsional)
//            }
//        }
//
//        binding.saveButton.setOnClickListener {
//            val selectedCategory = purchaseStocksViewModel.selectedCategory.value
//            // Ambil vendor yang dipilih dari ViewModel
//            val selectedVendor = purchaseStocksViewModel.selectedVendor.value
//            // Ambil unit yang dipilih dari ViewModel
//            val selectedUnit = purchaseStocksViewModel.selectedUnit.value
//
//            if (selectedCategory == null || selectedVendor == null || selectedUnit == null) {
//                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val enteredStockName = binding.itemNameEditText.text.toString()
//            val enteredStockCode = binding.itemCodeEditText.text.toString()
//            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
//            val enteredPurchasePrice =
//                binding.itemPurchaseEditText.text.toString().toIntOrNull() ?: 0
//            val enteredSellingPrice = binding.itemSellingEditText.text.toString().toIntOrNull() ?: 0
//
////            if (selectedUnit.isNotEmpty() && selectedCategory.isNotEmpty() && enteredQuantity > 0 && supplierVendor.isNotEmpty() && stockName.isNotEmpty() && stockCode.isNotEmpty()) {
//            val purchaseUnitRequest = createPurchaseRequest(
//                selectedVendor.id,
//                enteredStockName,
//                enteredStockCode,
//                selectedCategory.id,
//                selectedUnit.id,
//                enteredQuantity,
//                enteredPurchasePrice,
//                enteredSellingPrice
//            )
//            purchaseStocksViewModel.uploadPurchaseStocks(token, purchaseUnitRequest)
//                .observe(this) {
//                    if (it != null) {
//                        when (it) {
//                            is ResultResponse.Loading -> {
//                                binding.progressBar.visibility = View.VISIBLE
//                            }
//                            is ResultResponse.Success -> {
//                                binding.progressBar.visibility = View.GONE
//                                helper.showToast(this, getString(R.string.upload_success))
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_success))
//                                    setMessage(getString(R.string.data_success))
//                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
//                                        binding.progressBar.visibility = View.GONE
//                                    }
//                                    create()
//                                    show()
//                                }
//                            }
//                            is ResultResponse.Error -> {
//                                binding.progressBar.visibility = View.GONE
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_failed))
//                                    setMessage(getString(R.string.upload_failed) + ", ${it.error}")
//                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
//                                        binding.progressBar.visibility = View.GONE
//                                    }
//                                    create()
//                                    show()
//                                }
//                            }
//                        }
//                    }
//                }
//
//
//            // Set semua EditText menjadi kosong setelah data dikirim
////                binding.itemNameEditText.text.clear()
////                binding.itemCodeEditText.text.clear()
////                binding.itemQuantityEditText.text.clear()
////            } else {
////                // Tampilkan pesan bahwa jumlah harus lebih dari 0
////                AlertDialog.Builder(this).apply {
////                    setTitle("Gagal")
////                    setMessage("Harap Isi Semua")
////                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
////                        binding.progressBar.visibility = View.GONE
////                    }
////                    create()
////                    show()
////                }
////            }
//        }
//    }
//
//    private fun createPurchaseRequest(
//        vendorId: Int,
//        stockName: String,
//        stockCode: String,
//        categoryId: Int,
//        unitId: Int,
//        quantity: Int,
//        purchasePrice: Int,
//        sellingPrice: Int,
//    ): PurchaseRequest {
//        return PurchaseRequest(
//            vendorId = vendorId,
//            stockName = stockName,
//            stockCode = stockCode,
//            categoryId = categoryId,
//            unitId = unitId,
//            quantity = quantity,
//            purchasePrice = purchasePrice,
//            sellingPrice = sellingPrice,
//        )
//    }
//
//    companion object {
//        const val EXTRA_TOKEN = "extra_token"
//    }
//
////    companion object {
////        const val EXTRA_USER = "user"
////    }
//}


//    private fun setCategory(){
//        val spinner: Spinner = findViewById(R.id.itemCategorySpinner)
//
//        purchaseStocksViewModel.getCategories(user.token)
//        purchaseStocksViewModel.categoryList.observe(this, Observer { categories ->
//            val adapter = ArrayAdapter(
//                this@PurchaseStockActivity,
//                android.R.layout.simple_spinner_item,
//                categories
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = adapter
////            binding.itemUnitSpinner.adapter = adapter
//        })
//
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                // Dapatkan kategori yang dipilih berdasarkan posisi
//                val selectedCategory = parent.getItemAtPosition(position) as? List<ListCategoryItem>
//
//                // Set kategori yang dipilih di ViewModel
//                selectedCategory?.let {
//                    purchaseStocksViewModel.setSelectedCategory(it)
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada kategori yang dipilih (opsional)
//            }
//        }
//    }

//    private var selectedVendor: Int = 0
//    private var selectedUnit: Int = 0
//    private var selectedCategory: Int = 0

//        lifecycleScope.launch {
//            try {
//                val apiResponse = ApiConfig.ApiService().getCategorylifecycleScope(user.token)
//                val categories = apiResponse.token
//                val categoryNames = categories.map { it.category_Name }
//
//                val adapter = ArrayAdapter(this@PurchaseStockActivity,
//                    android.R.layout.simple_spinner_item,
//                    categoryNames
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                binding.itemCategorySpinner.adapter = adapter
//                // Set an OnItemSelectedListener to the spinner
//
//                binding.itemCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                        // Handle the selected item here
//                        val selectedCategory = categories[position]
//                        purchaseStocksViewModel.setSelectedCategory(selectedCategory)
//                        helper.showToast(this@PurchaseStockActivity, getString(R.string.no_story))
//
//                        // Do something with the selectedCategory
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>) {
//                        // Handle when nothing is selected if needed
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                helper.showToast(this@PurchaseStockActivity, getString(R.string.information_failed))
//
//            }
//        }
/*        val vendors = listOf("Vendor 1", "Vendor 2", "Vendor 3")

        // Buat ArrayAdapter untuk itemCategorySpinner
        val vendorsAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            vendors
        )
        // Set layout dropdown untuk spinner
        vendorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set adapter ke itemCategorySpinner
        binding.itemVendorSpinner.adapter = vendorsAdapter
        binding.itemVendorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Handle the selected item here
                selectedVendor = position
                helper.showToast(this@PurchaseStockActivity, getString(R.string.no_story))

                // Do something with the selectedCategory
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle when nothing is selected if needed
            }
        }

        val units = listOf("Unit 1", "Unit 2", "Unit 3")

        // Buat ArrayAdapter untuk itemCategorySpinner
        val unitsAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            units
        )
        // Set layout dropdown untuk spinner
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set adapter ke itemCategorySpinner
        binding.itemUnitSpinner.adapter = unitsAdapter
        binding.itemUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Handle the selected item here
                selectedUnit = position
                helper.showToast(this@PurchaseStockActivity, getString(R.string.no_story))

                // Do something with the selectedCategory
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle when nothing is selected if needed
            }
        }

        val categories = listOf("Kategori 1", "Kategori 2", "Kategori 3")

        // Buat ArrayAdapter untuk itemCategorySpinner
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        // Set layout dropdown untuk spinner
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set adapter ke itemCategorySpinner
        binding.itemCategorySpinner.adapter = categoryAdapter
        binding.itemCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        // Handle the selected item here
         selectedCategory = position
        helper.showToast(this@PurchaseStockActivity, getString(R.string.no_story))

                        // Do something with the selectedCategory
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle when nothing is selected if needed
                    }
                }*/
//        val units = listOf("Unit 1", "Unit 2", "Unit 3")
////            Pair(1, "Units 1"),
////            Pair(2, "Units 2"),
////            Pair(3, "Units 3"),
//
//        // Buat ArrayAdapter untuk itemCategorySpinner
//        val unitsAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item,
//            units
//        )
//        // Set layout dropdown untuk spinner
//        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        // Set adapter ke itemCategorySpinner
//        binding.itemUnitSpinner.adapter = unitsAdapter

//    private fun setupVendors(){
//        lifecycleScope.launch {
//            try {
//                val apiResponse = ApiConfig.ApiService().getVendors(user.token)
//                val categories = apiResponse.token
//                val vendorsNames = categories.map { it.vendor_Name }
//
//                val adapter = ArrayAdapter(this@PurchaseStockActivity,
//                    android.R.layout.simple_spinner_item,
//                    vendorsNames
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                binding.itemVendorSpinner.adapter = adapter
//                adapter.addAll(vendorsNames)
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                helper.showToast(this@PurchaseStockActivity, getString(R.string.information_failed))
//
//            }
//        }
//    }

//    private fun setupUnits(){
//        lifecycleScope.launch {
//            try {
//                val apiResponse = ApiConfig.ApiService().getUnits(user.token)
//                val categories = apiResponse.token
//                val unitsNames = categories.map { it.units }
//
//                val adapter = ArrayAdapter(this@PurchaseStockActivity,
//                    android.R.layout.simple_spinner_item,
//                    unitsNames
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                binding.itemUnitSpinner.adapter = adapter
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                helper.showToast(this@PurchaseStockActivity, getString(R.string.information_failed))
//
//            }
//        }
//    }

//private fun setupCategory(){
//                lifecycleScope.launch {
//            try {
//                val apiResponse = ApiConfig.ApiService().getCategorylifecycleScope(user.token)
//                val categories = apiResponse.token
//                val categoryNames = categories.map { it.category_Name }
//
//                val adapter = ArrayAdapter(this@PurchaseStockActivity,
//                    android.R.layout.simple_spinner_item,
//                    categoryNames
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                binding.itemCategorySpinner.adapter = adapter
//                // Set an OnItemSelectedListener to the spinner
//
//                binding.itemCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                        // Handle the selected item here
//                        val selectedCategory = categories[position]
//                        helper.showToast(this@PurchaseStockActivity, getString(R.string.no_story))
//
//                        // Do something with the selectedCategory
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>) {
//                        // Handle when nothing is selected if needed
//                    }
//                }
//                } catch (e: Exception) {
//                e.printStackTrace()
//                helper.showToast(this@PurchaseStockActivity, getString(R.string.information_failed))
//
//            }
//        }
//}