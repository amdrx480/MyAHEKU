package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySalesStockBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.customers.ListCustomersItem
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.CartItemsModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.DashboardFragment
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales.items.AddItemsActivity

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesStockBinding
//    private lateinit var user: UserModel
    private lateinit var cartItemsAdapter: CartItemsAdapter

    private var token: String = ""

    private var customerId: Int? = null
    private var selectedCustomer: ListCustomersItem? = null
    private var currentSubtotal: Double = 0.0

    private val salesStockViewModel: SalesStocksViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

//    private val salesStockViewModel: SalesStocksViewModel by viewModels {
//        ViewModelFactory.getInstance(this)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        user = intent.getParcelableExtra(EXTRA_TOKEN)!!
        token = intent.getStringExtra(DashboardFragment.EXTRA_TOKEN)?: ""

        cartItemsAdapter = CartItemsAdapter()
        setupViews()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        customerId?.let { // Pastikan customerId tidak null
            handleCustomerSelection(
                selectedCustomer ?: return
            ) // Memanggil fungsi untuk mengambil data berdasarkan customer ID
        }
    }

    private fun setupViews() {
        setupAutoCompleteTextView()
        setupRecyclerView()
        setupAddButton()
        setupCartItemsAdapter()
    }

    private fun setupRecyclerView() {
        binding.cartItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            setHasFixedSize(true)
            adapter = cartItemsAdapter
        }
    }

    private fun setupAutoCompleteTextView() {
        val customerNameAutocompleteTextView: AutoCompleteTextView =
            binding.customerNameAutocompleteTextView
        salesStockViewModel.getCustomers(token)
        salesStockViewModel.customersList.observe(this) { customers ->
            val customerNames = customers.map { it.customer_name }
            val customerNameAdapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, customerNames)
            customerNameAutocompleteTextView.setAdapter(customerNameAdapter)
        }
        customerNameAutocompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedCustomerName = parent.getItemAtPosition(position) as String
                val selectedCustomer =
                    salesStockViewModel.customersList.value?.find { it.customer_name == selectedCustomerName }
                selectedCustomer?.let {
                    handleCustomerSelection(it)
                    customerId = it.id
                }
            }
        binding.saveButton.setOnClickListener {
            if (customerId != null) {
                salesStockViewModel.postItemTransactions(token, customerId!!).observe(this) {
                    when (it) {
                        is ResultResponse.Loading -> {
                            // Handle loading state
                        }
                        is ResultResponse.Success -> {

                            helper.showToast(this, getString(R.string.upload_success))
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.upload_success))
                                setMessage(getString(R.string.data_success))
                                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                                    salesStockViewModel.getCartItemsForCustomer(
                                        token,
                                        customerId!!
                                    )
                                }
                                create()
                                show()

                            }
                        }
                        is ResultResponse.Error -> {
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.upload_failed))
                                setMessage(getString(R.string.upload_failed) + ", ${it.error}")
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

    private fun handleCustomerSelection(customer: ListCustomersItem) {
        selectedCustomer = customer
        salesStockViewModel.getCartItemsForCustomer(token, customer.id)
    }

    private fun setupAddButton() {
        binding.addButton.setOnClickListener {
            val moveToAddItemsActivity = Intent(this, AddItemsActivity::class.java)
            moveToAddItemsActivity.putExtra(AddItemsActivity.EXTRA_USER, token)
            startActivity(moveToAddItemsActivity)
        }
    }

    private fun setupCartItemsAdapter() {
        cartItemsAdapter.setOnItemClickListener { cartItem, position ->
            salesStockViewModel.deleteListItems(token, cartItem.id)
            currentSubtotal -= cartItem.price.toDouble()
            updateSubtotalText()
        }
    }

    private fun observeData() {
        salesStockViewModel.cartItems.observe(this) { cartItems ->
            //java.lang.NullPointerException: cartItems must not be null
            // cegah null menggunakan ?.let
            cartItems?.let {
                cartItemsAdapter.submitList(it)
                calculateAndSetSubtotal(it)
            }
        }

        salesStockViewModel.isLoading.observe(this) {
            binding.apply {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    cartItemsRecyclerView.visibility = View.INVISIBLE
                } else {
                    progressBar.visibility = View.GONE
                    cartItemsRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        salesStockViewModel.isHaveData.observe(this) {
            binding.apply {
                if (it) {
                    cartItemsRecyclerView.visibility = View.VISIBLE
                    tvInfo.visibility = View.GONE
                } else {
                    cartItemsRecyclerView.visibility = View.VISIBLE
                    tvInfo.visibility = View.GONE
                }
            }
        }
    }

    private fun calculateAndSetSubtotal(cartItems: List<CartItemsModel>) {
        currentSubtotal = cartItems.sumOf { it.price.toDouble() }
        updateSubtotalText()
    }

    private fun updateSubtotalText() {
        val formattedPrice = helper.formatToRupiah(currentSubtotal)
        binding.nominalText.text = formattedPrice
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

//    companion object {
//        const val EXTRA_USER = "user"
//    }
}

//            resultAddItemsActivity.launch(moveToAddItemsActivity)

//    private val resultAddItemsActivity = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val selectedCustomerId =
//                result.data?.getIntExtra(AddItemsActivity.EXTRA_CUSTOMER_ID, -1)
//            selectedCustomerId?.let { customerId ->
//                // Refresh the cart items for the selected customer
//                salesStockViewModel.getCartItemsForCustomer(user.token, customerId)
//            }
//        }
//    }

//                            customerNameAutocompleteTextView.text.clear()

//                            nameItemAutoComplete.text.clear()
//                            customerNameAutoComplete.text.clear()
//                            binding.itemCodeEditText.text.clear()
//                            binding.itemUnitEditText.text.clear()
//                            binding.itemQuantityEditText.text.clear()
//                            binding.itemCategoryEditText.text.clear()
//                            binding.itemSellingEditText.text.clear()

// Tambahkan untuk mengembalikan hasil resultAddItemsActivity ke CartActivity
//                            val resultIntent = Intent().apply {
//                                putExtra(EXTRA_CUSTOMER_ID, customerId)
//                            }
//                            setResult(Activity.RESULT_OK, resultIntent)
//                            finish()


//    private fun createAddSalesRequest(
//        customer_id: Int,
//    ): ItemTransactionsRequest {
//        return ItemTransactionsRequest(
//            customer_id,
//        )
//    }

//            updateSubtotalAfterDeletion(cartItem.price * cartItem.quantity)


// Refresh the cart items list after deletion to ensure data is up to date
//            salesStockViewModel.getCartItemsForCustomer(user.token, cartItem.customerId)
//            salesStockViewModel.cartItems.value?.let {
//                updateSubtotal(it)
//            }
//            binding.nominalText.text = cartItem.sub_total
//            binding.nominalText.text = cartItem.sub_total
//            cartItem.sub_total
//            updateSubtotal(cartItem.sub_total - cartItem.price)
//            val formattedPrice = helper.formatToRupiah(cartItem.sub_total.toDouble())
//            binding.nominalText.text = formattedPrice

//    private fun updateSubtotal(cartItems: List<ListCartItems>) {
//        val subTotal = cartItems.sumOf { it.price * it.quantity }
//        val formattedPrice = helper.formatToRupiah(subTotal.toDouble())
//        binding.nominalText.text = formattedPrice
//    }

//    private fun updateSubtotal(cartItems: List<ListCartItems>) {
//        val subTotal = cartItems.firstOrNull()?.sub_total ?: "0"
//        val formattedPrice = helper.formatToRupiah(subTotal.toDouble())
//        binding.nominalText.text = formattedPrice
//    }

//    private fun updateSubtotal(cartItems: List<ListCartItems>) {
//        var total = 0
//        for (item in cartItems) {
//            total += item.price.toInt()
//        }
//        val formattedPrice = helper.formatToRupiah(total.toDouble())
//        binding.nominalText.text = formattedPrice
//    }


//class CartActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySalesStockBinding
//
//    private lateinit var user: UserModel
//    private lateinit var cartItemsAdapter: CartItemsAdapter
//
//    private val salesStockViewModel: SalesStocksViewModel by viewModels {
//        ViewModelFactory.getInstance(this)
//    }
//
//    private val resultAddItemsActivity = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//
//            // Refresh data pada RecyclerView
//            val selectedCustomerId =
//                result.data?.getIntExtra(AddItemsActivity.EXTRA_CUSTOMER_ID, -1)
//            if (selectedCustomerId != null && selectedCustomerId != -1) {
//                salesStockViewModel.getCartItemsForCustomer(user.token, selectedCustomerId)
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySalesStockBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Mendapatkan objek user dari Intent
//        user = intent.getParcelableExtra(EXTRA_USER)!!
//
//        cartItemsAdapter = CartItemsAdapter()
//
//
//        // Inisialisasi komponen-komponen
//        showHaveDataOrNot()
//
//        setupAutoCompleteTextView()
//        setupCartItemsAdapter()
//        setupRecycleView()
//
//        showLoading()
//
//        setupAddButton()
//    }
//
//    private fun setupCartItemsAdapter() {
//        // Inisialisasi adapter untuk RecyclerView
//        cartItemsAdapter = CartItemsAdapter()
//
//        // Observe itemDelete LiveData to update items after deletion
////        salesStockViewModel.itemDelete.observe(this) { updatedItems ->
//////            cartItemsAdapter.submitList()
////        }
//        // Mengatur listener untuk item yang dihapus dari keranjang
//        cartItemsAdapter.setOnItemClickListener { cartItems, position ->
//            salesStockViewModel.deleteListItems(user.token, cartItems.id)
//            cartItemsAdapter.deleteItem(position)
////            Toast.makeText(this@CartActivity, "Item Deleted: ${cartItems.stock_name}", Toast.LENGTH_SHORT).show()
//
////            refreshCartData() // Menyegarkan data keranjang
//            refreshCartDataAfterDeletion(cartItems.id) // Menyegarkan data keranjang setelah CartItems di hapus
//
//        }
//    }
//
//    private fun setupAddButton() {
//        // Mengatur listener untuk tombol tambah item
//        binding.addButton.setOnClickListener {
//            val moveToAddItemsActivity = Intent(this, AddItemsActivity::class.java)
//            moveToAddItemsActivity.putExtra(AddItemsActivity.EXTRA_USER, user)
//            // Memulai Activity baru tetapi tidak ada mekanisme langsung untuk mendapatkan hasil atau data kembali dari aktivitas tersebut ketika selesai.
//            // startActivity(moveToAddItemsActivity)
//            // Memulai Activity baru dengan kontrak untuk mendapatkan hasil atau data kembali dari aktivitas tersebut ketika selesai.
//            resultAddItemsActivity.launch(moveToAddItemsActivity)
//        }
//    }
//
//
//    private fun setupAutoCompleteTextView() {
//        val customerNameAutocompleteTextView: AutoCompleteTextView =
//            binding.customerNameAutocompleteTextView
//
//        // Mengambil daftar pelanggan dari ViewModel
//        salesStockViewModel.getCustomers(user.token)
//        salesStockViewModel.customersList.observe(this, Observer { customers ->
//            val customerNames = customers.map { it.customer_name }
//            val customerNameAdapter =
//                ArrayAdapter(this, android.R.layout.simple_list_item_1, customerNames)
//            customerNameAutocompleteTextView.setAdapter(customerNameAdapter)
//        })
//
//        // Mengatur listener untuk pemilihan item di AutoCompleteTextView
//        customerNameAutocompleteTextView.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, _, position, _ ->
//                val selectedCustomer = salesStockViewModel.customersList.value?.find {
//                    it.customer_name == parent.getItemAtPosition(position) as String
//                }
//                selectedCustomer?.let { customer ->
//                    handleCustomerSelection(customer)
//                } ?: run {
//                    // Handle the case when selectedCustomer is null
//                    Log.e("CartActivity", "Selected customer is null 2")
//                    Toast.makeText(
//                        this@CartActivity,
//                        "Selected customer is null 2",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//            }
//    }
//
//    private fun handleCustomerSelection(customer: ListCustomersItem) {
//        // Menangani pemilihan pelanggan dari AutoCompleteTextView
////        Toast.makeText(this@CartActivity, "Customer selected: ${customer.customer_name}", Toast.LENGTH_SHORT).show()
//        Log.i(
//            "CartActivity",
//            "Customer selected: ID=${customer.id}, Name=${customer.customer_name}"
//        )
//
//        salesStockViewModel.getCartItemsForCustomer(user.token, customer.id)
//        salesStockViewModel.cartItems.observe(this) { cartItems ->
//            if (cartItems != null) {
////                cartItemsAdapter.setListSalesStock(cartItems)
//                updateSubtotal(cartItems)   // Memperbarui subtotal
//                cartItemsAdapter.submitList(cartItems)
//            } else {
//                Log.e("CartActivity", "Cart items are null")
//                Toast.makeText(
//                    this@CartActivity,
//                    "Selected customer has no items",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    private fun setupRecycleView() {
//        // Mengatur RecyclerView
//        binding.cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.cartItemsRecyclerView.setHasFixedSize(true)
//        binding.cartItemsRecyclerView.adapter = cartItemsAdapter
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//        // Panggil kembali fungsi untuk memperbarui data saat aktivitas di-restart
//        // Memperbarui data pelanggan tanpa menghapus teks yang sudah ada
//        showHaveDataOrNot()
//        showLoading()
//    }
//
//    private fun refreshCartDataAfterDeletion(deletedItemId: Int) {
//        // Mengambil kembali item keranjang dari pelanggan yang dipilih
//        val selectedCustomerName = binding.customerNameAutocompleteTextView.text.toString()
//        val selectedCustomer =
//            salesStockViewModel.customersList.value?.find { it.customer_name == selectedCustomerName }
//        selectedCustomer?.let { customer ->
//            salesStockViewModel.getCartItemsForCustomer(user.token, customer.id)
//            salesStockViewModel.cartItems.observe(this) { cartItems ->
//                if (cartItems != null) {
////                    cartItemsAdapter.setListSalesStock(cartItems)
//                    cartItemsAdapter.submitList(cartItems)
//                    updateSubtotal(cartItems)  // Memperbarui subtotal
//                } else {
//                    Log.e("CartActivity", "Cart items are null")
//                    Toast.makeText(
//                        this@CartActivity,
//                        "Selected customer has no items",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
////    private fun refreshCartData() {
////        // Menyegarkan RecyclerView dan AutoCompleteTextView
////        setupRecycleView()
////        setupAutoCompleteTextView()
////    }
//
//    private fun updateSubtotal(cartItems: List<ListCartItems>) {
//        val subTotal = cartItems.firstOrNull()?.sub_total ?: "0"
//        val formattedPrice = helper.formatToRupiah(subTotal.toDouble())
//        binding.nominalText.text = formattedPrice
//    }
//
//    private fun showHaveDataOrNot() {
//        salesStockViewModel.isHaveData.observe(this) {
//            binding.apply {
//                if (it) {
//                    cartItemsRecyclerView.visibility = View.VISIBLE
//                    tvInfo.visibility = View.GONE
//                } else {
//                    cartItemsRecyclerView.visibility = View.VISIBLE
////                    cartItemsRecyclerView.visibility = View.GONE
////                    tvInfo.visibility = View.VISIBLE
//                    tvInfo.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    private fun showLoading() {
//        salesStockViewModel.isLoading.observe(this) {
//            binding.apply {
//                if (it) {
//                    progressBar.visibility = View.VISIBLE
//                    cartItemsRecyclerView.visibility = View.INVISIBLE
//                } else {
//                    progressBar.visibility = View.GONE
//                    cartItemsRecyclerView.visibility = View.VISIBLE
//                }
//            }
//        }
//    }
//
//    companion object {
//        const val EXTRA_USER = "user"
//    }
//}
//////////////////////////////////////////////////////////////////////////////////////////

//        salesStockViewModel.isLoading.observe(this) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//            binding.cartItemsRecyclerView.visibility =
//                if (isLoading) View.INVISIBLE else View.VISIBLE
//        }
//        salesStockViewModel.isHaveData.observe(this) { isHaveData ->
//            binding.cartItemsRecyclerView.visibility = if (isHaveData) View.VISIBLE else View.GONE
//            binding.tvInfo.visibility = if (isHaveData) View.GONE else View.VISIBLE
//        }
//    }

//    private fun setupRecycleViewCartItem(){
//        val recyclerView: RecyclerView = findViewById(R.id.rv_sales)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = cartItemAdapter
//    }
//
//    private fun setListCartItem() {
////        salesStockViewModel.fetchCustomerById(user.token, 1)
//        salesStockViewModel.fetchCustomerById(user.token)
//        salesStockViewModel.customer.observe(this, { customer ->
//            customer?.let {
//                cartItemAdapter.setItems(it.cart_items)
//            }
//        })
//    }

//    private fun setTotalAllPrice() {
//        salesStockViewModel.getCartItemsForCustomer(user.token)
//        salesStockViewModel.cartItems.observe(this) { salesItems ->
//            val totalAllPrice = salesItems.firstOrNull()?.price ?: "0"
//            val formattedPrice = helper.formatToRupiah(totalAllPrice.toDouble())
//            binding.nominalText.text = formattedPrice
//
//        }
//    }

//    private fun setListCartItems() {
//        salesStockViewModel.cartItems.observe(this, Observer { cartItems ->
//            cartItems?.let {
//                cartItemsAdapter.setListSalesStock(it)
//            }
//        })
//        salesStockViewModel.showListSales(user.token)
//        salesStockViewModel.itemSalesStock.observe(this,Observer { customer ->
//            val cartItems = customer.flatMap { it.cart_items }
//            adapter.setListSalesStock(cartItems)
//        })

//        binding.saveButton.setOnClickListener {
//            salesStockViewModel.uploadSalesToHistory(user.token)
//                .observe(this) {
//                    if (it != null) {
//                        when (it) {
//                            is ResultResponse.Loading -> {
////                                    binding.progressBar.visibility = View.VISIBLE
//                            }
//                            is ResultResponse.Success -> {
////                                    binding.progressBar.visibility = View.GONE
//                                helper.showToast(
//                                    this,
//                                    getString(R.string.upload_success)
//                                )
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_success))
//                                    setMessage(getString(R.string.data_success))
//                                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
////                                            binding.progressBar.visibility = View.GONE
//                                        // Perbarui data dan antarmuka pengguna setelah berhasil disimpan dengan cara memanggil kembali fun karena data yang berhasil tersimpan akan menghilangkan list
//                                        setListSales()
//                                    }
//                                    create()
//                                    show()
//                                }
//                            }
//                            is ResultResponse.Error -> {
////                                    binding.progressBar.visibility = View.GONE
//                                AlertDialog.Builder(this).apply {
//                                    setTitle(getString(R.string.upload_failed))
//                                    setMessage(getString(R.string.upload_failed) + ", ${it.error}")
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
//}

