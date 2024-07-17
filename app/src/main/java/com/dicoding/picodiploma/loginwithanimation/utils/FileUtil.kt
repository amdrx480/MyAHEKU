package com.dicoding.picodiploma.loginwithanimation.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtil {
    fun from(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = createTempFile(context, getFileName(context, uri), inputStream)
        inputStream?.close()
        return tempFile
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    // Coba ambil nama dari OpenableColumns.DISPLAY_NAME
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = if (displayNameIndex != -1) {
                        it.getString(displayNameIndex)
                    } else {
                        // Jika tidak tersedia, fallback ke mendapatkan nama dari URI path
                        uri.pathSegments.lastOrNull()
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown"
    }

    private fun createTempFile(context: Context, fileName: String?, inputStream: InputStream?): File? {
        var file: File? = null
        if (fileName != null && inputStream != null) {
            try {
                file = File(context.cacheDir, fileName)
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }
}
