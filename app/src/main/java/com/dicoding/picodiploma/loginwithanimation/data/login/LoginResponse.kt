package com.dicoding.picodiploma.loginwithanimation.data.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token : String

)