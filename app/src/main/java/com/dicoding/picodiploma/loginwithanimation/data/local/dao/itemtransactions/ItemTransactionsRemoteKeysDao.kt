package com.dicoding.picodiploma.loginwithanimation.data.local.dao.itemtransactions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.ItemTransactionsRemoteKeys


@Dao
interface ItemTransactionsRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ItemTransactionsRemoteKeys>)

    @Query("SELECT * FROM item_transactions_remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): ItemTransactionsRemoteKeys?

    @Query("DELETE FROM item_transactions_remote_keys")
    suspend fun deleteRemoteKeys()

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(remoteKey: List<RemoteKeys>)
//
//    @Query("SELECT * FROM remote_keys WHERE id = :id")
//    fun getRemoteKeysId(id: String): RemoteKeys?
//
//    @Query("DELETE FROM remote_keys")
//    fun deleteRemoteKeys()

}