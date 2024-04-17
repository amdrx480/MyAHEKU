package com.dicoding.picodiploma.loginwithanimation.view.stocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStocksBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StocksActivity : AppCompatActivity() {

    private val viewModel: StocksViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var user: UserModel
    private lateinit var adapter: StocksAdapter

    private var _binding: ActivityStocksBinding? = null
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStocksBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        user = intent.getParcelableExtra(EXTRA_USER)!!
        adapter = StocksAdapter()

        setupAdapter()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupAdapter(){
        adapter = StocksAdapter()
        binding?.rvStocks?.adapter = adapter.withLoadStateHeaderAndFooter(
            footer = LoadingStateStocksAdapter { adapter.retry() },
            header = LoadingStateStocksAdapter { adapter.retry() }
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

        viewModel.getStocks(user.token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_USER = "user"
    }

}