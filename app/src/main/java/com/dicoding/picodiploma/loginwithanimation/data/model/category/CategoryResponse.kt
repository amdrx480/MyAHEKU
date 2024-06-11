package com.dicoding.picodiploma.loginwithanimation.data.model.category

import com.google.gson.annotations.SerializedName


data class CategoryResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<CategoryModel>,

    )

//@Parcelize
//data class ListCategoryItem(
//
//    @PrimaryKey
//    @field:SerializedName("id")
//    val id: Int,
//
//    @SerializedName("category_name")
//    val category_Name: String,
//) : Parcelable