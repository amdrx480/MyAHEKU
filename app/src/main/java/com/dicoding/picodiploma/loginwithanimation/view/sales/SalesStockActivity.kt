package com.dicoding.picodiploma.loginwithanimation.view.sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySalesStockBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.stocks.StocksActivity
import com.dicoding.picodiploma.loginwithanimation.view.stocks.StocksAdapter

class SalesStockActivity : AppCompatActivity() {

    private var _binding: ActivitySalesStockBinding? = null
    private val binding get() = _binding

    private lateinit var user: UserModel
    private lateinit var adapter: SalesStockAdapter

    private val salesStockViewModel by viewModels<SalesStocksViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySalesStockBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        user = intent.getParcelableExtra(EXTRA_USER)!!
        adapter = SalesStockAdapter()

        setupRecycleView()
        setListSalesAdapter()
    }

    private fun setupRecycleView(){
        binding?.rvSalesStock?.layoutManager = LinearLayoutManager(this)
        binding?.rvSalesStock?.setHasFixedSize(true)
        binding?.rvSalesStock?.adapter = adapter
    }

    private fun setListSalesAdapter() {
        salesStockViewModel.showListSalesStock(user.token)
        salesStockViewModel.itemSalesStock.observe(this){
            adapter.setListSalesStock(it)

        }
    }

    companion object {
        const val EXTRA_USER = "user"
    }
}
