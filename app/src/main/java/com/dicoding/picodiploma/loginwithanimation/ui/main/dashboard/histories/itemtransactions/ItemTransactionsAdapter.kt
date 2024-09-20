package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itemtransactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemTransactionsItemBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity
import java.text.NumberFormat
import java.util.*

class ItemTransactionsAdapter :
    PagingDataAdapter<ItemTransactionsEntity, ItemTransactionsAdapter.ViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTransactionsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class ViewHolder(private var binding: ItemTransactionsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemTransactions: ItemTransactionsEntity) {
            with(binding) {
                Glide.with(imgViewStock)
//                    .load(story.photoUrl) // URL Avatar
//                    .placeholder(R.drawable.ic_place_default_holder)
//                    .error(R.drawable.ic_broken_image)
//                    .into(imgViewPhoto)
                tvIndex.text = itemTransactions.id.toString()
                val combinedText = "${itemTransactions.stockName} - ${itemTransactions.stockCode}"
                tvStockName.text = combinedText
                tvCustomerName.text = itemTransactions.customerName
                tvStockCategory.text = itemTransactions.categoryName
                tvUnits.text = itemTransactions.unitName
                tvStockTotal.text = itemTransactions.quantity.toString()

                // Format selling price to IDR
                val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                tvTransactionPrice.text = format.format(itemTransactions.price)

//                tvTransactionPrice.text = format.format(itemTransactions.subTotal)
//                tvStockTotal.text = itemTransactions.stock_total.toString()
//                tvStockRoll.text = itemTransactions.stock_Roll.toString()
//                tvStockMeter.text = itemTransactions.stock_Meter.toString()

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    tvCreatedATime.text =
//                        binding.root.resources.getString(R.string.created_add, helper.dateFormat(itemTransactions.created_at, TimeZone.getDefault().id))
//                }
                cardItemTransactions.setOnClickListener {
//                    val intent = Intent(it.context, DetailStocksActivity::class.java)
//                    intent.putExtra(DetailStocksActivity.EXTRA_DETAIL, itemTransactions)
//                    it.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemTransactionsEntity>() {
            override fun areItemsTheSame(
                oldItem: ItemTransactionsEntity,
                newItem: ItemTransactionsEntity,
            ): Boolean {
                return oldItem == newItem
//                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ItemTransactionsEntity,
                newItem: ItemTransactionsEntity,
            ): Boolean {
//                return oldItem == newItem
                return oldItem.id == newItem.id

            }
        }
    }
}

//                return oldItem.stockName == newItem.stockName &&
//                        oldItem.categoryName == newItem.categoryName &&
//                        oldItem.unitName == newItem.unitName &&
//                        oldItem.quantity == newItem.quantity

//                return oldItem.stockName == newItem.stockName &&
//                        oldItem.categoryName == newItem.categoryName &&
//                        oldItem.unitName == newItem.unitName &&
//                        oldItem.quantity == newItem.quantity