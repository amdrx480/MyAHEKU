package com.dicoding.picodiploma.loginwithanimation.view.Purchase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val purchaseRequest = PurchaseRequest(
                id = "id_value", // Isi dengan nilai yang sesuai
                stock_Location = "stock_location_value", // Isi dengan nilai yang sesuai
                binding.itemCodeEditText.text.toString(), // Isi dengan nilai yang sesuai
                stock_Category = "stock_category_value", // Isi dengan nilai yang sesuai
                binding.itemNameEditText.text.toString(), // Isi dengan nilai yang sesuai
                stock_Pcs = 9, // Isi dengan nilai yang sesuai
                stock_Pack = 9, // Isi dengan nilai yang sesuai
                stock_Roll = 9, // Isi dengan nilai yang sesuai
                stock_Meter = 9 // Isi dengan nilai yang sesuai
            )
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
        }
    }

//    private fun setupAction() {
//        binding.saveButton.setOnClickListener {
//            val purchaseRequest = PurchaseRequest(
//                id = "id_value", // Isi dengan nilai yang sesuai
//                stock_Location = "stock_location_value", // Isi dengan nilai yang sesuai
//                binding.itemCodeEditText.text.toString(), // Isi dengan nilai yang sesuai
//                stock_Category = "stock_category_value", // Isi dengan nilai yang sesuai
//                binding.itemNameEditText.text.toString(), // Isi dengan nilai yang sesuai
//                stock_Pcs = 9, // Isi dengan nilai yang sesuai
//                stock_Pack = 9, // Isi dengan nilai yang sesuai
//                stock_Roll = 9, // Isi dengan nilai yang sesuai
//                stock_Meter = 9 // Isi dengan nilai yang sesuai
//            )
////            val itemName = binding.itemNameEditText.text.toString()
////            purchaseStocksViewModel.purchaseStocks(purchaseRequest, object : helper.ApiCallBackString {
//              purchaseStocksViewModel.purchaseStocks(user.token, purchaseRequest, object : helper.ApiCallBackString {
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
}