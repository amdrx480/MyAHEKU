package com.dicoding.picodiploma.loginwithanimation.data.model.units

import com.google.gson.annotations.SerializedName


data class UnitsResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<UnitsModel>,

    )

