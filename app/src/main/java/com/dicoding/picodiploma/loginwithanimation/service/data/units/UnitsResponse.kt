package com.dicoding.picodiploma.loginwithanimation.service.data.units

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class AllUnitsResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: List<ListUnitsItem>,

    )

@Parcelize
data class ListUnitsItem(

    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @SerializedName("units_name")
    val units_name: String,
) : Parcelable