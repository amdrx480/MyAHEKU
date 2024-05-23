package com.dicoding.picodiploma.loginwithanimation.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.service.data.AppRepository
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.view.purchase.PurchaseStocksViewModel
import com.dicoding.picodiploma.loginwithanimation.view.cart.SalesStocksViewModel
import com.dicoding.picodiploma.loginwithanimation.view.stocks.StocksViewModel

class ViewModelFactory private constructor(
    private val repository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StocksViewModel::class.java) -> {
                StocksViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PurchaseStocksViewModel::class.java) -> {
                PurchaseStocksViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SalesStocksViewModel::class.java) -> {
                SalesStocksViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideAppRepository(context))
            }.also { instance = it }
    }
}