//        binding.addButton.setOnClickListener {
//            val moveToPurchaseStocksActivity = Intent(this, AddItemsActivity::class.java)
//            moveToPurchaseStocksActivity.putExtra(AddItemsActivity.EXTRA_USER, user)
//            startActivity(moveToPurchaseStocksActivity)
//        }

//        salesStockViewModel.cartItems.observe(this) { cartItems ->
//            cartItemsAdapter.setListSalesStock(cartItems)
//        }

//        customerNameAutocompleteTextView.setOnItemClickListener { parent, view, position, id ->
//            val selectedCustomerName = parent.getItemAtPosition(position) as String
//            val selectedCustomer = salesStockViewModel.customersList.value?.find { it.customer_Name == selectedCustomerName }
//            selectedCustomer?.let {
//                salesStockViewModel.getCartItemsForCustomer(user.token, it.id)
//                salesStockViewModel.cartItems.observe(this, { cartItems ->
//                    cartItems?.let {
//                        cartItemsAdapter.setListSalesStock(it)
////                recycler_view.visibility = View.VISIBLE
//                    }
//                })
//            } ?: run {
//                Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show()
//            }
//        }


//    private fun setListSalesCustomers() {
//        val spinnerCustomers: Spinner = findViewById(R.id.customerNameSpinner)
//
//        var customerId: Int
//
//        salesStockViewModel.getCustomers(user.token)
//        salesStockViewModel.customersList.observe(this, Observer { customers ->
//            val adapter = ArrayAdapter(
//                this@CartActivity,
//                R.layout.support_simple_spinner_dropdown_item,
//                customers.map { it.customer_Name }
//            )
//            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
//            spinnerCustomers.adapter = adapter
//        })
//        // Tangani pemilihan item dari customerSpinner
//        spinnerCustomers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedCustomer = salesStockViewModel.customersList.value?.find { it.customer_Name == parent.getItemAtPosition(position) as String }

