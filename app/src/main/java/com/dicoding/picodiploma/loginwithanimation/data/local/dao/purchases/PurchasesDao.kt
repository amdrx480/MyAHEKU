package com.dicoding.picodiploma.loginwithanimation.data.local.dao.purchases

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity

@Dao
interface PurchasesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPurchases(purchasesEntity: List<PurchasesEntity>)

    // Read
    @RawQuery(observedEntities = [PurchasesEntity::class])
    fun getPurchasesByRawQuery(query: SupportSQLiteQuery): PagingSource<Int, PurchasesEntity>

    @Query("SELECT * FROM purchases")
    fun getAllPurchases(): PagingSource<Int, PurchasesEntity>

    // Update
    @Update
    suspend fun updateStock(purchases: PurchasesEntity)

    @Query("DELETE FROM purchases")
    suspend fun deleteAllPurchases()

}