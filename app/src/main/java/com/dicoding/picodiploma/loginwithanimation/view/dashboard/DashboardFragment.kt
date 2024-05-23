package com.dicoding.picodiploma.loginwithanimation.view.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentDashboardBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.purchase.PurchaseStockActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.view.cart.CartActivity
import com.dicoding.picodiploma.loginwithanimation.view.stocks.StocksActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var user: UserModel

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        //perlu menggukaan requireContext().dataStore pada fragment
        dashboardViewModel = ViewModelProvider(this, ViewModelUserFactory(UserPreference.getInstance(requireContext().dataStore)))[DashboardViewModel::class.java]

        dashboardViewModel.getSession().observe(this) { preferences ->
            user = UserModel(
                preferences.password,
                preferences.token,
                true
            )
        }
    }

    private fun setupAction() {
        binding.purchaseStocksButton.setOnClickListener {
            val moveToPurchaseStocksActivity = Intent(requireActivity(), PurchaseStockActivity::class.java)
            moveToPurchaseStocksActivity.putExtra(PurchaseStockActivity.EXTRA_USER, user)
            startActivity(moveToPurchaseStocksActivity)
        }
        binding.salesStockButton.setOnClickListener {
            val moveToSalesStockActivity = Intent(requireActivity(), CartActivity::class.java)
            moveToSalesStockActivity.putExtra(CartActivity.EXTRA_USER, user)
            startActivity(moveToSalesStockActivity)
        }
        binding.stocksButton.setOnClickListener {
            val moveToStocksActivity = Intent(requireActivity(), StocksActivity::class.java)
            moveToStocksActivity.putExtra(StocksActivity.EXTRA_USER, user)
            startActivity(moveToStocksActivity)
            //penyebab repeating ListStockItem
//            dashboardViewModel.getSession().observe(viewLifecycleOwner) { user ->
//                moveToStocksActivity.putExtra(StocksActivity.EXTRA_USER, user)
//                startActivity(moveToStocksActivity)
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}