//                val selectedCustomer = salesStockViewModel.customersList.value?.get(position)
//                val selectedCustomer = salesStockViewModel.cartItems.value?.get(position)

//                selectedCustomer?.let {
//                    customerId = it.id
//                    salesStockViewModel.getCartItemsForCustomer(user.token, customerId)
//                    Toast.makeText(this@CartActivity, "Customer selected: ${selectedCustomer.customer_Name}", Toast.LENGTH_SHORT).show()
//                    Log.i("CartActivity", "Customer selected: ID=${selectedCustomer.id}, Name=${selectedCustomer.customer_Name}")

//                    salesStockViewModel.cartItems.observe(this, Observer { cartItems ->
//                        cartItems.let {
//                            cartItemsAdapter.setListSalesStock(cartItems)
//                        }
//                    })
//                }

// Notifikasi atau log saat customer dipilih
//                Toast.makeText(this@CartActivity, "Customer selected: ${selectedCustomer?.customer_Name}", Toast.LENGTH_SHORT).show()
//                Log.i("CartActivity", "Customer selected: ID=${selectedCustomer?.id}, Name=${selectedCustomer?.customer_Name}")
//                Toast.makeText(this@CartActivity, "Customer selected: ${selectedCustomer?.customerName}", Toast.LENGTH_SHORT).show()
//                Log.i("CartActivity", "Customer selected: ID=${selectedCustomer?.id}, Name=${selectedCustomer?.customerName}")
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Tindakan jika tidak ada customer yang dipilih (opsional)
//            }
//        }
////        salesStockViewModel.cartItems.observe(this, Observer { cartItems ->
//            cartItems?.let {
//                cartItemsAdapter.setListSalesStock(it)
//                Log.d("CartActivity", "Cart items updated: ${it.size} items")
//            }
//        })
//        salesStockViewModel.cartItems.observe(this, Observer { cartItems ->
//            cartItems?.let {
//                Log.d("CartActivity", "Cart items: $it")
//                cartItemsAdapter.setListSalesStock(it)
//                if (it.isEmpty()) {
////                    showEmptyListNotification()
//                    Log.e("CartActivity", "Cart items are empty")
//
//                } else {
////                    Log.d("CartActivity", "Cart items updated: ${it.size} items")
//                    Log.d("CartActivity", "Cart items updated: ${it.size} items")
//
//                }
//            } ?: run {
////                showEmptyListNotification()
//                Log.e("CartActivity", "Cart items are null")
//            }
//        })
//    }

