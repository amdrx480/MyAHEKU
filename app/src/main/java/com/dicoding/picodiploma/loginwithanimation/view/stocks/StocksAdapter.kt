package com.dicoding.picodiploma.loginwithanimation.view.stocks

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowPhotoBinding
import com.dicoding.picodiploma.loginwithanimation.service.data.stocks.ListStocksItem
import com.dicoding.picodiploma.loginwithanimation.view.stocks.detail.DetailStocksActivity

class StocksAdapter : PagingDataAdapter<ListStocksItem, StocksAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class ViewHolder(private var binding: ItemRowPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stocks: ListStocksItem) {
            with(binding) {
                Glide.with(imgViewStock)
//                    .load(story.photoUrl) // URL Avatar
//                    .placeholder(R.drawable.ic_place_default_holder)
//                    .error(R.drawable.ic_broken_image)
//                    .into(imgViewPhoto)
                val combinedText = "${stocks.stock_Name} - ${stocks.stock_Code}"
                tvStockName.text = combinedText
                tvStockCategory.text = stocks.category_Name
                tvUnits.text = stocks.units_Name
                tvStockTotal.text = stocks.stock_total.toString()
//                tvStockRoll.text = stocks.stock_Roll.toString()
//                tvStockMeter.text = stocks.stock_Meter.toString()

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    tvCreatedATime.text =
//                        binding.root.resources.getString(R.string.created_add, helper.dateFormat(stocks.created_at, TimeZone.getDefault().id))
//                }
                cardPhoto.setOnClickListener {
                    val intent = Intent(it.context, DetailStocksActivity::class.java)
                    intent.putExtra(DetailStocksActivity.EXTRA_DETAIL, stocks)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStocksItem>() {
            override fun areItemsTheSame(
                oldItem: ListStocksItem,
                newItem: ListStocksItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStocksItem,
                newItem: ListStocksItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}