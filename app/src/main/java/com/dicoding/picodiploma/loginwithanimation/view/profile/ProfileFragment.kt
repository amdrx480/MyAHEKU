package com.dicoding.picodiploma.loginwithanimation.view.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentProfileBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var user: UserModel

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
    }

private fun setupViewModel() {
    profileViewModel = ViewModelProvider(
        this,
        ViewModelUserFactory(UserPreference.getInstance(requireContext().dataStore))
    )[ProfileViewModel::class.java]

    profileViewModel.getSession().observe(this) { preferences ->
        user = UserModel(
            preferences.password,
            preferences.token,
            true
        )
    }
}

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            // Tampilkan dialog konfirmasi logout
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Anda yakin ingin keluar?")
            builder.setPositiveButton("Ya") { dialog, _ ->
                // Jika pengguna memilih "Ya", lakukan logout
                // Buat Intent untuk menuju ke halaman login
                val moveToListStoryActivity = Intent(requireActivity(), LoginActivity::class.java)
                // Clear task dan membuat halaman login menjadi aktivitas teratas yang baru
                moveToListStoryActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                // Ambil data sesi dari ViewModel dan tambahkan ke Intent
                profileViewModel.logout()
                // Mulai activity login dan tutup activity saat ini
                startActivity(moveToListStoryActivity)
                requireActivity().finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("Tidak") { dialog, _ ->
                // Jika pengguna memilih "Tidak", tutup dialog
                dialog.dismiss()
            }
            builder.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}