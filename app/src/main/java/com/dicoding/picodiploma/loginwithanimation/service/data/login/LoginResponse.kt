package com.dicoding.picodiploma.loginwithanimation.service.data.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token : String
//    @SerializedName("token")
//    val loginResult : LoginResult
)

//data class LoginResult(
//
//    @field:SerializedName("name")
//    val name: String,
//
//    @field:SerializedName("userId")
//    val userId: String,
//
//    @field:SerializedName("token")
//    val token: String
//)