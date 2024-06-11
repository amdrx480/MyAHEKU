package com.dicoding.picodiploma.loginwithanimation.data.local.dao.itemtransactions

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity

@Dao
interface ItemTransactionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemTransactions(purchasesEntity: List<ItemTransactionsEntity>)

    @Query("SELECT * FROM item_transactions")
    fun getAllItemTransactions(): PagingSource<Int, ItemTransactionsEntity>

    @Query("DELETE FROM item_transactions")
    suspend fun deleteAllItemTransactions()

}