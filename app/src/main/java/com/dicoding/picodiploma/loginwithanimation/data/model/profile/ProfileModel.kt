package com.dicoding.picodiploma.loginwithanimation.data.model.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class ProfileModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    val updatedAt: Date,

    @SerializedName("deleted_at")
    val deletedAt: Date?,

    @SerializedName("image_path")
    val imagePath: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("role_id")
    val roleId: Int,

    @SerializedName("role_name")
    val roleName: String,

    @SerializedName("voucher")
    val voucher: String,

    @SerializedName("password")
    val password: String
) : Parcelable
