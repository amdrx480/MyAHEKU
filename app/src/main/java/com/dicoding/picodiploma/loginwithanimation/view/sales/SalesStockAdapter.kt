package com.dicoding.picodiploma.loginwithanimation.view.sales

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowPhotoBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowSalesBinding
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.ListSalesStocksItem
import com.dicoding.picodiploma.loginwithanimation.utils.DiffCallBack
import java.util.*

class SalesStockAdapter : RecyclerView.Adapter<SalesStockAdapter.ViewHolder>() {

    private val listSalesStock = ArrayList<ListSalesStocksItem>()

    fun setListSalesStock(itemStory: List<ListSalesStocksItem>) {
        val diffCallback = DiffCallBack(this.listSalesStock, itemStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listSalesStock.clear()
        this.listSalesStock.addAll(itemStory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowSalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listSalesStock[position])
    }

    override fun getItemCount() = listSalesStock.size

    inner class ViewHolder(private var binding: ItemRowSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(salesItem: ListSalesStocksItem) {
            with(binding) {
                Log.d(TAG, "Binding stocks: $salesItem")
//                Glide.with(imgVPhoto)
//                    .load(story.photoUrl) // URL Avatar
//                    .placeholder(R.drawable.ic_place_default_holder)
//                    .error(R.drawable.ic_broken_image)
//                    .into(imgVPhoto)
                tvStockName.text = salesItem.stock_name
                tvStockQuantity.text = salesItem.quantity

//                tvDescription.text = story.description
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    tvCreatedATime.text =
//                        binding.root.resources.getString(R.string.created_add, helper.dateFormat(story.createdAt, TimeZone.getDefault().id))
//                }
//                cardPhoto.setOnClickListener {
//                    val intent = Intent(it.context, DetailStoryAppActivity::class.java)
//                    intent.putExtra(DetailStoryAppActivity.EXTRA_STORY, story)
//                    it.context.startActivity(intent)
//                }
            }
        }
    }
    companion object {
        private const val TAG = "StockAdapter_TAG"
    }
}