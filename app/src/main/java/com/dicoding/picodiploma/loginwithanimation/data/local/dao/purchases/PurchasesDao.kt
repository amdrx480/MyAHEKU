package com.dicoding.picodiploma.loginwithanimation.data.local.dao.purchases

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity

@Dao
interface PurchasesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchases(purchasesEntity: List<PurchasesEntity>)

    @Query("SELECT * FROM purchases")
    fun getAllPurchases(): PagingSource<Int, PurchasesEntity>

    @Query("DELETE FROM purchases")
    suspend fun deleteAllPurchases()

}