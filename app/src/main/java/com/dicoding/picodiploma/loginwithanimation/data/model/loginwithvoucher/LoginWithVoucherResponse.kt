package com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher

import com.google.gson.annotations.SerializedName

data class LoginWithVoucherResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("login_result")
//    val loginWithVoucherResult : List<LoginWithVoucherResult>
    val loginWithVoucherResult : AdminModel

)