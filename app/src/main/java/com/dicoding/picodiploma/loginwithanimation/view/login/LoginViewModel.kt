package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository

class LoginViewModel(private val repository: AppRepository) : ViewModel() {
    fun login(pass: String) =
        repository.login(pass)
}