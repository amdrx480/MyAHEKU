package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchaseorder

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders.PurchaseOrderModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemPurchaseOrderBinding
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.detail.DetailStocksActivity
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.utils.helper.animateTimerView
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PurchaseOrderAdapter : ListAdapter<PurchaseOrderModel, PurchaseOrderAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((PurchaseOrderModel, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (PurchaseOrderModel, Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPurchaseOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            Log.d("PurchaseOrderAdapter", "Item binded at position: $position")
        } else {
            Log.e("PurchaseOrderAdapter", "Error binding item at position: $position")
        }
    }

    inner class ViewHolder(private var binding: ItemPurchaseOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(purchaseOrder: PurchaseOrderModel) {
            with(binding) {
                tvCustomerName.text = purchaseOrder.customerName
                tvReminderTime.text = helper.formatTime(purchaseOrder.reminderTime)

                val reminderTime = OffsetDateTime.parse(purchaseOrder.reminderTime)
                val now = OffsetDateTime.now()

                // Menentukan warna berdasarkan perbandingan tanggal
                val timerColor = when {
                    reminderTime.toLocalDate().isBefore(now.toLocalDate()) -> R.color.red // Sudah lewat (Merah)
                    reminderTime.toLocalDate().isEqual(now.toLocalDate()) -> R.color.yellow // Hari ini (Kuning)
                    reminderTime.toLocalDate().isAfter(now.toLocalDate().plusDays(2)) -> R.color.green // Lebih dari 2 hari (Hijau)
                    else -> R.color.yellow // Kurang dari 2 hari (Kuning)
                }

                // Setel warna latar belakang dan animasikan view timer
                timerView.setBackgroundColor(ContextCompat.getColor(root.context, timerColor))
                animateTimerView(timerView)

                // Log untuk memverifikasi pengikatan data
                Log.d("PurchaseOrderAdapter", "Binding data: $purchaseOrder")

                cardItemPurchaseOrder.setOnClickListener{
                    val intent = Intent(it.context, DetailPurchaseOrderActivity::class.java)
                    intent.putExtra(DetailPurchaseOrderActivity.EXTRA_DETAIL, purchaseOrder)
                    it.context.startActivity(intent)
                }
            }
        }
    }
    
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PurchaseOrderModel>() {
            override fun areItemsTheSame(
                oldItem: PurchaseOrderModel,
                newItem: PurchaseOrderModel,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PurchaseOrderModel,
                newItem: PurchaseOrderModel,
            ): Boolean {
                return oldItem == newItem
            }
        }

        private const val TAG = "PurchaseOrderAdapter"
    }
}

//                tvStockName.text = purchaseOrder.stockName
//                tvStockCode.text = purchaseOrder.stockCode
//                tvUnitName.text = purchaseOrder.unitName
//                tvCategoryName.text = purchaseOrder.categoryName
//                tvQuantity.text = purchaseOrder.quantity.toString()
//                tvPrice.text = helper.formatToRupiah(purchaseOrder.price.toDouble())
//
//                btnDelete.setOnClickListener {
//                    val newList = currentList.toMutableList()
//                    val position = bindingAdapterPosition
//
//                    if (position != RecyclerView.NO_POSITION) {
//                        onItemClickListener?.invoke(newList[position], position)
//                        newList.removeAt(position)
//                        submitList(newList)
//                    }
//                }