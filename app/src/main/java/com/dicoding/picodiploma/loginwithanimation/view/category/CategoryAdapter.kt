package com.dicoding.picodiploma.loginwithanimation.view.category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowCategoryBinding
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowSalesBinding
import com.dicoding.picodiploma.loginwithanimation.model.category.CategoryModel
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.ListSalesStocksItem
import com.dicoding.picodiploma.loginwithanimation.utils.DiffCallBack
import java.util.ArrayList

class CategoryAdapter(var context: Context, val list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.CateogryViewHolder>() {

    private val listSalesStock = ArrayList<ListSalesStocksItem>()

    fun setListCategory(itemStory: List<ListSalesStocksItem>) {
        val diffCallback = DiffCallBack(this.listSalesStock, itemStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listSalesStock.clear()
        this.listSalesStock.addAll(itemStory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CateogryViewHolder {
        val binding = ItemRowCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CateogryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CateogryViewHolder, position: Int) {
        holder.bind(listSalesStock[position])
    }

    override fun getItemCount() = listSalesStock.size

    inner class CateogryViewHolder(private var binding: ItemRowCategoryBinding) :
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