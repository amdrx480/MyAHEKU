package com.dicoding.picodiploma.loginwithanimation.data.local.dao.stocks

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity

@Dao
interface StocksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(appEntity: List<StocksEntity>)

    @Query("SELECT * FROM stocks")
    fun getAllStocks(): PagingSource<Int, StocksEntity>

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