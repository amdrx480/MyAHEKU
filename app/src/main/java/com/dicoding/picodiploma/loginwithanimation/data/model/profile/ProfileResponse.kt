package com.dicoding.picodiploma.loginwithanimation.data.model.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ProfileModel,

    )