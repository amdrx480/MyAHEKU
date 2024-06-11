package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AppRepository
import com.dicoding.picodiploma.loginwithanimation.data.local.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository

object Injection {
    fun provideAppRepository(context: Context): AppRepository {
        val appDatabase = AppDatabase.getInstance(context)
        val apiService = ApiConfig.ApiService()
        return AppRepository(appDatabase, apiService)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val authPreferenceDataSource = AuthPreferenceDataSource.getInstance(context)
        val appDatabase = AppDatabase.getInstance(context)
        val apiService = ApiConfig.ApiService()
        return AuthRepository(authPreferenceDataSource, appDatabase, apiService)
    }

//    fun provideAuthRepository(dataStore: DataStore<Preferences>): AuthRepository {
//        val authPreferenceDataSource = AuthPreferenceDataSource.getInstance(dataStore)
//        val apiService = ApiConfig.ApiService()
//        return AuthRepository(authPreferenceDataSource, apiService)
//    }
}