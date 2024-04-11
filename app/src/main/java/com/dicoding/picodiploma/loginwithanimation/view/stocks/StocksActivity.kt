package com.dicoding.picodiploma.loginwithanimation.view.stocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStocksBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.google.android.material.snackbar.Snackbar

class StocksActivity : AppCompatActivity() {

    private val viewModel by viewModels<StocksViewModel>()

    private lateinit var user: UserModel
//    private lateinit var stocks: StocksModel
    private lateinit var adapter: StocksAdapter

    private var _binding: ActivityStocksBinding? = null
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStocksBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        user = intent.getParcelableExtra(EXTRA_USER)!!

        setListStocks()
        adapter = StocksAdapter()

        showHaveDataOrNot()
        setupRecycleView()
        showSnackBar()
        showLoading()
    }

    private fun showHaveDataOrNot(){
        viewModel.isHaveData.observe(this){ haveData ->
            binding?.apply {
                if (haveData) {
                    rvStock.visibility = View.VISIBLE
                    tvInfo.visibility = View.GONE
                } else {
                    //penyebab biang keroknya
//                    rvStock.visibility = View.GONE
//                    tvInfo.visibility = View.VISIBLE
                }
            }
            Log.d("showHaveDataOrNot", "Data status: $haveData")
        }
    }

    private fun setListStocks() {
        viewModel.showListStocks(user.token)
        viewModel.itemStory.observe(this) {
            adapter.setListStocks(it)
        }
    }

    override fun onResume() {
        super.onResume()
        setListStocks()
    }

    private fun showSnackBar() {
        viewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(
                    findViewById(R.id.rv_stock),
                    snackBarText,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this) {
            binding?.apply {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    rvStock.visibility = View.INVISIBLE
                } else {
                    progressBar.visibility = View.GONE
                    rvStock.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupRecycleView(){
        binding?.rvStock?.layoutManager = LinearLayoutManager(this)
        binding?.rvStock?.setHasFixedSize(true)
        binding?.rvStock?.adapter = adapter
    }

    companion object {
        const val EXTRA_USER = "user"
    }

}