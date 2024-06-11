package com.dicoding.picodiploma.loginwithanimation.data.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val voucher: String,
    val token: String,
    val isLogin: Boolean
): Parcelable