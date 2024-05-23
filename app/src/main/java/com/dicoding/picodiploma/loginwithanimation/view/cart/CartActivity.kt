package com.dicoding.picodiploma.loginwithanimation.view.cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySalesStockBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.cart.items.AddItemsActivity
import com.google.android.material.snackbar.Snackbar

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesStockBinding

    private lateinit var user: UserModel
    private lateinit var cartItemsAdapter: CartItemsAdapter

    private val salesStockViewModel: SalesStocksViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(EXTRA_USER)!!

        cartItemsAdapter = CartItemsAdapter()

        binding.addButton.setOnClickListener {
            val moveToPurchaseStocksActivity = Intent(this, AddItemsActivity::class.java)
            moveToPurchaseStocksActivity.putExtra(AddItemsActivity.EXTRA_USER, user)
            startActivity(moveToPurchaseStocksActivity)
        }

//        showHaveDataOrNot()
//        showLoading()
        setupRecycleView()
        setupAutoCompleteTextView()
        setDeleteSales()
//        setTotalAllPrice()
    }


    private fun setupAutoCompleteTextView() {
        val customerNameAutocompleteTextView: AutoCompleteTextView = binding.customerNameAutocompleteTextView
//        var customerId: Int? = null

        salesStockViewModel.getCustomers(user.token)
        salesStockViewModel.customersList.observe(this, Observer { customers ->
            val customerNames = customers.map { it.customer_name }
            val customerNameAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, customerNames)
            customerNameAutocompleteTextView.setAdapter(customerNameAdapter)
        })

        // Penanganan pemilihan pelanggan
        customerNameAutocompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedCustomer = salesStockViewModel.customersList.value?.find { it.customer_name == parent.getItemAtPosition(position) as String }
            selectedCustomer?.let {
//                customerId = it.id
                it.id
                Toast.makeText(this@CartActivity, "Customer selected: ${it.customer_name}", Toast.LENGTH_LONG).show()
                Log.i("AddItemsActivity", "Customer selected: ID=${it.id}, Name=${it.customer_name}")
                Log.e("AddItemsActivity", "Customer selected: ID=${it.id}, Name=${it.customer_name}")

                salesStockViewModel.getCartItemsForCustomer(user.token, it.id)
                salesStockViewModel.cartItems.observe(this){ cartItems ->
                    // cartItemsAdapter.setListSalesStock(it)
//                    cartItemsAdapter.setListSalesStock(cartItems)
                    if (cartItems != null) {
                        cartItemsAdapter.setListSalesStock(cartItems)
                        val subTotal = cartItems.firstOrNull()?.sub_total ?: "0"
                        val formattedPrice = helper.formatToRupiah(subTotal.toDouble())
                        binding.nominalText.text = formattedPrice
                    } else {
                        // Handle the case when cartItems is null
                        Log.e("CartActivity", "Cart items are null 1")
                        Toast.makeText(this@CartActivity, "Selected customer is null 1", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                // Handle the case when selectedCustomer is null
                Log.e("CartActivity", "Selected customer is null 2")
                Toast.makeText(this@CartActivity, "Selected customer is null 2", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setupRecycleView(){
        binding.cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartItemsRecyclerView.setHasFixedSize(true)
        binding.cartItemsRecyclerView.adapter = cartItemsAdapter
    }

    override fun onRestart() {
        super.onRestart()
        // Panggil kembali fungsi untuk memperbarui data saat aktivitas di-restart
        setupRecycleView()
        setupAutoCompleteTextView()
        setDeleteSales()
    }

    private fun setDeleteSales() {
        cartItemsAdapter.setOnItemClickListener { salesItem, _ ->
            salesStockViewModel.deleteListItems(user.token, salesItem.id)
            Toast.makeText(this@CartActivity, "Item Deleted: ${salesItem.stock_name}", Toast.LENGTH_SHORT).show()

//            setTotalAllPrice()
        }
    }

    companion object {
        const val EXTRA_USER = "user"
    }


//    private fun showHaveDataOrNot(){
//        salesStockViewModel.isHaveData.observe(this){
//            binding.apply {
//                if (it) {
//                    cartItemsRecyclerView.visibility = View.VISIBLE
//                    tvInfo.visibility = View.GONE
//                } else {
//                    cartItemsRecyclerView.visibility = View.GONE
//                    tvInfo.visibility = View.VISIBLE
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
}

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

//    private fun deleteListSales(salesItem: ListSalesStocksItem) {
//        salesStockViewModel.deleteListSales(user.token, salesItem.id)
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
//            override fun onItemClick(salesItem: ListSalesStocksItem) {
//                // Tangani klik item (misalnya, buka detail item)
//            }
//
//            override fun onDeleteClick(salesItem: ListSalesStocksItem, position: Int) {
//                // Panggil fungsi delete dari ViewModel
//                salesStockViewModel.deleteListSales("Bearer <TOKEN>", salesItem.id).observe(this@SalesStockActivity) { response ->
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