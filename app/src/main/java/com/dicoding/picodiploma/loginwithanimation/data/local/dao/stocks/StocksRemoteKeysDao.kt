package com.dicoding.picodiploma.loginwithanimation.data.local.dao.stocks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.RemoteKeys


@Dao
interface StocksRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
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