package com.dicoding.picodiploma.loginwithanimation.service.data.category

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class AllCategoryResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: List<ListCategoryItem>,

    )

@Parcelize
data class ListCategoryItem(

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @SerializedName("category_name")
    val category_Name: String,
) : Parcelable