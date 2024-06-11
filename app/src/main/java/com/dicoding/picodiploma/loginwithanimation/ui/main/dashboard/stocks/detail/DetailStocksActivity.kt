package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStocksBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity

class DetailStocksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStocksBinding
    private lateinit var detailList: StocksEntity

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
        val unitsName = detailViewModel.detailItem.unitName
        val stockTotal = detailViewModel.detailItem.stockTotal
//        val descriptionEditText = binding.itemDescriptionEditText
//        descriptionEditText.isNestedScrollingEnabled = false

        val combinedText = getString(R.string.stock_total_with_units, unitsName, stockTotal)
        with(binding){
            itemNameTextView.text = detailViewModel.detailItem.stockName
            itemQuantityTextView.text = combinedText
            itemCodeTextView.text = detailViewModel.detailItem.stockCode
            itemCategoryTextView.text = detailViewModel.detailItem.categoryName
            itemSellingTextView.text = detailViewModel.detailItem.sellingPrice.toString()
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