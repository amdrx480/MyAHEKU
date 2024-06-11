package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityHistoriesBinding
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.DashboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class HistoriesActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var binding: ActivityHistoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityHistoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) // Set action bar


        // Get token from intent
        token = intent.getStringExtra(DashboardFragment.EXTRA_TOKEN) ?: ""

        // Setup Bottom Navigation View
        val navView: BottomNavigationView = binding.historiesBottomNavigationView

        // Setup NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_histories)

        // Configure the AppBar with the NavController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_item_purchases, R.id.navigation_item_transactions
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Setup the view
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
}



//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

//class HistoriesActivity : AppCompatActivity() {
//
//    private lateinit var token: String
//    private lateinit var binding: ActivityHistoriesBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //si bangsat ini kenapa pake double setContentView
////        setContentView(R.layout.activity_histories)
//
//        binding = ActivityHistoriesBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        token = intent.getStringExtra(DashboardFragment.EXTRA_TOKEN) ?: ""
//
//        val navView: BottomNavigationView = binding.historiesBottomNavigationView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_histories)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_item_purchases, R.id.navigation_item_transactions
//            )
//        )
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//
//        setupView()
//    }
//
//    private fun setupView() {
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
//        supportActionBar?.hide()
//    }
//
//    companion object {
//        const val EXTRA_TOKEN = "extra_token"
//    }
//}



//        setSupportActionBar(binding.toolbar) // Set action bar

//        replaceFragment(ItemPurchasesFragment.newInstance(token))
////        replaceFragment(ItemTransactionsFragment.newInstance(token))
//
//
//
//        binding.historiesBottomNavigationView.setOnItemSelectedListener {
//
//            when (it.itemId) {
//                R.id.navigation_item_purchases -> replaceFragment(ItemPurchasesFragment()
//                )
//                R.id.navigation_item_transactions -> replaceFragment(ItemTransactionsFragment())
//                else -> {
//                }
//            }
//            true
//        }
//


//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frame_layout, fragment)
//        fragmentTransaction.commit()
//    }


////private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
//
//class HistoriesActivity : AppCompatActivity() {
//
//    //    private lateinit var historiesViewModel: HistoriesViewModel
////    private lateinit var user: UserModel
//    private var token: String = ""
//    private lateinit var binding: ActivityHistoriesBinding
//
////    private val historiesViewModel: HistoriesViewModel by viewModels {
////        ViewModelFactory.getInstance(this)
////    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_histories)
//
//        binding = ActivityHistoriesBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Contoh cara mendapatkan UserModel, misalnya dari Intent atau sumber lain
////        user = intent.getStringExtra(EXTRA_TOKEN)!!
////        token = intent.getStringExtra(DashboardFragment.EXTRA_TOKEN)!!
//        replaceFragment(ItemPurchasesFragment.newInstance(token))
//
//
//        binding.historiesBottomNavigationView.setOnItemSelectedListener {
//
//            when (it.itemId) {
//                R.id.navigation_item_purchases -> replaceFragment(
//                    ItemPurchasesFragment()
////                        .newInstance(
////                        token
////                    )
//                )
//                R.id.navigation_item_transactions -> replaceFragment(ItemTransactionsFragment())
//                else -> {
//                }
//            }
//            true
//        }
//
//        setupView()
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frame_layout, fragment)
//        fragmentTransaction.commit()
//    }
//
//    private fun setupView() {
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
//        supportActionBar?.hide()
//    }
//
//    companion object {
//        const val EXTRA_TOKEN = "extra_token"
//    }
//
////    companion object {
////        const val EXTRA_USER = "user"
////    }
//}
//    private fun setupViewModel() {
//        //perlu menggukaan requireContext().dataStore pada fragment
//        historiesViewModel = ViewModelProvider(this, ViewModelUserFactory(UserPreference.getInstance(dataStore)))[HistoriesViewModel::class.java]
//
//        historiesViewModel.getSession().observe(this) { preferences ->
//            user = UserModel(
//                preferences.password,
//                preferences.token,
//                true
//            )
//        }
//    }