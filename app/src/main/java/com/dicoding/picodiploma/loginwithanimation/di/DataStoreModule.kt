package com.dicoding.picodiploma.loginwithanimation.di
//
//import android.content.Context
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStore
//import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//
//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//
//@Module
//@InstallIn(SingletonComponent::class)
//class DataStoreModule {
//
//    @Provides
//    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
//        context.dataStore
//
//    @Provides
//    @Singleton
//    fun provideAuthPreferences(dataStore: DataStore<Preferences>): AuthPreferenceDataSource =
//        AuthPreferenceDataSource(dataStore)
//}