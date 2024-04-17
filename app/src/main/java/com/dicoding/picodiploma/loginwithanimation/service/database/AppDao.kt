package com.dicoding.picodiploma.loginwithanimation.service.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.model.stocks.ListStocksItem

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(appEntity: List<ListStocksItem>)

    @Query("SELECT * FROM stocks")
    fun getAllStocks(): PagingSource<Int, ListStocksItem>

    @Query("DELETE FROM stocks")
    suspend fun deleteAllStocks()

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertStocks(appEntity: List<ListStocksItem>)
//
//    @Query("SELECT * FROM stocks")
//    fun getAllStocks(): PagingSource<Int, ListStocksItem>
//
//    @Query("DELETE FROM stocks")
//    fun deleteAllStocks()
}