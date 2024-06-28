package com.dicoding.picodiploma.loginwithanimation.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.local.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.RemoteKeys
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class StocksRemoteMediator(
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
) : RemoteMediator<Int, StocksEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StocksEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val categoryFilter = categoryName?.joinToString(",")
            val unitFilter = unitName?.joinToString(",")

            val apiResponse = apiService.getAllStocks(
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

            val stocks = apiResponse.data
            val endOfPaginationReached = stocks.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.remoteKeysDao().deleteRemoteKeys()
                    appDatabase.stocksDao().deleteAllStocks()
                }

                // Insert new data and remote keys
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = stocks.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                appDatabase.remoteKeysDao().insertAllRemoteKeys(keys)
                appDatabase.stocksDao().insertAllStocks(stocks)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StocksEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            appDatabase.remoteKeysDao().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StocksEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            appDatabase.remoteKeysDao().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StocksEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                appDatabase.remoteKeysDao().getRemoteKeysId(it)
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
