package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.datamaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.data.model.sales.CartItemsModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemDataMasterBinding

class DataMasterNestedAdapter : ListAdapter<DataMasterItem, DataMasterNestedAdapter.DataMasterViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((DataMasterItem, Int) -> Unit)? = null
    private var expandedPosition: Int? = null

    fun setOnItemClickListener(listener: (DataMasterItem, Int) -> Unit) {
        onItemClickListener = listener
    }

    class DataMasterViewHolder(val binding: ItemDataMasterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataMasterViewHolder {
        val binding = ItemDataMasterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataMasterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataMasterViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.itemTv.text = item.title
        val nestedAdapter = NestedAdapter(item.data)
        holder.binding.childRv.adapter = nestedAdapter
        holder.binding.childRv.layoutManager = LinearLayoutManager(holder.binding.root.context)

        val isExpanded = position == expandedPosition
        holder.binding.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.binding.constraintLayout.setOnClickListener {
            val previouslyExpandedPosition = expandedPosition
            if (isExpanded) {
                expandedPosition = null
            } else {
                expandedPosition = position
            }
            previouslyExpandedPosition?.let { notifyItemChanged(it) }
            notifyItemChanged(position)
        }

        holder.binding.btnAdd.setOnClickListener {
            onItemClickListener?.invoke(item, position)
        }

        // Update the NestedAdapter data when the DataMasterItem data changes
        nestedAdapter.setNestedData(item.data)
    }

    fun updateNestedData(position: Int, data: List<Any>) {
        val item = getItem(position)
        item.data = data
        notifyItemChanged(position)
    }

    fun setData(data: List<DataMasterItem>) {
        // Save the current expanded position
        val expandedPositionBefore = expandedPosition
        submitList(data) {
            // Restore the expanded position if valid
            expandedPosition = if (expandedPositionBefore != null && expandedPositionBefore < data.size) {
                expandedPositionBefore
            } else {
                null
            }
            notifyDataSetChanged()
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataMasterItem>() {
            override fun areItemsTheSame(
                oldItem: DataMasterItem,
                newItem: DataMasterItem,
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: DataMasterItem,
                newItem: DataMasterItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

data class DataMasterItem(
    val title: String,
    var data: List<Any>,
    var isExpanded: Boolean = false
)


//class DataMasterNestedAdapter : ListAdapter<DataMasterItem, DataMasterNestedAdapter.DataMasterViewHolder>(DIFF_CALLBACK) {
//
//    private var onItemClickListener: ((DataMasterItem, Int) -> Unit)? = null
//
//    fun setOnItemClickListener(listener: (DataMasterItem, Int) -> Unit) {
//        onItemClickListener = listener
//    }
//
//    class DataMasterViewHolder(val binding: ItemDataMasterBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataMasterViewHolder {
//        val binding = ItemDataMasterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return DataMasterViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: DataMasterViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.binding.itemTv.text = item.title
//        val nestedAdapter = NestedAdapter(item.data)
//        holder.binding.childRv.adapter = nestedAdapter
//        holder.binding.childRv.layoutManager = LinearLayoutManager(holder.binding.root.context)
//
//        val isExpanded = item.isExpanded
//        holder.binding.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
//
//        holder.binding.constraintLayout.setOnClickListener {
//            item.isExpanded = !item.isExpanded
//            notifyItemChanged(position)
//        }
//
//        holder.binding.btnAdd.setOnClickListener {
//            onItemClickListener?.invoke(item, position)
//        }
//
//        // Update the NestedAdapter data when the DataMasterItem data changes
//        nestedAdapter.setNestedData(item.data)
//    }
//
//    fun updateNestedData(position: Int, data: List<Any>) {
//        val item = getItem(position)
//        item.data = data
//        notifyItemChanged(position)
//    }
//
//    fun setData(data: List<DataMasterItem>) {
//        // Save the current expanded state
//        val expandedStateMap = currentList.mapIndexed { index, item ->
//            index to item.isExpanded
//        }.toMap()
//
//        submitList(data) {
//            // Restore the expanded state
//            data.forEachIndexed { index, item ->
//                item.isExpanded = expandedStateMap[index] ?: item.isExpanded
//            }
//            notifyDataSetChanged()
//        }
//    }
//
//    companion object {
//        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataMasterItem>() {
//            override fun areItemsTheSame(
//                oldItem: DataMasterItem,
//                newItem: DataMasterItem,
//            ): Boolean {
//                return oldItem.title == newItem.title
//            }
//
//            override fun areContentsTheSame(
//                oldItem: DataMasterItem,
//                newItem: DataMasterItem,
//            ): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}
//
//data class DataMasterItem(
//    val title: String,
//    var data: List<Any>,
//    var isExpanded: Boolean = false
//)

//class DataMasterNestedAdapter : ListAdapter<DataMasterItem, DataMasterNestedAdapter.DataMasterViewHolder>(DIFF_CALLBACK) {
//
//    private var onItemClickListener: ((DataMasterItem, Int) -> Unit)? = null
//
//    fun setOnItemClickListener(listener: (DataMasterItem, Int) -> Unit) {
//        onItemClickListener = listener
//    }
//
//    class DataMasterViewHolder(val binding: ItemDataMasterBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataMasterViewHolder {
//        val binding = ItemDataMasterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return DataMasterViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: DataMasterViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.binding.itemTv.text = item.title
//        val nestedAdapter = NestedAdapter(item.data)
//        holder.binding.childRv.adapter = nestedAdapter
//        holder.binding.childRv.layoutManager = LinearLayoutManager(holder.binding.root.context)
//
//        val isExpanded = item.isExpanded
//        holder.binding.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
//
//        holder.binding.constraintLayout.setOnClickListener {
//            item.isExpanded = !item.isExpanded
//            notifyItemChanged(position)
//        }
//
//        holder.binding.btnAdd.setOnClickListener {
//            onItemClickListener?.invoke(item, position)
//        }
//
//        // Update the NestedAdapter data when the DataMasterItem data changes
//        nestedAdapter.setNestedData(item.data)
//    }
//
//    fun updateNestedData(position: Int, data: List<Any>) {
//        val item = getItem(position)
//        item.data = data
//        notifyItemChanged(position)
//    }
//
//    fun setData(data: List<DataMasterItem>) {
//        submitList(data)
//    }
//
//    companion object {
//        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataMasterItem>() {
//            override fun areItemsTheSame(
//                oldItem: DataMasterItem,
//                newItem: DataMasterItem,
//            ): Boolean {
//                return oldItem == newItem
//            }
//
//            override fun areContentsTheSame(
//                oldItem: DataMasterItem,
//                newItem: DataMasterItem,
//            ): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}

//data class DataMasterItem(
//    val title: String,
//    var data: List<Any>,
//    var isExpanded: Boolean = false
//)


//class DataMasterNestedAdapter : ListAdapter<DataMasterItem, DataMasterNestedAdapter.DataMasterViewHolder>(DIFF_CALLBACK) {
//
//    private var onItemClickListener: ((DataMasterItem, Int) -> Unit)? = null
//
//    fun setOnItemClickListener(listener: (DataMasterItem, Int) -> Unit) {
//        onItemClickListener = listener
//    }
//
//    class DataMasterViewHolder(val binding: ItemDataMasterBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataMasterViewHolder {
//        val binding = ItemDataMasterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return DataMasterViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: DataMasterViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.binding.itemTv.text = item.title
//        val nestedAdapter = NestedAdapter(item.data)
//        holder.binding.childRv.adapter = nestedAdapter
//        holder.binding.childRv.layoutManager = LinearLayoutManager(holder.binding.root.context)
//
//        val isExpanded = item.isExpanded
//        holder.binding.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
//
//        holder.binding.constraintLayout.setOnClickListener {
//            item.isExpanded = !item.isExpanded
//            notifyItemChanged(position)
//        }
//
//        holder.binding.btnAdd.setOnClickListener {
//            onItemClickListener?.invoke(item, position)
//        }
//
//        // Update the NestedAdapter data when the DataMasterItem data changes
//        nestedAdapter.setNestedData(item.data)
//    }
//
//    fun setData(data: List<DataMasterItem>) {
//        submitList(data)
//    }
//
//    companion object {
//        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataMasterItem>() {
//            override fun areItemsTheSame(
//                oldItem: DataMasterItem,
//                newItem: DataMasterItem,
//            ): Boolean {
//                return oldItem == newItem
//            }
//
//            override fun areContentsTheSame(
//                oldItem: DataMasterItem,
//                newItem: DataMasterItem,
//            ): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}
//
//
//data class DataMasterItem(
//    val title: String,
//    val data: List<Any>,
//    var isExpanded: Boolean = false
//)
//
//class DataMasterItemDiffCallback : DiffUtil.ItemCallback<DataMasterItem>() {
//    override fun areItemsTheSame(oldItem: DataMasterItem, newItem: DataMasterItem): Boolean {
//        return oldItem.title == newItem.title // Implementasi sesuai kebutuhan Anda
//    }
//
//    override fun areContentsTheSame(oldItem: DataMasterItem, newItem: DataMasterItem): Boolean {
//        return oldItem == newItem // Implementasi sesuai kebutuhan Anda
//    }
//}
//
//
//
