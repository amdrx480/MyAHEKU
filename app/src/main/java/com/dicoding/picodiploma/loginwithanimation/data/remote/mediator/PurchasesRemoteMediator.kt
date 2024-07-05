package com.dicoding.picodiploma.loginwithanimation.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.local.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.PurchasesRemoteKeys
import com.dicoding.picodiploma.loginwithanimation.data.model.purchases.PurchasesEntity


@OptIn(ExperimentalPagingApi::class)
class PurchasesRemoteMediator(
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
    private val token: String?,
    private val sort: String?,
    private val order: String?,
    private val search: String?,
    private val categoryName: List<String>? = null,
    private val unitName: List<String>? = null,
    private val sellingPriceMin: Int? = null,
    private val sellingPriceMax: Int? = null,
) : RemoteMediator<Int, PurchasesEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PurchasesEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val purchasesRemoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                purchasesRemoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val purchasesRemoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = purchasesRemoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = purchasesRemoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val purchasesRemoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = purchasesRemoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = purchasesRemoteKeys != null)
                nextKey
            }
        }

        return try {
            val categoryFilter = categoryName?.joinToString(",")
            val unitFilter = unitName?.joinToString(",")

            //jancok haurs pake all response gak bisa kek biasa
//      val responseData =
//        apiService.
//        getAllPurchase("Bearer $data", page, state.config.pageSize).data

            val apiResponse = apiService.getAllPurchase(
                data = "Bearer $token",
                page = page,
                limit = state.config.pageSize,
                sort = sort,
                order = order, // add order parameter
                search = search,
                categoryName = categoryFilter, // Pass new parameters to API call
                unitName = unitFilter,
                sellingPriceMin = sellingPriceMin,
                sellingPriceMax = sellingPriceMax
            )

            val purchases = apiResponse.data
            val endOfPaginationReached = purchases.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.purchasesRemoteKeysDao().deleteRemoteKeys()
                    appDatabase.purchasesDao().deleteAllPurchases()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = purchases.map {
                    PurchasesRemoteKeys(id = it.id.toString(), prevKey = prevKey, nextKey = nextKey)
                }

                appDatabase.purchasesRemoteKeysDao().insertAll(keys)
                appDatabase.purchasesDao().insertAllPurchases(purchases)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PurchasesEntity>): PurchasesRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            appDatabase.purchasesRemoteKeysDao().getRemoteKeysId(it.id.toString())
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PurchasesEntity>): PurchasesRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            appDatabase.purchasesRemoteKeysDao().getRemoteKeysId(it.id.toString())
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PurchasesEntity>): PurchasesRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                appDatabase.purchasesRemoteKeysDao().getRemoteKeysId(it.toString())
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