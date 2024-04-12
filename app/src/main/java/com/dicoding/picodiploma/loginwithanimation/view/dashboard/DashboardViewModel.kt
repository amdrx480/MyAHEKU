package com.dicoding.picodiploma.loginwithanimation.view.dashboard

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference

class DashboardViewModel(private val repository: UserPreference) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}