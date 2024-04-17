package com.dicoding.picodiploma.loginwithanimation.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val LAST_LOGIN_TIME_KEY = longPreferencesKey("last_login_time")

    suspend fun setLastLoginTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_LOGIN_TIME_KEY] = time
        }
    }

    suspend fun getLastLoginTime(): Long? {
        return dataStore.data.map { preferences ->
            preferences[LAST_LOGIN_TIME_KEY]
        }.first()
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[PASSWORD_KEY] = user.password
            preferences[TOKEN_KEY] = user.token
            preferences[STATE_KEY] = user.isLogin
            preferences[LAST_LOGIN_TIME_KEY] = System.currentTimeMillis() // Simpan waktu login
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[PASSWORD_KEY] ?:"",
                    preferences[TOKEN_KEY] ?:"",
                    preferences[STATE_KEY] ?: false,
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[PASSWORD_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[STATE_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
