package com.dicoding.picodiploma.loginwithanimation.view.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemCartItemBinding
import com.dicoding.picodiploma.loginwithanimation.service.data.sales.ListCartItems
import com.dicoding.picodiploma.loginwithanimation.utils.DiffCallBack
import com.dicoding.picodiploma.loginwithanimation.utils.helper

class CartItemsAdapter : RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {

    private val listSalesStock = mutableListOf<ListCartItems>()
//    private val listSalesStock = ArrayList<ListCartItems>()

    private var onItemClickListener: ((ListCartItems, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (ListCartItems, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setListSalesStock(itemSales: List<ListCartItems>) {
//        if (itemSales.isEmpty()) {
//            Log.e("ItemsAdapter", "Failed to set list sales stock: Empty list")
//            // Tambahkan pesan error di sini jika perlu
//        } else {
            val diffCallback = DiffCallBack(this.listSalesStock, itemSales)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.listSalesStock.clear()
            this.listSalesStock.addAll(itemSales)
            diffResult.dispatchUpdatesTo(this)
//            notifyDataSetChanged()

//        }
//        val diffCallback = DiffCallBack(this.listSalesStock, itemSales)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)

//        this.listSalesStock.clear()
//        this.listSalesStock.addAll(ListCartItems)

//        this.listSalesStock.clear()
//        this.listSalesStock.addAll(itemSales)
//        notifyDataSetChanged()
//        diffResult.dispatchUpdatesTo(this)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listSalesStock[position], position)
        Log.d("CartItemAdapter", "Item binded at position: $position")
    }
    override fun getItemCount() = listSalesStock.size

    inner class ViewHolder(private var binding: ItemCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: ListCartItems, position: Int) {
            with(binding) {

                Log.d(TAG, "Binding stocks: $cartItem")
                tvStockName.text = cartItem.stock_name
                tvStockQuantity.text = cartItem.quantity
                val formattedPrice = helper.formatToRupiah(cartItem.price.toDouble())
                tvStockPrice.text = formattedPrice

//                Log.d(TAG, "Binding stocks: $cartItem")
//                tvStockName.text = cartItem.stock_name
//                tvStockQuantity.text = cartItem.quantity
//                val formattedPrice = helper.formatToRupiah(cartItem.price.toDouble())
//                tvStockPrice.text = formattedPrice

                btnDelete.setOnClickListener{
                    onItemClickListener?.invoke(cartItem, position)
                    listSalesStock.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }
    }
    companion object {
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