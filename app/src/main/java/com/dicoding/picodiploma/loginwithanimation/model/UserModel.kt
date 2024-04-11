package com.dicoding.picodiploma.loginwithanimation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val password: String,
    val token: String,
    val isLogin: Boolean
): Parcelable