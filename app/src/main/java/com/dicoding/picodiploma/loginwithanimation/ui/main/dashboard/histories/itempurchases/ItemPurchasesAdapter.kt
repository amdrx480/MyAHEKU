package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itempurchases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemPurchasesItemBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import java.text.NumberFormat
import java.util.*

class ItemPurchasesAdapter : PagingDataAdapter<PurchasesEntity, ItemPurchasesAdapter.ViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPurchasesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class ViewHolder(private var binding: ItemPurchasesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(purchases: PurchasesEntity) {
            with(binding) {
                Glide.with(imgViewStock)
//                    .load(story.photoUrl) // URL Avatar
//                    .placeholder(R.drawable.ic_place_default_holder)
//                    .error(R.drawable.ic_broken_image)
//                    .into(imgViewPhoto)
                tvIndex.text = purchases.id.toString()
                val combinedText = "${purchases.stockName} - ${purchases.stockCode}"
                tvStockName.text = combinedText
                tvVendorName.text = purchases.vendorName
                tvStockCategory.text = purchases.categoryName
                tvUnits.text = purchases.unitName
                tvStockTotal.text = purchases.quantity.toString()
                // Format selling price to IDR
                val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                tvPurchasePrice.text = format.format(purchases.purchasePrice)
                tvSellingPrice.text = format.format(purchases.sellingPrice)
//                tvStockTotal.text = purchases.stock_total.toString()
//                tvStockRoll.text = purchases.stock_Roll.toString()
//                tvStockMeter.text = purchases.stock_Meter.toString()

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    tvCreatedATime.text =
//                        binding.root.resources.getString(R.string.created_add, helper.dateFormat(purchases.created_at, TimeZone.getDefault().id))
//                }
                cardItemPurchases.setOnClickListener {
//                    val intent = Intent(it.context, DetailStocksActivity::class.java)
//                    intent.putExtra(DetailStocksActivity.EXTRA_DETAIL, purchases)
//                    it.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PurchasesEntity>() {
            override fun areItemsTheSame(
                oldItem: PurchasesEntity,
                newItem: PurchasesEntity,
            ): Boolean {
                return oldItem == newItem
//                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(
                oldItem: PurchasesEntity,
                newItem: PurchasesEntity,
            ): Boolean {
                return oldItem.id == newItem.id
//                return oldItem == newItem
            }
        }
    }
}