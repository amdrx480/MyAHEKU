package com.dicoding.picodiploma.loginwithanimation.data.local.dao.stocks

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity

@Dao
interface StocksDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStocks(appEntity: List<StocksEntity>)

    // Read
    @RawQuery(observedEntities = [StocksEntity::class])
    fun getStocksByRawQuery(query: SupportSQLiteQuery): PagingSource<Int, StocksEntity>

    // Update
    @Update
    suspend fun updateStock(stock: StocksEntity)

    @Query("DELETE FROM stocks")
    suspend fun deleteAllStocks()
}

// Read
//    @Query("SELECT * FROM stocks ORDER BY stock_name ASC")
//    @Query("SELECT * FROM stocks")
//    fun getAllStocks(): PagingSource<Int, StocksEntity>
//
//    @Query("SELECT * FROM stocks WHERE category_name LIKE '%' || :filter || '%' " +
//            "ORDER BY CASE WHEN :sort = 'asc' THEN selling_price END ASC, " +
//            "CASE WHEN :sort = 'desc' THEN selling_price END DESC")
//    fun getFilteredStocks(
//        filter: String,
//        sort: String
//    ): PagingSource<Int, StocksEntity>
//
//    @Query("SELECT * FROM stocks WHERE stock_name LIKE '%' || :search || '%'")
//    fun searchStocksByName(search: String): PagingSource<Int, StocksEntity>


//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertStocks(appEntity: List<ListStocksItem>)
//
//    @Query("SELECT * FROM stocks")
//    fun getAllStocks(): PagingSource<Int, ListStocksItem>
//
//    @Query("DELETE FROM stocks")
//    fun deleteAllStocks()