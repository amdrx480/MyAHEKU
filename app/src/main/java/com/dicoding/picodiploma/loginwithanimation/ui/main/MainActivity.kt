package com.dicoding.picodiploma.loginwithanimation.ui.main

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginVoucherViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

//Aheku
//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private val mainViewModel: MainViewModel by viewModels {
//        ViewModelUserFactory.getInstance(this)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) // Set action bar

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_data_master, R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupView()
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

//    private fun setupViewModel() {
//        mainViewModel.getAuthToken().observe(this) { authToken ->
////            val navController = findNavController(R.id.nav_host_fragment_activity_main)
//////            val action = navController.navigate(R.id.navigation_dashboard, bundle)
////            val action = MainFragmentDirections.actionMainFragmentToDashboardFragment(authToken)
////            navController.navigate(action)
//
//        }
//    }
//        mainViewModel.authToken.observe(this) { token ->
//            if (token != null) {
//
//        } else {
//        }
//        }
//    }



//    private fun setupAction() {
//        binding.stocksButton.setOnClickListener {
//            val moveToListStoryActivity = Intent(this@MainActivity, StocksActivity::class.java)
//            moveToListStoryActivity.putExtra(StocksActivity.EXTRA_USER, user)
//            startActivity(moveToListStoryActivity)
//        }
//        binding.logoutButton.setOnClickListener {
//            mainViewModel.logout()
//            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//            finish()
//        }
//    }

//    private fun setupViewModel() {
//        mainViewModel = ViewModelProvider(
//            this,
//            ViewModelUserFactory(UserPreference.getInstance(dataStore))
//        )[MainViewModel::class.java]
//
//        mainViewModel.getSession().observe(this) { preferences ->
//            user = UserModel(
//                preferences.password,
//                preferences.token,
//                true
//            )
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // Hentikan observasi pada LiveData saat aktivitas dihancurkan
//        mainViewModel.getSession().removeObservers(this)
//    }
//
}