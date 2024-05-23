package com.dicoding.picodiploma.loginwithanimation.view.stocks.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStocksBinding
import com.dicoding.picodiploma.loginwithanimation.service.data.stocks.ListStocksItem

class DetailStocksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStocksBinding
    private lateinit var detailList: ListStocksItem

    private val detailViewModel: DetailStocksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStocksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailList = intent.getParcelableExtra(EXTRA_DETAIL)!!
        detailViewModel.setDetailStory(detailList)

        detailDisplayResult()
    }

    private fun detailDisplayResult() {
        val unitsName = detailViewModel.detailItem.units_Name
        val stockTotal = detailViewModel.detailItem.stock_total
//        val descriptionEditText = binding.itemDescriptionEditText
//        descriptionEditText.isNestedScrollingEnabled = false

        val combinedText = getString(R.string.stock_total_with_units, unitsName, stockTotal)
        with(binding){
            itemNameTextView.text = detailViewModel.detailItem.stock_Name
            itemQuantityTextView.text = combinedText
            itemCodeTextView.text = detailViewModel.detailItem.stock_Code
            itemCategoryTextView.text = detailViewModel.detailItem.category_Name
            itemSellingTextView.text = detailViewModel.detailItem.selling_Price.toString()
//            itemDescriptionEditText.text =

//            txtVCreatedTime.text = getString(R.string.created_add, helper.dateFormat(detailViewModel.storyItem.createdAt,
//                TimeZone.getDefault().id ))
//            txtVDescription.text = detailViewModel.storyItem.description

//            Glide.with(photoImageView)
//                .load(detailViewModel.detailItem.stock_photo)
//                .placeholder(R.drawable.ic_place_default_holder)
//                .error(R.drawable.ic_broken_image)
//                .into(photoImageView)
        }
    }

    companion object {
        const val EXTRA_DETAIL = "detailStocks"
    }
}