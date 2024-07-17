package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.datamaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.data.model.category.CategoryModel
import com.dicoding.picodiploma.loginwithanimation.data.model.customers.customers.CustomersModel
import com.dicoding.picodiploma.loginwithanimation.data.model.units.UnitsModel
import com.dicoding.picodiploma.loginwithanimation.data.model.vendors.VendorsModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemNestedBinding

class NestedAdapter(private var data: List<Any>) : RecyclerView.Adapter<NestedAdapter.NestedViewHolder>() {

    class NestedViewHolder(val binding: ItemNestedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val binding = ItemNestedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NestedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        val item = data[position]

                // Customize the display based on the type of data
        when (item) {
            is CustomersModel -> {
                holder.binding.tvName.text = item.customer_name
                holder.binding.tvDetails.text = "${item.customer_address}, ${item.customer_email}, ${item.customer_phone}"
            }
            is VendorsModel -> {
                holder.binding.tvName.text = item.vendor_Name
                holder.binding.tvDetails.text = "${item.vendor_Address}, ${item.vendor_Email}, ${item.vendor_Phone}"
            }
            is UnitsModel -> {
                holder.binding.tvName.text = item.unit_name
                holder.binding.tvDetails.text = ""
            }
            is CategoryModel -> {
                holder.binding.tvName.text = item.category_Name
                holder.binding.tvDetails.text = ""
            }
        }
//        holder.binding.itemTv.text = item.toString() // Adjust this according to your data model
    }

    override fun getItemCount(): Int = data.size

    fun setNestedData(newData: List<Any>) {
        this.data = newData
        notifyDataSetChanged()
    }
}


//class NestedAdapter(
//    private var nestedData: List<Any> = emptyList()
//) : RecyclerView.Adapter<NestedAdapter.NestedViewHolder>() {
//
//    class NestedViewHolder(val binding: ItemNestedBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
//        val binding = ItemNestedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return NestedViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
//        val item = nestedData[position]
//
//        // Customize the display based on the type of data
//        when (item) {
//            is VendorsModel -> {
//                holder.binding.tvName.text = item.vendor_Name
//                holder.binding.tvDetails.text = "${item.vendor_Address}, ${item.vendor_Email}, ${item.vendor_Phone}"
//            }
//            is UnitsModel -> {
//                holder.binding.tvName.text = item.unit_name
//                holder.binding.tvDetails.text = ""
//            }
//            is CategoryModel -> {
//                holder.binding.tvName.text = item.category_Name
//                holder.binding.tvDetails.text = ""
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = nestedData.size
//
//    fun setNestedData(newData: List<Any>) {
//        nestedData = newData
//        notifyDataSetChanged()
//    }
//}
