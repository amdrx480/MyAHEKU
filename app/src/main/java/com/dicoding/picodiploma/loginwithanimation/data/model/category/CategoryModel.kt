package com.dicoding.picodiploma.loginwithanimation.data.model.category

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryModel(

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @SerializedName("category_name")
    val category_Name: String,
) : Parcelable