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
    private val token: String,
    private val sort: String? = null,
    private val order: String? = null,
    private val search: String? = null,
//    private val customerName: List<String>? = null,
    private val categoryName: List<String>? = null,
    private val unitName: List<String>? = null,
    private val subTotalMin: Int? = null,
    private val subTotalMax: Int? = null
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
            val categoryFilter = categoryName?.joinToString(",")
            val unitFilter = unitName?.joinToString(",")
//            val customerFilter = customerName?.joinToString(",")


            //jancok haurs pake all response gak bisa kek biasa
            val apiResponse =
                apiService.getAllItemTransactions(
                    data = "Bearer $token",
                    page = page,
                    limit = state.config.pageSize,
                    sort = sort,
                    order = order, // add order parameter
                    search = search,
//                    customerName = customerFilter,
                    categoryName = categoryFilter, // Pass new parameters to API call
                    unitName = unitFilter,
                    subTotalMin = subTotalMin,
                    subTotalMax = subTotalMax
                )

            val itemTransactionsData = apiResponse.data
            val endOfPaginationReached = itemTransactionsData.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.itemTransactionsRemoteKeysDao().deleteRemoteKeys()
                    appDatabase.itemTransactionsDao().deleteAllItemTransactions()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = itemTransactionsData.map {
                    ItemTransactionsRemoteKeys(
                        id = it.id.toString(),
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                appDatabase.itemTransactionsRemoteKeysDao().insertAll(keys)
                appDatabase.itemTransactionsDao().insertItemTransactions(itemTransactionsData)
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