package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository

//class HistoriesViewModel(private val repository: AuthRepository) : ViewModel() {
//
//}
//class HistoriesViewModel(private val repository: AuthPreferenceDataSource) : ViewModel() {
//    fun getSession(): LiveData<UserModel> {
//        return repository.getSession().asLiveData()
//    }
//}