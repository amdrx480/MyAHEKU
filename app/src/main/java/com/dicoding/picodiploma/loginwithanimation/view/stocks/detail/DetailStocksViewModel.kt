package com.dicoding.picodiploma.loginwithanimation.view.stocks.detail

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.service.data.stocks.ListStocksItem

class DetailStocksViewModel: ViewModel() {
    lateinit var detailItem: ListStocksItem

    fun setDetailStory(detail: ListStocksItem) : ListStocksItem{
        detailItem = detail
        return detailItem
    }

}