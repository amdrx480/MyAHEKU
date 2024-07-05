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

//    fun exportDataToExcel(context: Context) = runBlocking {
//        val db = AppDatabase.getInstance(context)
//        val stocksDao = db.stocksDao()
//
//        val stockList: List<StocksEntity> = withContext(Dispatchers.IO) {
//            stocksDao.getAllStocks()
//        }
//
//        val workbook = XSSFWorkbook()
//        val sheet = workbook.createSheet("Stocks")
//
//        val header = sheet.createRow(0)
//        val headerCellStyle = workbook.createCellStyle()
//        headerCellStyle.fillForegroundColor = IndexedColors.YELLOW.index
//        headerCellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
//
//        val headers = listOf("ID", "Created At", "Stock Name", "Stock Code", "Category Name", "Unit Name", "Stock Total", "Selling Price")
//
//        for (i in headers.indices) {
//            val cell = header.createCell(i)
//            cell.setCellValue(headers[i])
//            cell.cellStyle = headerCellStyle
//        }
//
//        for (i in stockList.indices) {
//            val row = sheet.createRow(i + 1)
//            val stock = stockList[i]
//            row.createCell(0).setCellValue(stock.id)
//            row.createCell(1).setCellValue(stock.createdAt)
//            row.createCell(2).setCellValue(stock.stockName)
//            row.createCell(3).setCellValue(stock.stockCode)
//            row.createCell(4).setCellValue(stock.categoryName)
//            row.createCell(5).setCellValue(stock.unitName)
//            row.createCell(6).setCellValue(stock.stockTotal.toDouble())
//            row.createCell(7).setCellValue(stock.sellingPrice.toDouble())
//        }
//
//        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "stocks.xlsx")
//        FileOutputStream(file).use { outputStream ->
//            workbook.write(outputStream)
//            Log.d("Helper", "Data exported to Excel successfully")
//        }
//        workbook.close()
//    }
//    private fun exportDataToExcel(context: Context, data: List<StocksEntity>) {
//        val workbook = XSSFWorkbook()
//        val sheet = workbook.createSheet("Stocks")
//
//        // Header Row
//        val header = sheet.createRow(0)
//        header.createCell(0).setCellValue("ID")
//        header.createCell(1).setCellValue("Created At")
//        header.createCell(2).setCellValue("Stock Name")
//        header.createCell(3).setCellValue("Stock Code")
//        header.createCell(4).setCellValue("Category Name")
//        header.createCell(5).setCellValue("Unit Name")
//        header.createCell(6).setCellValue("Stock Total")
//        header.createCell(7).setCellValue("Selling Price")
//
//        // Data Rows
//        data.forEachIndexed { index, stock ->
//            val row = sheet.createRow(index + 1)
//            row.createCell(0).setCellValue(stock.id)
//            row.createCell(1).setCellValue(stock.createdAt)
//            row.createCell(2).setCellValue(stock.stockName)
//            row.createCell(3).setCellValue(stock.stockCode)
//            row.createCell(4).setCellValue(stock.categoryName)
//            row.createCell(5).setCellValue(stock.unitName)
//            row.createCell(6).setCellValue(stock.stockTotal.toDouble())
//            row.createCell(7).setCellValue(stock.sellingPrice.toDouble())
//        }
//
//        // Save to file
//        val file = File(context.getExternalFilesDir(null), "stocks_data.xlsx")
//        FileOutputStream(file).use { outputStream ->
//            workbook.write(outputStream)
//            workbook.close()
//        }
//    }
}

