package com.dicoding.picodiploma.loginwithanimation.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginVoucherViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.DashboardViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itempurchases.ItemPurchasesViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.itemtransactions.ItemTransactionsViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchase.PurchaseStocksViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales.SalesStocksViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.StocksViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.profile.ProfileViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.splash.SplashScreenViewModel

class ViewModelUserFactory(private val pref: AuthRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(pref) as T
            }

            modelClass.isAssignableFrom(LoginVoucherViewModel::class.java) -> {
                LoginVoucherViewModel(pref) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }

            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(pref) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(pref) as T
            }

            modelClass.isAssignableFrom(StocksViewModel::class.java) -> {
                StocksViewModel(pref) as T
            }

            modelClass.isAssignableFrom(PurchaseStocksViewModel::class.java) -> {
                PurchaseStocksViewModel(pref) as T
            }

            modelClass.isAssignableFrom(ItemPurchasesViewModel::class.java) -> {
                ItemPurchasesViewModel(pref) as T
            }

            modelClass.isAssignableFrom(ItemTransactionsViewModel::class.java) -> {
                ItemTransactionsViewModel(pref) as T
            }

            modelClass.isAssignableFrom(SalesStocksViewModel::class.java) -> {
                SalesStocksViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelUserFactory? = null

        fun getInstance(context: Context): ViewModelUserFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelUserFactory(Injection.provideAuthRepository(context))
            }.also { instance = it }
    }



//class ViewModelUserFactory(private val pref: AuthPreferenceDataSource) : ViewModelProvider.NewInstanceFactory() {

//    companion object {
//        @Volatile
//        private var instance: ViewModelUserFactory? = null
//        fun getInstance(authRepository: AuthRepository): ViewModelUserFactory =
//            instance ?: synchronized(this) {
//                instance ?: ViewModelUserFactory(authRepository)
//            }.also { instance = it }
//    }

//    companion object {
//        @Volatile
//        private var instance: ViewModelUserFactory? = null
//        fun getInstance(dataStore: DataStore<Preferences>): ViewModelUserFactory =
//            instance ?: synchronized(this) {
//                instance ?: ViewModelUserFactory(Injection.provideAuthRepository(dataStore))
//            }.also { instance = it }
//    }
}