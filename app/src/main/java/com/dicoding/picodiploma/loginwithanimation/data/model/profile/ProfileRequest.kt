package com.dicoding.picodiploma.loginwithanimation.data.model.profile

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


data class ProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val imageFile: File? // Tambahkan properti untuk file gambar
) {
    fun toRequestBody(): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        // Add profile data
        builder.addFormDataPart("data", """
            {
                "name": "$name",
                "email": "$email",
                "phone": "$phone"
            }
        """.trimIndent())

        // Add image file if available
        imageFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            builder.addFormDataPart("image", it.name, requestFile)
        }

        return builder.build()
    }
}
//data class ProfileRequest(
//    val name: String,
//    val email: String,
//    val phone: String
//) {
//    fun toRequestBody(): RequestBody {
//        return MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("data", """
//                {
//                    "name": "$name",
//                    "email": "$email",
//                    "phone": "$phone"
//                }
//            """.trimIndent())
//            .build()
//    }
//}

//data class ProfileRequest(
//    val name: String,
//    val email: String,
//    val phone: String
//) {
//    fun toRequestBody(): RequestBody {
//        val formData = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("name", name)
//            .addFormDataPart("email", email)
//            .addFormDataPart("phone", phone)
//            .build()
//        return formData
//    }
//}