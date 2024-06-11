package com.dicoding.picodiploma.loginwithanimation.utils

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.CartItemsModel

class DiffCallBack(
    private val mOldList: List<CartItemsModel>,
    private val mNewList: List<CartItemsModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = mOldList.size

    override fun getNewListSize() = mNewList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        mOldList[oldItemPosition].id == mNewList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldList[oldItemPosition]
        val newEmployee = mNewList[newItemPosition]
        return oldEmployee.id == newEmployee.id
    }
}