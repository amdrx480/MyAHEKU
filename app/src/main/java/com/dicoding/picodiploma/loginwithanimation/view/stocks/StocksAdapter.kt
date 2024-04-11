package com.dicoding.picodiploma.loginwithanimation.view.stocks

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowPhotoBinding
import com.dicoding.picodiploma.loginwithanimation.helper.DiffCallBack
import com.dicoding.picodiploma.loginwithanimation.helper.helper
import com.dicoding.picodiploma.loginwithanimation.model.stocks.AllStocksResponse
import com.dicoding.picodiploma.loginwithanimation.model.stocks.ListStocksItem
import java.util.*

class StocksAdapter : RecyclerView.Adapter<StocksAdapter.ViewHolder>() {

    private val listStocks = ArrayList<ListStocksItem>()

    fun setListStocks(itemStocks: List<ListStocksItem>) {
        val diffCallback = DiffCallBack(this.listStocks, itemStocks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listStocks.clear()
        this.listStocks.addAll(itemStocks)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listStocks[position])
    }

    override fun getItemCount() = listStocks.size

    inner class ViewHolder(private var binding: ItemRowPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stocks: ListStocksItem) {
            with(binding) {
                Log.d(TAG, "Binding stocks: $stocks")

                Glide.with(imgViewStock)
//                    .load(story.photoUrl) // URL Avatar
//                    .placeholder(R.drawable.ic_place_default_holder)
//                    .error(R.drawable.ic_broken_image)
//                    .into(imgViewPhoto)
                val combinedText = "${stocks.stock_Name} - ${stocks.stock_Code}"
                tvStockName.text = combinedText
                tvStockCategory.text = stocks.stock_Category
                tvStockPcs.text = stocks.stock_Pcs.toString()
//                tvStockPack.text = stocks.stock_Pack.toString()
                tvStockRoll.text = stocks.stock_Roll.toString()
                tvStockMeter.text = stocks.stock_Meter.toString()

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    tvCreatedATime.text =
//                        binding.root.resources.getString(R.string.created_add, helper.dateFormat(stocks.created_at, TimeZone.getDefault().id))
//                }
//                cardPhoto.setOnClickListener {
//                    val intent = Intent(it.context, DetailStoryAppActivity::class.java)
//                    intent.putExtra(DetailStoryAppActivity.EXTRA_STORY, stocks)
//                    it.context.startActivity(intent)
//                }
            }
        }
    }

    companion object {
        private const val TAG = "StockAdapter_TAG"
    }
}