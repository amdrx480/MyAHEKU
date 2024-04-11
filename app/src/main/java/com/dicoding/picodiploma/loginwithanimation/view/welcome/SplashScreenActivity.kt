package com.dicoding.picodiploma.loginwithanimation.view.welcome

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        handler = Handler(mainLooper)
        handler.postDelayed({
            mainViewModel = ViewModelProvider(
                this,
                ViewModelFactory(UserPreference.getInstance(dataStore))
            )[MainViewModel::class.java]

            mainViewModel.getSession().observe(this) {
                if (it.isLogin) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }, 3000)

    }

}