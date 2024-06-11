package com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdminModel(

    @SerializedName("token")
    val token : String

) : Parcelable