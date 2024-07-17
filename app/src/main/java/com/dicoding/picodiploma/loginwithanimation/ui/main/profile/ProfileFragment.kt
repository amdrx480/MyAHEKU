package com.dicoding.picodiploma.loginwithanimation.ui.main.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
import com.dicoding.picodiploma.loginwithanimation.data.model.profile.ProfileRequest
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelUserFactory
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.utils.FileUtil
import com.google.android.material.snackbar.Snackbar
import java.io.File

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelUserFactory.getInstance(requireContext())
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageFile: File? = null // File yang dipilih dari penyimpanan

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
        setupObservers()
        setupActions()
    }

    private fun setupObservers() {
        profileViewModel.authToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                profileViewModel.fetchAdminProfile().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultResponse.Loading -> {
                            // Tampilkan loading indicator jika diperlukan
                        }
                        is ResultResponse.Success -> {
                            // Tampilkan data profil
                            val profile = result.data
                            updateUI(profile)
                        }
                        is ResultResponse.Error -> {
                            // Tampilkan pesan error
                            Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // Tampilkan pesan bahwa token tidak ditemukan
                Toast.makeText(requireContext(), "Token not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(profile: ProfileModel) {
        with(binding) {
            Glide.with(this@ProfileFragment)
                .load(profile.imagePath) // URL gambar dari backend
                .placeholder(R.drawable.ic_place_default_holder) // Gambar placeholder jika diperlukan
                .error(R.drawable.ic_broken_image) // Gambar error jika terjadi kesalahan
                .into(imageViewPerson) // ImageView untuk menampilkan gambar

            etNamePerson.setText(profile.name)
            etEmail.setText(profile.email)
            etPhone.setText(profile.phone)
        }
    }

    private fun updateProfile() {
        val name = binding.etNamePerson.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()

        val profileRequest = ProfileRequest(name, email, phone, selectedImageFile)
        profileViewModel.updateAdminProfile(profileRequest)
    }

    private fun setupActions() {
        binding.btnSelectImage.setOnClickListener {
            // Panggil metode untuk memilih gambar dari penyimpanan
            openImagePicker()
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // Konversi URI menjadi file
                selectedImageFile = FileUtil.from(requireContext(), uri)
                binding.imageViewPerson.setImageURI(uri) // Tampilkan gambar yang dipilih
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi Logout")
        builder.setMessage("Anda yakin ingin keluar?")
        builder.setPositiveButton("Ya") { dialog, _ ->
            performLogout()
            dialog.dismiss()
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun performLogout() {
        val moveToListStoryActivity = Intent(requireActivity(), LoginActivity::class.java)
        moveToListStoryActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        profileViewModel.logoutVoucher()
        startActivity(moveToListStoryActivity)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_IMAGE_PICKER = 100
    }
}




//class ProfileFragment : Fragment() {
//
//    private val profileViewModel: ProfileViewModel by viewModels {
//        ViewModelUserFactory.getInstance(requireContext())
//    }
//
//    private var _binding: FragmentProfileBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentProfileBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setupAction()
//        setupObservers()
//    }
//
//    private fun setupObservers() {
//        profileViewModel.authToken.observe(viewLifecycleOwner) { token ->
//            if (token != null) {
//                profileViewModel.fetchAdminProfile().observe(viewLifecycleOwner) { result ->
//                    when (result) {
//                        is ResultResponse.Loading -> {
//                            // Tampilkan loading indicator
//                        }
//                        is ResultResponse.Success -> {
//                            // Tampilkan data profil
//                            val profile = result.data
//                            with(binding) {
//                                Glide.with(this@ProfileFragment)
//                                    .load(profile.imagePath) // URL gambar dari backend
//                                    .placeholder(R.drawable.ic_place_default_holder) // Gambar placeholder jika diperlukan
//                                    .error(R.drawable.ic_broken_image) // Gambar error jika terjadi kesalahan
//                                    .into(imageViewPerson) // ImageView untuk menampilkan gambar
//
//                                tvNamePerson.text = profile.name
//                                tvRole.text = profile.roleName
//                                tvPhone.text = profile.phone
//                                tvEmail.text = profile.email
//                            }
//                        }
//                        is ResultResponse.Error -> {
//                            // Tampilkan pesan error
//                            Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            } else {
//                // Tampilkan pesan bahwa token tidak ditemukan
//                Toast.makeText(requireContext(), "Token not found", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun setupAction() {
//        binding.logoutButton.setOnClickListener {
//            // Tampilkan dialog konfirmasi logout
//            val builder = AlertDialog.Builder(requireContext())
//            builder.setTitle("Konfirmasi Logout")
//            builder.setMessage("Anda yakin ingin keluar?")
//            builder.setPositiveButton("Ya") { dialog, _ ->
//                // Jika pengguna memilih "Ya", lakukan logout
//                // Buat Intent untuk menuju ke halaman login
//                val moveToListStoryActivity = Intent(requireActivity(), LoginActivity::class.java)
//                // Clear task dan membuat halaman login menjadi aktivitas teratas yang baru
//                moveToListStoryActivity.flags =
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                // Ambil data sesi dari ViewModel dan tambahkan ke Intent
//                profileViewModel.logoutVoucher()
//                // Mulai activity login dan tutup activity saat ini
//                startActivity(moveToListStoryActivity)
//                requireActivity().finish()
//                dialog.dismiss()
//            }
//            builder.setNegativeButton("Tidak") { dialog, _ ->
//                // Jika pengguna memilih "Tidak", tutup dialog
//                dialog.dismiss()
//            }
//            builder.show()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}