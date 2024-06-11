package com.dicoding.picodiploma.loginwithanimation.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthPreferenceDataSource(private val dataStore: DataStore<Preferences>) {

    suspend fun saveAuthVoucher(Voucher: String) {
        dataStore.edit { preferences ->
            preferences[VOUCHER_KEY] = Voucher
        }
    }

    suspend fun saveAuthToken(tokenVoucher: String) {
        dataStore.edit { preferences ->
            preferences[VOUCHER_TOKEN_KEY] = tokenVoucher
        }
    }

    suspend fun saveAuthStateToken(stateVoucher: Boolean) {
        dataStore.edit { preferences ->
            preferences[VOUCHER_LOGIN_STATE_KEY] = stateVoucher
        }
    }

    fun getAuthVoucher(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[VOUCHER_KEY]
        }
    }

    fun getAuthToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[VOUCHER_TOKEN_KEY]
        }
    }

    fun getAuthStateToken(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[VOUCHER_LOGIN_STATE_KEY] ?: false
        }
    }

    suspend fun logoutVoucher() {
        dataStore.edit { preferences ->
            preferences[VOUCHER_LOGIN_STATE_KEY] = false
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: AuthPreferenceDataSource? = null

        // Login
//        private val PASSWORD_KEY = stringPreferencesKey("password")
//        private val TOKEN_KEY = stringPreferencesKey("token")
//        private val STATE_KEY = booleanPreferencesKey("state")

        // Voucher Login
        private val VOUCHER_KEY = stringPreferencesKey("voucher")
        private val VOUCHER_TOKEN_KEY = stringPreferencesKey("token")
        private val VOUCHER_LOGIN_STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(context: Context): AuthPreferenceDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferenceDataSource(context.dataStore)
                INSTANCE = instance
                instance
            }
        }

    }
}


//    suspend fun saveVoucherSession(user: UserModel) {
//        dataStore.edit { preferences ->
//            preferences[VOUCHER_KEY] = user.voucher
//            preferences[VOUCHER_TOKEN_KEY] = user.token
//            preferences[VOUCHER_LOGIN_STATE_KEY] = user.isLogin
////            preferences[LAST_LOGIN_TIME_KEY] = System.currentTimeMillis() // Simpan waktu login
//        }
//    }
//
//    fun getVoucherSession(): Flow<UserModel> {
//        return dataStore.data.map { preferences ->
//            UserModel(
//                preferences[VOUCHER_KEY] ?:"",
//                preferences[VOUCHER_TOKEN_KEY] ?:"",
//                preferences[VOUCHER_LOGIN_STATE_KEY] ?: false,
//            )
//        }
//    }

//    suspend fun saveSession(user: UserModel) {
//        dataStore.edit { preferences ->
//            preferences[PASSWORD_KEY] = user.password
//            preferences[TOKEN_KEY] = user.token
//            preferences[STATE_KEY] = user.isLogin
//        }
//    }

//    fun getSession(): Flow<UserModel> {
//        return dataStore.data.map { preferences ->
//            UserModel(
//                preferences[PASSWORD_KEY] ?: "",
//                preferences[TOKEN_KEY] ?: "",
//                preferences[STATE_KEY] ?: false,
//            )
//        }
//    }

//        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferenceDataSource {
//            return INSTANCE ?: synchronized(this) {
//                val instance = AuthPreferenceDataSource(dataStore)
//                INSTANCE = instance
//                instance
//            }
//        }
//    suspend fun logout() {
//        dataStore.edit { preferences ->
//            preferences[PASSWORD_KEY] = ""
//            preferences[TOKEN_KEY] = ""
//            preferences[STATE_KEY] = false
//        }
//    }

//    fun getAuthToken(): Flow<Boolean> {
//        return dataStore.data.map { preferences ->
//            preferences[TOKEN_VOUCHER_KEY] ?: false
//        }
//    }

//class AuthPreferenceDataSource @Inject constructor(private val dataStore: DataStore<Preferences>) {

//            preferences[LAST_LOGIN_TIME_KEY] = System.currentTimeMillis() // Simpan waktu login

//    private val LAST_LOGIN_TIME_KEY = longPreferencesKey("last_login_time")
//
//    suspend fun setLastLoginTime(time: Long) {
//        dataStore.edit { preferences ->
//            preferences[LAST_LOGIN_TIME_KEY] = time
//        }
//    }
//
//    suspend fun getLastLoginTime(): Long? {
//        return dataStore.data.map { preferences ->
//            preferences[LAST_LOGIN_TIME_KEY]
//        }.first()
//    }