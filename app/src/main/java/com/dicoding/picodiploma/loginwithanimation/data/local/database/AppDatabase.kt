package com.dicoding.picodiploma.loginwithanimation.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.loginwithanimation.data.local.dao.itemtransactions.ItemTransactionsDao
import com.dicoding.picodiploma.loginwithanimation.data.local.dao.itemtransactions.ItemTransactionsRemoteKeysDao
import com.dicoding.picodiploma.loginwithanimation.data.local.dao.stocks.StocksDao
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.RemoteKeys
import com.dicoding.picodiploma.loginwithanimation.data.local.dao.stocks.StocksRemoteKeysDao
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.local.dao.purchases.PurchasesDao
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.PurchasesRemoteKeys
import com.dicoding.picodiploma.loginwithanimation.data.local.dao.purchases.PurchasesRemoteKeysDao
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.ItemTransactionsRemoteKeys
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity

//ketemu
@Database(
    entities = [StocksEntity::class, PurchasesEntity::class, ItemTransactionsEntity::class, RemoteKeys::class, PurchasesRemoteKeys::class, ItemTransactionsRemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): StocksDao
    abstract fun remoteKeysDao(): StocksRemoteKeysDao
    abstract fun purchasesDao(): PurchasesDao
    abstract fun purchasesRemoteKeysDao(): PurchasesRemoteKeysDao
    abstract fun itemTransactionsDao(): ItemTransactionsDao
    abstract fun itemTransactionsRemoteKeysDao(): ItemTransactionsRemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "myAheku.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}