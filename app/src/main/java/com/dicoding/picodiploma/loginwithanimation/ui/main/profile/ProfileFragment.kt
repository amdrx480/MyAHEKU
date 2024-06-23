package com.dicoding.picodiploma.loginwithanimation.ui.main.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentProfileBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.local.AuthPreferenceDataSource
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }
//    private lateinit var user: UserModel

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
        setupAction()
        setupObservers()
    }

    private fun setupObservers() {
        profileViewModel.authToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                profileViewModel.fetchAdminProfile().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultResponse.Loading -> {
                            // Tampilkan loading indicator
                        }
                        is ResultResponse.Success -> {
                            // Tampilkan data profil
                            val profile = result.data
                            with(binding) {
                                Glide.with(this@ProfileFragment)
                                    .load(profile.imagePath) // URL gambar dari backend
                                    .placeholder(R.drawable.ic_place_default_holder) // Gambar placeholder jika diperlukan
                                    .error(R.drawable.ic_broken_image) // Gambar error jika terjadi kesalahan
                                    .into(imageViewPerson) // ImageView untuk menampilkan gambar

                                tvNamePerson.text = profile.name
                                tvRole.text = profile.roleName
                            }
                            // Isi field lainnya sesuai data profil
                        }
                        is ResultResponse.Error -> {
                            // Tampilkan pesan error
//                            Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // Tampilkan pesan bahwa token tidak ditemukan
                Toast.makeText(requireContext(), "Token not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayProfile(profile: ProfileModel) {
        // Tampilkan data profil ke UI
        binding.apply {
            // Misalnya, terapkan data ke TextViews atau ImageView sesuai kebutuhan
            tvNamePerson.text = profile.name
            tvRole.text = profile.roleName
//            textViewPhone.text = profile.phone
            // dll.
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
                moveToListStoryActivity.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                // Ambil data sesi dari ViewModel dan tambahkan ke Intent
                profileViewModel.logoutVoucher()
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

//private fun setupViewModel() {
//    profileViewModel = ViewModelProvider(
//        this,
//        ViewModelUserFactory(AuthPreferenceDataSource.getInstance(requireContext().dataStore))
//    )[ProfileViewModel::class.java]
//
//    profileViewModel.getSession().observe(this) { preferences ->
//        user = UserModel(
//            preferences.password,
//            preferences.token,
//            true
//        )
//    }
//}