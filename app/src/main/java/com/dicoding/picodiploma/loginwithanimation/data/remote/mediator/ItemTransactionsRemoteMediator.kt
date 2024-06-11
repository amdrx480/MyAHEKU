package com.dicoding.picodiploma.loginwithanimation.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.picodiploma.loginwithanimation.data.local.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.ItemTransactionsRemoteKeys
import com.dicoding.picodiploma.loginwithanimation.data.model.transactions.ItemTransactionsEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService

@OptIn(ExperimentalPagingApi::class)
class ItemTransactionsRemoteMediator(
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
    private val data: String
) : RemoteMediator<Int, ItemTransactionsEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemTransactionsEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val itemTransactionsRemoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                itemTransactionsRemoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val itemTransactionsRemoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = itemTransactionsRemoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = itemTransactionsRemoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val itemTransactionsRemoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = itemTransactionsRemoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = itemTransactionsRemoteKeys != null)
                nextKey
            }
        }

        return try {
            //jancok haurs pake all response gak bisa kek biasa
            val responseData =
                apiService.
                getAllItemTransactions("Bearer $data", page, state.config.pageSize).data

            val endOfPaginationReached = responseData.isEmpty()
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.itemTransactionsRemoteKeysDao().deleteRemoteKeys()
                    appDatabase.itemTransactionsDao().deleteAllItemTransactions()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = responseData.map {
                    ItemTransactionsRemoteKeys(id = it.id.toString(), prevKey = prevKey, nextKey = nextKey)
                }

                appDatabase.itemTransactionsRemoteKeysDao().insertAll(keys)
                appDatabase.itemTransactionsDao().insertItemTransactions(responseData)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ItemTransactionsEntity>): ItemTransactionsRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            appDatabase.itemTransactionsRemoteKeysDao().getRemoteKeysId(it.id.toString())
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ItemTransactionsEntity>): ItemTransactionsRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            appDatabase.itemTransactionsRemoteKeysDao().getRemoteKeysId(it.id.toString())
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ItemTransactionsEntity>): ItemTransactionsRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                appDatabase.itemTransactionsRemoteKeysDao().getRemoteKeysId(it.toString())
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}