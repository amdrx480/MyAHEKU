package com.dicoding.picodiploma.loginwithanimation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases_remote_keys")
data class PurchasesRemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
)