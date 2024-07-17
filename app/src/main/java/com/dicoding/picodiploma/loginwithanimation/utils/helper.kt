package com.dicoding.picodiploma.loginwithanimation.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dicoding.picodiploma.loginwithanimation.data.local.database.AppDatabase
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object helper {

//    private const val DATE_FORMAT = "dd-MMM-yyyy"
//
//    private val timeStamp: String = SimpleDateFormat(
//        DATE_FORMAT,
//        Locale.US
//    ).format(System.currentTimeMillis())

    interface ApiCallBackString {
        fun onResponse(success: Boolean, message: String)
    }

    fun formatToRupiah(price: Double): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatRupiah.format(price)
    }

    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateFormat(currentDateString: String, targetTimeZone: String): String {
        val instant = Instant.parse(currentDateString)
        val dTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
            .withZone(ZoneId.of(targetTimeZone))
        return dTimeFormatter.format(instant)
    }
}

