package com.dicoding.picodiploma.loginwithanimation.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AppRepository
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginVoucherViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchase.PurchaseStocksViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales.SalesStocksViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itempurchases.ItemPurchasesViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.StocksViewModel

//class ViewModelFactory private constructor(
//    private val repository: AppRepository
//) : ViewModelProvider.NewInstanceFactory() {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(StocksViewModel::class.java) -> {
//                StocksViewModel(repository) as T
//            }
//            modelClass.isAssignableFrom(PurchaseStocksViewModel::class.java) -> {
//                PurchaseStocksViewModel(repository) as T
//            }
//
//            modelClass.isAssignableFrom(ItemPurchasesViewModel::class.java) -> {
//                ItemPurchasesViewModel(repository) as T
//            }
//            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
//        }
//    }
//    companion object {
//        @Volatile
//        private var instance: ViewModelFactory? = null
//        fun getInstance(context: Context): ViewModelFactory =
//            instance ?: synchronized(this) {
//                instance ?: ViewModelFactory(Injection.provideAppRepository(context))
//            }.also { instance = it }
//    }
//}