package com.dicoding.picodiploma.loginwithanimation.utils

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.ListSalesStocksItem
import com.dicoding.picodiploma.loginwithanimation.service.data.stocks.ListStocksItem

class DiffCallBack(
    private val mOldFavList: List<ListSalesStocksItem>,
    private val mNewFavList: List<ListSalesStocksItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = mOldFavList.size

    override fun getNewListSize() = mNewFavList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        mOldFavList[oldItemPosition].id == mNewFavList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldFavList[oldItemPosition]
        val newEmployee = mNewFavList[newItemPosition]
        return oldEmployee.id == newEmployee.id
    }
}