//    private fun observeViewModel() {
//        salesStockViewModel.getCustomers(user.token)
//        salesStockViewModel.customersList.observe(this, Observer { customers ->
//            val adapter = ArrayAdapter(
//                this@CartActivity,
//                R.layout.support_simple_spinner_dropdown_item,
//                customers.map { it.customer_Name }
//            )
//            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
//            spinnerCustomers.adapter = adapter
//        })

//        salesStockViewModel.cartItems.observe(this, Observer { cartItems ->
//            cartItems?.let {
//                Log.d("CartActivity", "Cart items: $it")
//                cartItemsAdapter.setListSalesStock(it)
//                if (it.isEmpty()) {
//                    showEmptyListNotification()
//                    Log.e("CartActivity", "Cart items are empty")
//
//                } else {
//                    Log.d("CartActivity", "Cart items updated: ${it.size} items")
//                    Log.d("CartActivity", "Cart items updated: ${it.size} items")
//
//                }
//            } ?: run {
//                showEmptyListNotification()
//                Log.e("CartActivity", "Cart items are null")
//            }
//        })
//        salesStockViewModel.cartItems.observe(this, Observer { cartItems ->
//            cartItems?.let {
//                cartItemsAdapter.setListSalesStock(it)
//                Log.d("CartActivity", "Cart items updated: ${it.size} items")
//            }
//        })
//    }

