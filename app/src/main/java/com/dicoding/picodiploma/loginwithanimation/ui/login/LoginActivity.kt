package com.dicoding.picodiploma.loginwithanimation.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.loginwithvoucher.LoginWithVoucherRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.MainActivity


//@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginVoucherViewModel: LoginVoucherViewModel by viewModels {
        ViewModelUserFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupLoginVoucherAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupLoginVoucherAction() {
        binding.loginButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()
            Log.d("LoginActivity", "Attempting login with password: $password")

            val loginWithVoucherRequest = loginWithVoucherRequest(password)
            loginVoucherViewModel.loginVoucher(loginWithVoucherRequest).observe(this) { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        Log.d("LoginActivity", "Login loading...")
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultResponse.Success -> {
                        Log.d("LoginActivity", "Login success: ${result.data}")
                        binding.progressBar.visibility = View.GONE
                        val token = result.data.loginWithVoucherResult.token
                        Log.d("LoginActivity", "Received token: $token")

                        loginVoucherViewModel.saveAuthVoucher(token)
                        loginVoucherViewModel.saveAuthToken(token)
                        loginVoucherViewModel.saveAuthStateToken(true)

                        showAlertDialog(true, getString(R.string.login_in_success))
                    }
                    is ResultResponse.Error -> {
                        Log.e("LoginActivity", "Login error: ${result.error}")
                        binding.progressBar.visibility = View.GONE
                        showAlertDialog(false, result.error)
                    }
                }
            }
        }
    }

    private fun showAlertDialog(success: Boolean, message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        if (success) {
            dialogBuilder.apply {
                setTitle(getString(R.string.information_success))
                setMessage(getString(R.string.login_in_success))
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.information_failed))
                setMessage(getString(R.string.login_in_failed) + ", $message")
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    binding.progressBar.visibility = View.GONE
                }
                create()
                show()

            }
        }
    }

    private fun loginWithVoucherRequest(
        voucher: String,
    ): LoginWithVoucherRequest {
        return LoginWithVoucherRequest(
            voucher = voucher,
        )
    }
}



//    companion object {
//        const val EXTRA_TOKEN = "extra_token"
//    }

//            loginVoucherViewModel.loginResponse(password).observe(this){
//                if (it != null){
//                    if (it.error == true){
//                        showLoading(false)
//                        Snackbar.make(binding.root, getString(R.string.login_failed_please_try_later), Toast.LENGTH_SHORT).show()
//                    }else{
//                        showLoading(false)
//                        intent = Intent(this, MainActivity::class.java)
//                        loginVoucherViewModel.saveAuthVoucher(true)
//                        loginVoucherViewModel.saveAuthToken(it.loginResult?.token!!)
//                        loginVoucherViewModel.saveAuthStateToken(it.loginResult.name!!)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//            }

//    private fun setupLoginVoucherAction() {
//        binding.loginButton.setOnClickListener {
//            val password = binding.passwordEditText.text.toString()
//            Log.d("LoginActivity", "Attempting login with password: $password")
//            loginVoucherViewModel.loginVoucher(password)
//
//            loginVoucherViewModel.loginResult.observe(this) {
//                when (it) {
//                    is ResultResponse.Loading -> {
//                        Log.d("LoginActivity", "Login loading...")
//                        binding.progressBar.visibility = View.VISIBLE
//                    }
//                    is ResultResponse.Success -> {
//                        Log.d("LoginActivity", "Login success: ${it.data}")
//
//                        binding.progressBar.visibility = View.GONE
//                        val token = it.data.loginWithVoucherResult.token
//                        Log.d("LoginActivity", "Received token: $token")
//                        showAlertDialog(true, getString(R.string.login_in_success))
//
//                        loginVoucherViewModel.saveAuthToken(token)
////                        showAlertDialog(true, getString(R.string.login_in_success))
//                    }
//                    is ResultResponse.Error -> {
//                        Log.e("LoginActivity", "Login error: ${it.error}")
//                        binding.progressBar.visibility = View.GONE
//                        showAlertDialog(false, it.error)
//                    }
//                }
//            }
//        }
//    }


//    private fun showAlertDialog(param: Boolean, message: String) {
//        if (param) {
//            AlertDialog.Builder(this).apply {
//                setTitle(getString(R.string.information_success))
//                setMessage(getString(R.string.login_in_success))
//                setPositiveButton(getString(R.string.continue_)) { _, _ ->
////                    loginVoucherViewModel.loginVoucher()
//                    val intent = Intent(context, MainActivity::class.java).apply {
////                        putExtra(EXTRA_TOKEN, loginVoucherViewModel.getAuthToken().value)
//                        loginVoucherViewModel.saveAuthVoucher(it.loginResult?.voucher)
//                        loginVoucherViewModel.saveAuthToken(it.loginResult?.token)
//                        loginVoucherViewModel.saveAuthStateToken(true)
//                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//
//                    }
////                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(intent)
//                    finish()
//                }
//                create()
//                show()
//            }
//import dagger.hilt.android.AndroidEntryPoint

//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//    private val loginVoucherViewModel: LoginVoucherViewModel by viewModels()

//    private val loginViewModel: LoginViewModel  by viewModels {
//        ViewModelFactory.getInstance(this)
//    }
//        setupViewModel()
//        setupLoginAction()
//        showLoading()
//                        loginVoucherViewModel.saveAuthToken(token)
//                        showAlertDialog(true, getString(R.string.login_in_success))
//                        val token = it.data.loginWithVoucherResult.firstOrNull()?.token
//                        if (token != null) {

//                        val userPref = AuthPreferenceDataSource.getInstance(dataStore)
//                        lifecycleScope.launchWhenStarted {
//                            userPref.saveSession(user)
//                        }
//    private fun setupLoginAction() {
//        binding.loginButton.setOnClickListener {
//            val password = binding.passwordEditText.text.toString()
//
//            loginViewModel.login(password).observe(this){
//                when (it) {
//                    is ResultResponse.Loading -> {
//                        binding.progressBar.visibility = View.VISIBLE
//                    }
//                    is ResultResponse.Success -> {
//                        binding.progressBar.visibility = View.GONE
//                        val user = UserModel(
//                            password,
//                            it.data.token,
//                            true
//                        )
//                        showAlertDialog(true, getString(R.string.login_in_success))
//
//                        val userPref = AuthPreferenceDataSource.getInstance(dataStore)
//                        lifecycleScope.launchWhenStarted {
//                            userPref.saveSession(user)
//                        }
//                    }
//                    is ResultResponse.Error -> {
//                        binding.progressBar.visibility = View.GONE
//                        showAlertDialog(false, it.error)
//                    }
//                }
//            }
//        }
//    }

//                        val token = it.data.loginWithVoucherResult.firstOrNull()?.token
//                        if (token != null) {
//                            loginVoucherViewModel.saveAuthToken(token)
//                            showAlertDialog(true, getString(R.string.login_in_success))
//
//                        }
