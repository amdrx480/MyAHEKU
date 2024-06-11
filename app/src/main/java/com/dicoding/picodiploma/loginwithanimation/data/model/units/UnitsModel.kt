package com.dicoding.picodiploma.loginwithanimation.data.model.units

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnitsModel(

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @SerializedName("unit_name")
    val unit_name: String,
) : Parcelable