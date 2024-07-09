package com.dicoding.picodiploma.loginwithanimation.data.local.dao.itemtransactions

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity

@Dao
interface ItemTransactionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemTransactions(purchasesEntity: List<ItemTransactionsEntity>)

    @RawQuery(observedEntities = [ItemTransactionsEntity::class])
    fun getItemTransactionsByRawQuery(query: SupportSQLiteQuery): PagingSource<Int, ItemTransactionsEntity>

    @Query("SELECT * FROM item_transactions")
    fun getAllItemTransactions(): PagingSource<Int, ItemTransactionsEntity>

    // Update
    @Update
    suspend fun updateStock(itemTransactionsEntity: ItemTransactionsEntity)

    @Query("DELETE FROM item_transactions")
    suspend fun deleteAllItemTransactions()

}