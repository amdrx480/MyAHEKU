package com.dicoding.picodiploma.loginwithanimation.view.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemCartItemBinding
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.ListCartItems
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.view.stocks.StocksAdapter.Companion.DIFF_CALLBACK

class CartItemsAdapter : ListAdapter<ListCartItems, CartItemsAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((ListCartItems, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (ListCartItems, Int) -> Unit) {
        onItemClickListener = listener
    }

//    fun deleteItem(deletedItemId: Int) {
//        val newList = currentList.toMutableList()
//        newList.removeAt(deletedItemId)
//        submitList(newList)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            Log.d("CartItemAdapter", "Item binded at position if if (data != null) : $position")
        } else {
            Log.e("CartItemAdapter", "Something get wrong on Item binded at position: $position")
        }
    }

    inner class ViewHolder(private var binding: ItemCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: ListCartItems) {
            with(binding) {
                Log.d(TAG, "Binding stocks: $cartItem")
                tvStockName.text = cartItem.stock_name
                tvStockQuantity.text = cartItem.quantity
                val formattedPrice = helper.formatToRupiah(cartItem.price.toDouble())
                tvStockPrice.text = formattedPrice

                btnDelete.setOnClickListener {
                    // Salin daftar saat ini ke dalam daftar baru yang dapat diubah
                    val newList = currentList.toMutableList()

                    // Dapatkan posisi item yang dihapus
                    val position = bindingAdapterPosition

                    // Pastikan posisi item yang dihapus valid
                    if (position != RecyclerView.NO_POSITION) {
                        // Panggil listener yang ditetapkan dengan item yang dihapus dan posisinya
                        onItemClickListener?.invoke(newList[position], position)

                        // Hapus item yang sesuai dari daftar baru
                        newList.removeAt(position)

                        // Perbarui daftar yang ditampilkan di adapter dengan daftar yang baru
                        submitList(newList)
                    }
                }


//                btnDelete.setOnClickListener{
//                    val newList = currentList.toMutableList()
//                    newList.removeAt(bindingAdapterPosition)
//                    submitList(newList)
//
//                    val position = bindingAdapterPosition
//                    if (position != RecyclerView.NO_POSITION) {
//                        onItemClickListener?.invoke(currentList[position], position)
//                    }
//                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListCartItems>() {
            override fun areItemsTheSame(
                oldItem: ListCartItems,
                newItem: ListCartItems,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListCartItems,
                newItem: ListCartItems,
            ): Boolean {
                return oldItem == newItem
            }
        }

        private const val TAG = "CartItemsAdapter_TAG"
    }
}


//                tvStockPrice.text = salesItem.stock_name
//                btnDelete.setOnClickListener(deleteItem(index))

// Metode untuk memperbarui daftar penjualan dalam adapter
//    fun updateSalesList(newSalesList: List<ListSalesStocksItem>) {
//        ListSalesStocksItem = newSalesList
//        notifyDataSetChanged()  // Beritahu adapter bahwa data telah berubah
//    }
//
//    fun deleteItem(index: Int){
//        listSalesStock.removeAt(index)
//        notifyDataSetChanged()
//    }
//    fun updateSalesList(newSalesList: List<ListSalesStocksItem>) {
//        listSalesStock.clear()  // Hapus data lama
//        listSalesStock.addAll(newSalesList)  // Tambah data baru
//        notifyDataSetChanged()  // Beritahu adapter bahwa data telah berubah
//    }
//    // Antarmuka untuk menangani interaksi pengguna
//    interface OnItemClickListener {
//        fun onItemClick(salesItem: ListSalesStocksItem)
//        fun onDeleteClick(salesItem: ListSalesStocksItem, position: Int)
//    }

//    private var onItemClickListener: OnItemClickListener? = null
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        onItemClickListener = listener
//    }