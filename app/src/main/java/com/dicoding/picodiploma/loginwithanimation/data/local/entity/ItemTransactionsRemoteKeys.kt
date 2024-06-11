package com.dicoding.picodiploma.loginwithanimation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "item_transactions_remote_keys")
data class ItemTransactionsRemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
)