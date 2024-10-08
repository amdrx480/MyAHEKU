package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileModel
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentDashboardBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchase.PurchaseStockActivity
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.sales.CartActivity
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.histories.HistoriesActivity
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchaseorder.PurchaseOrderActivity
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchaseorder.PurchaseOrderViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.stocks.StocksActivity
import com.dicoding.picodiploma.loginwithanimation.utils.helper
import com.dicoding.picodiploma.loginwithanimation.utils.helper.formatTime

//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }

    private val purchaseOrderViewModel: PurchaseOrderViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }

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

        setupAction()
        setupObservers()
    }


    private fun setupAction() {
        binding.purchaseOrderButton.setOnClickListener {
            val moveToPurchaseOrderActivity =
                Intent(requireActivity(), PurchaseOrderActivity::class.java)
            moveToPurchaseOrderActivity.putExtra(
                PurchaseOrderActivity.EXTRA_TOKEN,
                dashboardViewModel.authToken.value
            )

            startActivity(moveToPurchaseOrderActivity)
        }
        binding.purchaseStocksButton.setOnClickListener {
            val moveToPurchaseStocksActivity =
                Intent(requireActivity(), PurchaseStockActivity::class.java)
            moveToPurchaseStocksActivity.putExtra(
                PurchaseStockActivity.EXTRA_TOKEN,
                dashboardViewModel.authToken.value
            )
            startActivity(moveToPurchaseStocksActivity)
        }
        binding.salesStockButton.setOnClickListener {
            val moveToSalesStockActivity = Intent(requireActivity(), CartActivity::class.java)
//            moveToSalesStockActivity.putExtra(CartActivity.EXTRA_TOKEN, dashboardViewModel.getAuthToken().value)
            startActivity(moveToSalesStockActivity)
        }

        binding.historiesButton.setOnClickListener {
            val moveToHistoriesActivity = Intent(requireActivity(), HistoriesActivity::class.java)
            moveToHistoriesActivity.putExtra(
                HistoriesActivity.EXTRA_TOKEN,
                dashboardViewModel.authToken.value
            )
            startActivity(moveToHistoriesActivity)
        }

        binding.stocksButton.setOnClickListener {
            val moveToStocksActivity = Intent(requireActivity(), StocksActivity::class.java)
            moveToStocksActivity.putExtra(
                StocksActivity.EXTRA_TOKEN,
                dashboardViewModel.authToken.value
            )
            startActivity(moveToStocksActivity)
        }
    }


    private fun setupObservers() {
        dashboardViewModel.authToken.observe(viewLifecycleOwner) { token ->
            // Log the token value
            if (token != null) {
                dashboardViewModel.fetchAdminProfile().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultResponse.Loading -> {
                            // Tampilkan loading indicator jika diperlukan
                        }
                        is ResultResponse.Success -> {
                            // Tampilkan data profil
                            val profile = result.data
                            binding.tvUserName.text = "Hello ${profile.name}, Selamat Datang"
                        }
                        is ResultResponse.Error -> {
                            // Tampilkan pesan error
                            Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                // Tampilkan pesan bahwa token tidak ditemukan
                Toast.makeText(requireContext(), "Token not found", Toast.LENGTH_SHORT).show()
            }
            Log.d("DashboardFragment", "Auth Token: $token")
        }

        dashboardViewModel.authVoucher.observe(viewLifecycleOwner) { voucher ->
            // Log the voucher value
            Log.d("DashboardFragment", "Auth Voucher: $voucher")
        }

        dashboardViewModel.isAuthTokenAvailable.observe(viewLifecycleOwner) { isAvailable ->
            // Log the auth state value
            Log.d("DashboardFragment", "Auth State Available: $isAvailable")
        }

        purchaseOrderViewModel.purchaseOrder.observe(viewLifecycleOwner) { purchaseOrders ->
            purchaseOrders?.let { orders ->
                if (orders.isNotEmpty()) {
                    // Format tanggal menggunakan fungsi formatTime dari helper
                    // Akses elemen pertama dari daftar yang di mulai dari 0
                    val formattedDate = formatTime(orders[0].reminderTime)
                    binding.tvSchedule.text = "Jadwal Pengiriman: $formattedDate"
                } else {
                    binding.tvSchedule.text = "Tidak ada jadwal pengiriman"
                }
            }
        }

        purchaseOrderViewModel.currentDayOrderCount.observe(viewLifecycleOwner) { count ->
            binding.tvCurrentDayOrderCount.text = "($count)"
        }

        // Observe the currentDayOrdersText LiveData
        purchaseOrderViewModel.currentDayOrdersText.observe(viewLifecycleOwner) { message ->
            binding.tvCurrentDayOrders.text = message
        }

        // Observe deliveryAddressText LiveData
        purchaseOrderViewModel.deliveryAddressText.observe(viewLifecycleOwner) { addressText ->
            binding.tvDeliveryAddress.text = addressText
        }

//        purchaseOrderViewModel.deliveryAddressText.observe(viewLifecycleOwner) { address ->
//            val textViewDeliveryAddress = binding.tvDeliveryAddress
//            if (address.isNullOrEmpty()) {
//                // Tidak ada alamat pengiriman
//                textViewDeliveryAddress.text = "Tidak ada pengiriman hari ini"
//            } else {
//                // Ada alamat pengiriman
//                textViewDeliveryAddress.text = "Kirim barang ke $address"
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}


//penyebab repeating ListStockItem
//            dashboardViewModel.getSession().observe(viewLifecycleOwner) { user ->
//                moveToStocksActivity.putExtra(StocksActivity.EXTRA_USER, user)
//                startActivity(moveToStocksActivity)
//            }

//    private fun setupViewModel() {
//        //perlu menggukaan requireContext().dataStore pada fragment
//        dashboardViewModel.authToken.observe(this) { preferences ->
//
////            user = UserModel(
////                preferences.password,
////                preferences.token,
////                true
////            )
//        }
//    }
//    private fun setupViewModel() {
//        //perlu menggukaan requireContext().dataStore pada fragment
//        dashboardViewModel = ViewModelProvider(this, ViewModelUserFactory(AuthPreferenceDataSource.getInstance(requireContext().dataStore)))[DashboardViewModel::class.java]
//
//        dashboardViewModel.getSession().observe(this) { preferences ->
//            user = UserModel(
//                preferences.password,
//                preferences.token,
//                true
//            )
//        }
//    }