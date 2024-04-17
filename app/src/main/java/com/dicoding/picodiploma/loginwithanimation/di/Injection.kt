package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository
import com.dicoding.picodiploma.loginwithanimation.service.database.AppDatabase

object Injection {
    fun provideAppRepository(context: Context): AppRepository {
        val appDatabase = AppDatabase.getInstance(context)
        val apiService = ApiConfig.ApiService()
        return AppRepository(appDatabase, apiService)
    }
}