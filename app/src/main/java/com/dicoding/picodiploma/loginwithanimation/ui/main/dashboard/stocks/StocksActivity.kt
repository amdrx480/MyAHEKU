package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStocksBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.DashboardFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StocksActivity : AppCompatActivity() {

    private val stocksViewModel: StocksViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    private lateinit var adapter: StocksAdapter

    private var _binding: ActivityStocksBinding? = null
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStocksBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        adapter = StocksAdapter()

        setupAdapter()
        setupObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupAdapter() {
        adapter = StocksAdapter()
        binding?.rvStocks?.adapter = adapter.withLoadStateHeaderAndFooter(
            footer = LoadingStateAdapter { adapter.retry() },
            header = LoadingStateAdapter { adapter.retry() }
        )
        binding?.rvStocks?.layoutManager = LinearLayoutManager(this)
        binding?.rvStocks?.setHasFixedSize(true)

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect {
                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
            if (adapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
            else binding?.tvInfo?.root?.visibility = View.GONE
        }

        stocksViewModel.getStocks().observe(this) {
        adapter.submitData(lifecycle, it)
        }
    }

    private fun setupObservers() {
        stocksViewModel.authToken.observe(this) { token ->
            // Log the token value
            Log.d("StocksActivity", "Auth Token: $token")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

}

//    private lateinit var user: UserModel
//        private lateinit var user: StocksEntity