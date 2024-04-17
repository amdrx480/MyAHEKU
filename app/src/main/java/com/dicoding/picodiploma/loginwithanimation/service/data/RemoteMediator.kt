package com.dicoding.picodiploma.loginwithanimation.service.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.picodiploma.loginwithanimation.model.stocks.ListStocksItem
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.service.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.service.database.RemoteKeys


@OptIn(ExperimentalPagingApi::class)
class RemoteMediator(
  private val appDatabase: AppDatabase,
  private val apiService: ApiService,
  private val token: String
) : RemoteMediator<Int, ListStocksItem>() {
  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, ListStocksItem>
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
      val responseData =
        apiService.getAllStocks("Bearer $token", page, state.config.pageSize).token
      val endOfPaginationReached = responseData.isEmpty()

      appDatabase.withTransaction {
        if (loadType == LoadType.REFRESH) {
          appDatabase.remoteKeysDao().deleteRemoteKeys()
          appDatabase.appDao().deleteAllStocks()
        }

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1

        val keys = responseData.map {
          RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
        }

        appDatabase.remoteKeysDao().insertAll(keys)
        appDatabase.appDao().insertStocks(responseData)
      }

      MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    } catch (exception: Exception) {
      MediatorResult.Error(exception)
    }
  }

  private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStocksItem>): RemoteKeys? {
    return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
      appDatabase.remoteKeysDao().getRemoteKeysId(it.id)
    }
  }

  private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStocksItem>): RemoteKeys? {
    return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
      appDatabase.remoteKeysDao().getRemoteKeysId(it.id)
    }
  }

  private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStocksItem>): RemoteKeys? {
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