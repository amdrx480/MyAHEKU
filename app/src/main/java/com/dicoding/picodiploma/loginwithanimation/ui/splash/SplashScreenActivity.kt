package com.dicoding.picodiploma.loginwithanimation.ui.splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginVoucherViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenViewModel: SplashScreenViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            splashScreenViewModel.getAuthStateToken().observe(this@SplashScreenActivity) { isAuthTokenAvailable ->
                Log.d("SplashScreenActivity", "Auth Token: $isAuthTokenAvailable")
                val intent = if (isAuthTokenAvailable) {
                    Intent(this@SplashScreenActivity, MainActivity::class.java)
                } else {
                    Intent(this@SplashScreenActivity, LoginActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }, 3000)
    }
}


//    private lateinit var splashScreenViewModel: SplashScreenViewModel
//    private var splashScreenViewModel: SplashScreenViewModel by viewModels()
//        splashScreenViewModel = SplashScreenViewModel(AuthRepository(AuthPreferenceDataSource.getInstance(dataStore)))
//        splashScreenViewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)

//        handler = Handler(mainLooper)
//    private val splashScreenViewModel: SplashScreenViewModel by viewModels {
//        ViewModelUserFactory(AuthPreferenceDataSource.getInstance(dataStore))
//    }
//class SplashScreenActivity : AppCompatActivity() {
//
//    //    private lateinit var mainViewModel: MainViewModel
//    private var splashScreenViewModel: SplashScreenViewModel by viewModels()
//    lateinit var handler: Handler
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash_screen)
//        supportActionBar?.hide()
//
//        handler = Handler(mainLooper)
//        handler.postDelayed({
//            splashScreenViewModel = ViewModelProvider(
//                this,
//                ViewModelUserFactory(AuthPreferenceDataSource.getInstance(dataStore))
//            )[splashScreenViewModel::class.java]
//
//            splashScreenViewModel.getAuthToken().observe(this) {
//                if (it.isLogin) {
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                } else {
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }
//            }
//        }, 3000)
//    }
//}