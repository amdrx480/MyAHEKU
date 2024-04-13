package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
//import androidx.lifecycle.asLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(private val repository: UserPreference) : ViewModel() {

    private val SESSION_TIMEOUT = TimeUnit.MINUTES.toMillis(30)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

//    fun logout() {
//        viewModelScope.launch {
//            repository.logout()
//        }
//    }

    fun setLastLoginTime() {
        val currentTime = System.currentTimeMillis()
        viewModelScope.launch {
            repository.setLastLoginTime(currentTime)
        }
    }

    suspend fun isSessionExpired(): Boolean {
        val lastLoginTime = repository.getLastLoginTime()
        return if (lastLoginTime == null) {
            true // Jika waktu login terakhir tidak ada, sesi dianggap sudah kedaluwarsa
        } else {
            val currentTime = System.currentTimeMillis()
            currentTime - lastLoginTime > SESSION_TIMEOUT // Periksa apakah selisih waktu melebihi batas sesi
        }
    }

}