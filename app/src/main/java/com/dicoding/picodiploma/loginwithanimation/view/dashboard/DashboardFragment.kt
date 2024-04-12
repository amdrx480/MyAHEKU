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
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
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
        dashboardViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(requireContext().dataStore)))[DashboardViewModel::class.java]

        dashboardViewModel.getSession().observe(this) { preferences ->
            user = UserModel(
                preferences.password,
                preferences.token,
                true
            )
        }
    }

    private fun setupAction() {
        binding.stocksButton.setOnClickListener {
            // Buat Intent
            val moveToListStoryActivity = Intent(requireActivity(), StocksActivity::class.java)

            // Ambil data sesi dari ViewModel dan tambahkan ke Intent
            dashboardViewModel.getSession().observe(viewLifecycleOwner) { user ->
                moveToListStoryActivity.putExtra(StocksActivity.EXTRA_USER, user)
                startActivity(moveToListStoryActivity)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}