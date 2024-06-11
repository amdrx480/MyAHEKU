package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.detail

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity

class DetailStocksViewModel: ViewModel() {
    lateinit var detailItem: StocksEntity

    fun setDetailStory(detail: StocksEntity) : StocksEntity {
        detailItem = detail
        return detailItem
    }

}