//    private fun showEmptyListNotification() {
//        Snackbar.make(binding.root, "No items available in the cart", Snackbar.LENGTH_LONG).show()
//    }

//    private fun deleteListSales(cartItems: ListSalesStocksItem) {
//        salesStockViewModel.deleteListSales(user.token, cartItems.id)
//    }


//    fun onAddField(view: View) {
//        val inflater =
//            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val rowView: View = inflater.inflate(R.layout.field_sales, null)
//        binding.parentLinearLayout.addView(rowView, binding.parentLinearLayout.childCount - 1)
//    }
//    fun onDelete(view: View) {
//        binding.parentLinearLayout.removeView(view.parent as View)
//    }

//        binding.parentLinearLayout

//        // Setel OnItemClickListener pada adapter
//        adapter.setOnItemClickListener(object : SalesStockAdapter.OnItemClickListener {
//            override fun onItemClick(cartItems: ListSalesStocksItem) {
//                // Tangani klik item (misalnya, buka detail item)
//            }
//
//            override fun onDeleteClick(cartItems: ListSalesStocksItem, position: Int) {
//                // Panggil fungsi delete dari ViewModel
//                salesStockViewModel.deleteListSales("Bearer <TOKEN>", cartItems.id).observe(this@SalesStockActivity) { response ->
//                    if (response.error) {
//                        // Tampilkan pesan kesalahan jika penghapusan gagal
//                        Toast.makeText(this@SalesStockActivity, response.message, Toast.LENGTH_SHORT).show()
//                    } else {
//                        // Hapus item dari adapter jika penghapusan berhasil
//                        salesStockViewModel.removeItem(position)
//                    }
//                }
//            }
//        })