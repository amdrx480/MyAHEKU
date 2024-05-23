package com.dicoding.picodiploma.loginwithanimation.view.purchase

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
import com.dicoding.picodiploma.loginwithanimation.service.data.purchase.PurchaseRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPurchaseStockBinding
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.service.database.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.stocks.StocksActivity

class PurchaseStockActivity : AppCompatActivity() {

    private lateinit var user: UserModel
    private lateinit var binding: ActivityPurchaseStockBinding

    private val purchaseStocksViewModel: PurchaseStocksViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(StocksActivity.EXTRA_USER)!!

        setupAction()
    }


    private fun setupAction() {
        val spinner: Spinner = findViewById(R.id.itemCategorySpinner)
        val spinnerVendors: Spinner = findViewById(R.id.itemVendorSpinner)
        val spinnerUnits: Spinner = findViewById(R.id.itemUnitSpinner)

        purchaseStocksViewModel.getCategories(user.token)
        purchaseStocksViewModel.categoryList.observe(this, Observer { categories ->
            val adapter = ArrayAdapter(
                this@PurchaseStockActivity,
                android.R.layout.simple_spinner_item,
                categories.map { it.category_Name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        })
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Dapatkan kategori yang dipilih berdasarkan posisi
//                val selectedCategory = parent.getItemAtPosition(position) as? ListCategoryItem
                val selectedCategory = purchaseStocksViewModel.categoryList.value?.get(position)
                // Set kategori yang dipilih di ViewModel
                Toast.makeText(this@PurchaseStockActivity, "Category selected: ${selectedCategory?.category_Name}", Toast.LENGTH_SHORT).show()
                Log.i("PurchaseStockActivity", "Category selected: ID=${selectedCategory?.id}, Name=${selectedCategory?.category_Name}")

                purchaseStocksViewModel.setSelectedCategory(selectedCategory)
//                selectedCategory?.let {
//                    Toast.makeText(this@PurchaseStockActivity, "Category selected: ${it.category_Name}", Toast.LENGTH_SHORT).show()
//                    Log.i("PurchaseStockActivity", "Category selected: ID=${it.id}, Name=${it.category_Name}")
//
//                    purchaseStocksViewModel.setSelectedCategory(it)
//                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tindakan jika tidak ada kategori yang dipilih (opsional)
            }
        }

        purchaseStocksViewModel.getVendors(user.token)
        purchaseStocksViewModel.vendorList.observe(this, Observer { vendors ->
            val adapter = ArrayAdapter(
                this@PurchaseStockActivity,
                android.R.layout.simple_spinner_item,
                vendors.map { it.vendor_Name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerVendors.adapter = adapter
        })
        // Tangani pemilihan item dari vendorSpinner
        spinnerVendors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedVendor = purchaseStocksViewModel.vendorList.value?.get(position)

                // Notifikasi atau log saat vendor dipilih
                Toast.makeText(this@PurchaseStockActivity, "Vendor selected: ${selectedVendor?.vendor_Name}", Toast.LENGTH_SHORT).show()
                Log.i("PurchaseStockActivity", "Vendors selected: ID=${selectedVendor?.id}, Name=${selectedVendor?.vendor_Name}")


                // Simpan vendor yang dipilih di ViewModel
                purchaseStocksViewModel.setSelectedVendor(selectedVendor)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tindakan jika tidak ada vendor yang dipilih (opsional)
            }
        }

        purchaseStocksViewModel.getUnits(user.token)
        purchaseStocksViewModel.unitList.observe(this, Observer { units ->
            val adapter = ArrayAdapter(
                this@PurchaseStockActivity,
                android.R.layout.simple_spinner_item,
                units.map { it.units_name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnits.adapter = adapter
        })
        // Tangani pemilihan item dari unitSpinner
        spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedUnit = purchaseStocksViewModel.unitList.value?.get(position)

                // Notifikasi atau log saat unit dipilih
                Toast.makeText(this@PurchaseStockActivity, "Unit selected: ${selectedUnit?.units_name}", Toast.LENGTH_SHORT).show()
                Log.i("PurchaseStockActivity", "Units selected: ID=${selectedUnit?.id}, Name=${selectedUnit?.units_name}")

                // Simpan unit yang dipilih di ViewModel
                purchaseStocksViewModel.setSelectedUnit(selectedUnit)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tindakan jika tidak ada unit yang dipilih (opsional)
            }
        }

        binding.saveButton.setOnClickListener {
            val selectedCategory = purchaseStocksViewModel.selectedCategory.value
            // Ambil vendor yang dipilih dari ViewModel
            val selectedVendor = purchaseStocksViewModel.selectedVendor.value
            // Ambil unit yang dipilih dari ViewModel
            val selectedUnit = purchaseStocksViewModel.selectedUnit.value

            if (selectedCategory == null || selectedVendor == null || selectedUnit == null) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val enteredStockName = binding.itemNameEditText.text.toString()
            val enteredStockCode = binding.itemCodeEditText.text.toString()
            val enteredQuantity = binding.itemQuantityEditText.text.toString().toIntOrNull() ?: 0
            val enteredPurchasePrice = binding.itemPurchaseEditText.text.toString().toIntOrNull() ?: 0
            val enteredSellingPrice = binding.itemSellingEditText.text.toString().toIntOrNull() ?: 0

//            if (selectedUnit.isNotEmpty() && selectedCategory.isNotEmpty() && enteredQuantity > 0 && supplierVendor.isNotEmpty() && stockName.isNotEmpty() && stockCode.isNotEmpty()) {
            val purchaseUnitRequest = createPurchaseRequest(selectedVendor.id, enteredStockName, enteredStockCode, selectedCategory.id, selectedUnit.id, enteredQuantity, enteredPurchasePrice, enteredSellingPrice)
                purchaseStocksViewModel.uploadPurchaseStocks(user.token, purchaseUnitRequest)
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
                                        setTitle(getString(R.string.upload_success))
                                        setMessage(getString(R.string.data_success))
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
        }
    }
    private fun createPurchaseRequest(
        supplierVendor: Int,
        stockName: String,
        stockCode: String,
        categoryId: Int,
        unitsId: Int,
        quantity: Int,
        purchase_price: Int,
        selling_price: Int,
    ): PurchaseRequest {
        return PurchaseRequest(
            vendor_id = supplierVendor,
            stock_name = stockName,
            stock_code = stockCode,
            category_id = categoryId,
            units_id = unitsId,
            Quantity = quantity,
            purchase_price = purchase_price,
            selling_price = selling_price,
        )
    }

    companion object {
        const val EXTRA_USER = "user"
    }
}


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