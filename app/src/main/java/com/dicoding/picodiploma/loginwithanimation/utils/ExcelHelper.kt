package com.dicoding.picodiploma.loginwithanimation.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.model.stocks.StocksEntity
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object ExcelHelper {

    private const val DEFAULT_COLUMN_WIDTH = 15 // Lebar kolom default dalam karakter

    // Fungsi utama untuk menyimpan data StocksEntity ke dalam file Excel
    suspend fun saveStocksToExcel(
        context: Context,
        stocks: List<StocksEntity>
    ): ResultResponse<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Membuat workbook baru menggunakan XSSFWorkbook (untuk format XLSX)
                val workbook = XSSFWorkbook()
                val sheet =
                    workbook.createSheet("Stocks") // Membuat sheet baru dengan nama "Stocks"

                // Membuat style untuk teks tebal di tengah
                val boldCenterStyle = workbook.createCellStyle().apply {
                    alignment = HorizontalAlignment.CENTER
                    verticalAlignment = VerticalAlignment.CENTER
                    setFont(workbook.createFont().apply {
                        bold = true
                        fontHeightInPoints = 14
                    })
                }

                // Membuat style untuk teks tebal biasa
                val boldStyle = workbook.createCellStyle().apply {
                    setFont(workbook.createFont().apply {
                        bold = true
                    })
                }

                // Membuat style untuk border seluruh cell
                val borderStyle = workbook.createCellStyle().apply {
                    borderBottom = BorderStyle.THIN
                    borderTop = BorderStyle.THIN
                    borderLeft = BorderStyle.THIN
                    borderRight = BorderStyle.THIN
                }

                // Membuat row untuk judul perusahaan
                val companyTitleRow = sheet.createRow(0)
                val companyTitleCell = companyTitleRow.createCell(0)
                companyTitleCell.setCellValue("PT. Anugrah Hadi Electric")
                companyTitleCell.cellStyle = boldCenterStyle

                // Membuat row untuk judul header data
                val titleHeaderRow = sheet.createRow(1)
                val titleHeaderCell = titleHeaderRow.createCell(0)
                titleHeaderCell.setCellValue("Data Persediaan Barang")
                titleHeaderCell.cellStyle = boldCenterStyle

                // Menggabungkan sel untuk judul perusahaan dan judul header data
                sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 7))
                sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 7))

                // Membuat header row untuk nama kolom
                val headerRow = sheet.createRow(2)
                createHeaderRow(
                    headerRow,
                    borderStyle
                ) // Memanggil fungsi createHeaderRow untuk membuat header kolom

                // Memasukkan data ke dalam rows
                var rowIndex = 3
                for (stock in stocks) {
                    val dataRow = sheet.createRow(rowIndex++)
                    createDataRow(
                        dataRow,
                        stock,
                        borderStyle
                    ) // Memanggil fungsi createDataRow untuk membuat data row
                    // Menyesuaikan lebar kolom berdasarkan header dan data
                    adjustColumnWidths(sheet, headerRow, dataRow)
                }

                // Membuat row untuk alamat perusahaan
                val addressRow =
                    sheet.createRow(rowIndex + 1) // Menambahkan satu baris setelah data rows
                val addressCell = addressRow.createCell(0)
                addressCell.setCellValue("Alamat : Jl. Sriwijaya III No.9, Perumnas 3, Kec. Karawaci, Kabupaten Tangerang, Banten 15810")
                addressCell.cellStyle = boldStyle

                // Menggabungkan sel untuk row alamat
                sheet.addMergedRegion(CellRangeAddress(rowIndex + 1, rowIndex + 1, 0, 7))

                // Menyimpan workbook ke dalam file
                val fileName = generateFileName()
                val file = File(getDownloadFolderPath(context), fileName)
                val fileOutputStream = FileOutputStream(file)
                workbook.write(fileOutputStream)
                workbook.close()

                ResultResponse.Success(file.absolutePath) // Mengembalikan path file Excel yang berhasil disimpan
            } catch (e: Exception) {
                Log.e("ExcelHelper", "Error saving stocks to Excel", e)
                ResultResponse.Error("Failed to export stocks to Excel: ${e.message}") // Mengembalikan error jika terjadi exception
            }
        }
    }

    // Fungsi untuk membuat header row dengan border style
    private fun createHeaderRow(headerRow: Row, borderStyle: CellStyle) {
        val headers = arrayOf(
            "ID",
            "Created At",
            "Stock Name",
            "Stock Code",
            "Category",
            "Unit",
            "Stock Total",
            "Selling Price"
        )
        for ((index, header) in headers.withIndex()) {
            val cell = headerRow.createCell(index, CellType.STRING)
            cell.setCellValue(header)
            cell.cellStyle = borderStyle
        }
    }

    // Fungsi untuk membuat data row dengan border style
    private fun createDataRow(dataRow: Row, stock: StocksEntity, borderStyle: CellStyle) {
        val cells = arrayOf(
            stock.id.toString(),
            stock.createdAt,
            stock.stockName,
            stock.stockCode,
            stock.categoryName,
            stock.unitName,
            stock.stockTotal.toString(),
            formatCurrency(stock.sellingPrice) // Format sellingPrice ke mata uang Rupiah
        )

        for ((index, cellValue) in cells.withIndex()) {
            val cell = dataRow.createCell(index, CellType.STRING)
            cell.setCellValue(cellValue)
            cell.cellStyle = borderStyle
        }
    }

    // Fungsi untuk format sellingPrice sebagai mata uang Rupiah
    private fun formatCurrency(price: Int): String {
        val formatter = DecimalFormat("#,###")
        return "Rp " + formatter.format(price)
    }

    // Fungsi untuk generate nama file Excel berdasarkan timestamp
    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "Stocks_$timeStamp.xlsx"
    }

    // Fungsi untuk mendapatkan folder download di External Storage
    private fun getDownloadFolderPath(context: Context): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return downloadsDir
    }

    // Fungsi untuk menyesuaikan lebar kolom berdasarkan teks di header dan data row
    private fun adjustColumnWidths(sheet: XSSFSheet, headerRow: Row, dataRow: Row) {
        for (i in 0 until headerRow.physicalNumberOfCells) {
            val headerCell = headerRow.getCell(i)
            val dataCell = dataRow.getCell(i)

            var maxWidth = DEFAULT_COLUMN_WIDTH * 256 // Lebar default dalam karakter

            // Memeriksa lebar sel header
            val headerWidth = headerCell.stringCellValue.length * 256 // Lebar kira-kira
            if (headerWidth > maxWidth) {
                maxWidth = headerWidth
            }

            // Memeriksa lebar sel data berdasarkan tipe sel
            val dataWidth = when (dataCell.cellType) {
                CellType.NUMERIC -> {
                    val numericValue = dataCell.numericCellValue
                    val formattedValue = DecimalFormat("#,###").format(numericValue)
                    formattedValue.length * 256
                }
                CellType.STRING -> {
                    dataCell.stringCellValue.length * 256 // Lebar kira-kira untuk sel string
                }
                else -> 0
            }

            if (dataWidth > maxWidth) {
                maxWidth = dataWidth
            }

            // Mengatur lebar kolom
            sheet.setColumnWidth(i, maxWidth)
        }
    }
}


//object ExcelHelper {
//
//    suspend fun saveStocksToExcel(context: Context, stocks: List<StocksEntity>): ResultResponse<String> {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Create a new workbook
//                val workbook = XSSFWorkbook()
//                val sheet = workbook.createSheet("Stocks")
//
//                // Create styles
//                val boldCenterStyle = workbook.createCellStyle().apply {
//                    alignment = HorizontalAlignment.CENTER
//                    verticalAlignment = VerticalAlignment.CENTER
//                    setFont(workbook.createFont().apply {
//                        bold = true
//                        fontHeightInPoints = 14
//                    })
//                }
//
//                val boldStyle = workbook.createCellStyle().apply {
//                    setFont(workbook.createFont().apply {
//                        bold = true
//                    })
//                }
//
//                // Create a company title row
//                val companyTitleRow = sheet.createRow(0)
//                val companyTitleCell = companyTitleRow.createCell(0)
//                companyTitleCell.setCellValue("PT. Anugrah Hadi Electric")
//                companyTitleCell.cellStyle = boldCenterStyle
//
//                // Create a title header row
//                val titleHeaderRow = sheet.createRow(1)
//                val titleHeaderCell = titleHeaderRow.createCell(0)
//                titleHeaderCell.setCellValue("Data Persediaan Barang")
//                titleHeaderCell.cellStyle = boldCenterStyle
//
//                // Merge cells for title rows
//                sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 7))
//                sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 7))
//
//                // Create a header row
//                val headerRow = sheet.createRow(2)
//                createHeaderRow(headerRow)
//
//                // Populate data rows
//                var rowIndex = 3
//                for (stock in stocks) {
//                    val dataRow = sheet.createRow(rowIndex++)
//                    createDataRow(dataRow, stock)
//                }
//
//                // Create an address row
//                val addressRow = sheet.createRow(rowIndex + 1) // Skip one row after data rows
//                val addressCell = addressRow.createCell(0)
//                addressCell.setCellValue("Alamat : Jl. Sriwijaya III No.9, Perumnas 3, Kec. Karawaci, Kabupaten Tangerang, Banten 15810")
//                addressCell.cellStyle = boldStyle
//
//                // Merge cells for address row
//                sheet.addMergedRegion(CellRangeAddress(rowIndex + 1, rowIndex + 1, 0, 7))
//
//                // Save workbook to a file
//                val fileName = generateFileName()
//                val file = File(getDownloadFolderPath(context), fileName)
//                val fileOutputStream = FileOutputStream(file)
//                workbook.write(fileOutputStream)
//                workbook.close()
//
//                ResultResponse.Success(file.absolutePath)
//            } catch (e: Exception) {
//                Log.e("ExcelHelper", "Error saving stocks to Excel", e)
//                ResultResponse.Error("Failed to export stocks to Excel: ${e.message}")
//            }
//        }
//    }
//
//    private fun createHeaderRow(headerRow: Row) {
//        val headers = arrayOf("ID", "Created At", "Stock Name", "Stock Code", "Category", "Unit", "Stock Total", "Selling Price")
//        for ((index, header) in headers.withIndex()) {
//            val cell = headerRow.createCell(index, CellType.STRING)
//            cell.setCellValue(header)
//            cell.cellStyle = createHeaderCellStyle(headerRow.sheet.workbook)
//        }
//    }
//
//    private fun createHeaderCellStyle(workbook: Workbook): CellStyle {
//        val style = workbook.createCellStyle()
//        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index)
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
//        return style
//    }
//
//    private fun createDataRow(dataRow: Row, stock: StocksEntity) {
//        val cells = arrayOf(
//            stock.id.toString(),
//            stock.createdAt,
//            stock.stockName,
//            stock.stockCode,
//            stock.categoryName,
//            stock.unitName,
//            stock.stockTotal.toString(),
//            stock.sellingPrice.toString()
//        )
//
//        for ((index, cellValue) in cells.withIndex()) {
//            val cell = dataRow.createCell(index, CellType.STRING)
//            cell.setCellValue(cellValue)
//        }
//    }
//
//    private fun generateFileName(): String {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        return "Stocks_$timeStamp.xlsx"
//    }
//
//    private fun getDownloadFolderPath(context: Context): File {
//        val downloadsDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        if (!downloadsDir.exists()) {
//            downloadsDir.mkdirs()
//        }
//        return downloadsDir
//    }
//}


//object ExcelHelper {
//
//    suspend fun saveStocksToExcel(context: Context, stocks: List<StocksEntity>): ResultResponse<String> {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Create a new workbook
//                val workbook = XSSFWorkbook()
//                val sheet = workbook.createSheet("Stocks")
//
//                // Create a company title row
//                val companyTitleRow = sheet.createRow(0)
//                companyTitleRow.createCell(0).setCellValue("PT. Anugrah Hadi Electric")
//
//                // Create a title header row
//                val titleHeaderRow = sheet.createRow(1)
//                titleHeaderRow.createCell(0).setCellValue("Data Persediaan Barang")
//
//                // Create a header row
//                val headerRow = sheet.createRow(2)
//                createHeaderRow(headerRow)
//
//                // Populate data rows
//                var rowIndex = 3
//                for (stock in stocks) {
//                    val dataRow = sheet.createRow(rowIndex++)
//                    createDataRow(dataRow, stock)
//                }
//
//                // Create an address row
//                val addressRow = sheet.createRow(rowIndex)
//                addressRow.createCell(0).setCellValue("Alamat : Jl. Sriwijaya III No.9, Perumnas 3, Kec. Karawaci, Kabupaten Tangerang, Banten 15810")
//
//                // Save workbook to a file
//                val fileName = generateFileName()
//                val file = File(getDownloadFolderPath(context), fileName)
//                val fileOutputStream = FileOutputStream(file)
//                workbook.write(fileOutputStream)
//                workbook.close()
//
//                ResultResponse.Success(file.absolutePath)
//            } catch (e: Exception) {
//                Log.e("ExcelHelper", "Error saving stocks to Excel", e)
//                ResultResponse.Error("Failed to export stocks to Excel: ${e.message}")
//            }
//        }
//    }
//
//    private fun createHeaderRow(headerRow: Row) {
//        val headers = arrayOf("ID", "Created At", "Stock Name", "Stock Code", "Category", "Unit", "Stock Total", "Selling Price")
//        for ((index, header) in headers.withIndex()) {
//            val cell = headerRow.createCell(index, CellType.STRING)
//            cell.setCellValue(header)
//            cell.cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index)
//            cell.cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
//        }
//    }
//
//    private fun createDataRow(dataRow: Row, stock: StocksEntity) {
//        val cells = arrayOf(
//            stock.id.toString(),
//            stock.createdAt,
//            stock.stockName,
//            stock.stockCode,
//            stock.categoryName,
//            stock.unitName,
//            stock.stockTotal.toString(),
//            stock.sellingPrice.toString()
//        )
//
//        for ((index, cellValue) in cells.withIndex()) {
//            val cell = dataRow.createCell(index, CellType.STRING)
//            cell.setCellValue(cellValue)
//        }
//    }
//
//    private fun generateFileName(): String {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        return "Stocks_$timeStamp.xlsx"
//    }
//
//    private fun getDownloadFolderPath(context: Context): File {
//        val downloadsDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        if (!downloadsDir.exists()) {
//            downloadsDir.mkdirs()
//        }
//        return downloadsDir
//    }
//}

//object ExcelHelper {
//
//    suspend fun saveStocksToExcel(context: Context, stocks: List<StocksEntity>): ResultResponse<String> {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Create a new workbook
//                val workbook = XSSFWorkbook()
//                val sheet = workbook.createSheet("Stocks")
//
//                // Create a header row
//                val headerRow = sheet.createRow(0)
//                createHeaderRow(headerRow)
//
//                // Populate data rows
//                var rowIndex = 1
//                for (stock in stocks) {
//                    val dataRow = sheet.createRow(rowIndex++)
//                    createDataRow(dataRow, stock)
//                }
//
//                // Save workbook to a file
//                val fileName = generateFileName()
//                val file = File(getDownloadFolderPath(context), fileName)
//                val fileOutputStream = FileOutputStream(file)
//                workbook.write(fileOutputStream)
//                workbook.close()
//
//                ResultResponse.Success(file.absolutePath)
//            } catch (e: Exception) {
//                Log.e("ExcelHelper", "Error saving stocks to Excel", e)
//                ResultResponse.Error("Failed to export stocks to Excel: ${e.message}")
//            }
//        }
//    }
//
//    private fun createHeaderRow(headerRow: Row) {
//        val headers = arrayOf("ID", "Created At", "Stock Name", "Stock Code", "Category", "Unit", "Stock Total", "Selling Price")
//        for ((index, header) in headers.withIndex()) {
//            val cell = headerRow.createCell(index, CellType.STRING)
//            cell.setCellValue(header)
//            cell.cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index)
//            cell.cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
//        }
//    }
//
//    private fun createDataRow(dataRow: Row, stock: StocksEntity) {
//        val cells = arrayOf(
//            stock.id.toString(),
//            stock.createdAt,
//            stock.stockName,
//            stock.stockCode,
//            stock.categoryName,
//            stock.unitName,
//            stock.stockTotal.toString(),
//            stock.sellingPrice.toString()
//
//        )
//
//        for ((index, cellValue) in cells.withIndex()) {
//            val cell = dataRow.createCell(index, CellType.STRING)
//            cell.setCellValue(cellValue)
//        }
//    }
//
//    private fun generateFileName(): String {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        return "Stocks_$timeStamp.xlsx"
//    }
//
//    private fun getDownloadFolderPath(context: Context): File {
//        val downloadsDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        if (!downloadsDir.exists()) {
//            downloadsDir.mkdirs()
//        }
//        return downloadsDir
//    }
//}


//object ExcelHelper {
//
//    suspend fun saveStocksToExcel(context: Context, stocks: List<StocksEntity>): ResultResponse<String> {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Create a new workbook
//                val workbook = XSSFWorkbook()
//                val sheet = workbook.createSheet("Stocks")
//
//                // Create company title row
//                val companyTitleRow = sheet.createRow(0)
//                createCompanyTitleRow(companyTitleRow, workbook)
//
//                // Create title header row
//                val titleHeaderRow = sheet.createRow(1)
//                createTitleHeaderRow(titleHeaderRow, workbook)
//
//                // Create a header row
//                val headerRow = sheet.createRow(2)
//                createHeaderRow(headerRow, workbook)
//
//                // Populate data rows
//                var rowIndex = 3
//                for (stock in stocks) {
//                    val dataRow = sheet.createRow(rowIndex++)
//                    createDataRow(dataRow, stock)
//                }
//
//                // Create address row
//                val addressRow = sheet.createRow(rowIndex)
//                createAddressRow(addressRow, workbook)
//
////                // Auto-size columns
////                for (i in 0..7) {
////                    sheet.autoSizeColumn(i)
////                }
//
//                // Save workbook to a file
//                val fileName = generateFileName()
//                val file = File(getDownloadFolderPath(context), fileName)
//                val fileOutputStream = FileOutputStream(file)
//                workbook.write(fileOutputStream)
//                workbook.close()
//
//                ResultResponse.Success(file.absolutePath)
//            } catch (e: Exception) {
//                Log.e("ExcelHelper", "Error saving stocks to Excel", e)
//                ResultResponse.Error("Failed to export stocks to Excel: ${e.message}")
//            }
//        }
//    }
//
//    private fun createCompanyTitleRow(row: Row, workbook: XSSFWorkbook) {
//        val cell = row.createCell(0, CellType.STRING)
//        cell.setCellValue("PT. Anugrah Hadi Electric")
//        val style = workbook.createCellStyle()
//        val font = workbook.createFont()
//        font.fontHeightInPoints = 14
//        font.bold = true
//        style.setFont(font)
//        cell.cellStyle = style
//    }
//
//    private fun createTitleHeaderRow(row: Row, workbook: XSSFWorkbook) {
//        val cell = row.createCell(0, CellType.STRING)
//        cell.setCellValue("Data Persediaan Barang")
//        val style = workbook.createCellStyle()
//        val font = workbook.createFont()
//        font.fontHeightInPoints = 12
//        font.bold = true
//        style.setFont(font)
//        cell.cellStyle = style
//    }
//
//    private fun createHeaderRow(headerRow: Row, workbook: XSSFWorkbook) {
//        val headers = arrayOf("ID", "Created At", "Stock Name", "Stock Code", "Category", "Unit", "Stock Total", "Selling Price")
//        for ((index, header) in headers.withIndex()) {
//            val cell = headerRow.createCell(index, CellType.STRING)
//            cell.setCellValue(header)
//            val style = workbook.createCellStyle()
//            style.fillForegroundColor = IndexedColors.LIGHT_YELLOW.index
//            style.fillPattern = FillPatternType.SOLID_FOREGROUND
//            cell.cellStyle = style
//        }
//    }
//
//    private fun createDataRow(dataRow: Row, stock: StocksEntity) {
//        val cells = arrayOf(
//            stock.id.toString(),
//            stock.createdAt,
//            stock.stockName,
//            stock.stockCode,
//            stock.categoryName,
//            stock.unitName,
//            stock.stockTotal.toString(),
//            stock.sellingPrice.toString()
//        )
//
//        for ((index, cellValue) in cells.withIndex()) {
//            val cell = dataRow.createCell(index, CellType.STRING)
//            cell.setCellValue(cellValue)
//        }
//    }
//
//    private fun createAddressRow(row: Row, workbook: XSSFWorkbook) {
//        val cell = row.createCell(0, CellType.STRING)
//        cell.setCellValue("Alamat: Jl. Sriwijaya III No.9, Perumnas 3, Kec. Karawaci, Kabupaten Tangerang, Banten 15810")
//        val style = workbook.createCellStyle()
//        val font = workbook.createFont()
//        font.fontHeightInPoints = 10
//        font.bold = true
//        style.setFont(font)
//        cell.cellStyle = style
//    }
//
//    private fun generateFileName(): String {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        return "Stocks_$timeStamp.xlsx"
//    }
//
//    private fun getDownloadFolderPath(context: Context): File {
//        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        if (!downloadsDir.exists()) {
//            downloadsDir.mkdirs()
//        }
//        return downloadsDir
//    }
//}


//object ExcelHelper {
//
//    private const val FILE_NAME = "stocks_data.xlsx"
//
//    fun saveStocksToExcel(context: Context, stocks: List<StocksEntity>) {
//        val workbook = XSSFWorkbook()
//        val sheet = workbook.createSheet("Stocks Data")
//        val headers = listOf("ID", "Stock Name", "Quantity", "Price") // Define your headers
//
//        // Create header row and get the header cell style
//        val headerCellStyle = createHeaderRow(sheet, headers, workbook)
//
//        // Populate data rows
//        stocks.forEachIndexed { index, data ->
//            createDataRow(sheet, index, data, workbook.createCellStyle())
//        }
//
//        // Save workbook to a file
//        val file = File(context.getExternalFilesDir(null), FILE_NAME)
//        saveWorkbook(workbook, file)
//
//        // File berhasil disimpan
//        Log.d("ExcelHelper", "File saved: ${file.absolutePath}")
//        // Sekarang Anda dapat memberitahukan pengguna bahwa file telah disimpan atau memberikan akses untuk membukanya.
//    }
//
//    private fun createCell(row: Row, columnIndex: Int, value: Any?, cellStyle: CellStyle) {
//        val cell = row.createCell(columnIndex)
//        when (value) {
//            is String -> cell.setCellValue(value)
//            is Int -> cell.setCellValue(value.toDouble())
//            is Double -> cell.setCellValue(value)
//            is Boolean -> cell.setCellValue(value)
//            else -> cell.setCellValue(value?.toString())
//        }
//        cell.cellStyle = cellStyle
//    }
//
//    private fun createHeaderRow(sheet: Sheet, headers: List<String>, workbook: Workbook): CellStyle {
//        val headerRow = sheet.createRow(0)
//        val headerCellStyle = workbook.createCellStyle()
//        headerCellStyle.alignment = HorizontalAlignment.CENTER
//
//        headers.forEachIndexed { index, header ->
//            val cell = headerRow.createCell(index)
//            cell.setCellValue(header)
//            cell.cellStyle = headerCellStyle
//        }
//        return headerCellStyle
//    }
//
//    private fun createDataRow(sheet: Sheet, rowIndex: Int, data: StocksEntity, cellStyle: CellStyle) {
//        val row = sheet.createRow(rowIndex + 1) // rowIndex + 1 to skip header row
//        // Populate each cell in the row with data from StocksEntity
//        createCell(row, 0, data.id, cellStyle)
//        createCell(row, 1, data.stockName, cellStyle)
////        createCell(row, 2, data.quantity, cellStyle)
////        createCell(row, 3, data.price, cellStyle)
//        // Add more columns as per your StocksEntity fields
//    }
//
//    private fun saveWorkbook(workbook: Workbook, file: File) {
//        var fileOutputStream: FileOutputStream? = null
//        try {
//            fileOutputStream = FileOutputStream(file)
//            workbook.write(fileOutputStream)
//            workbook.close()
//            fileOutputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            try {
//                fileOutputStream?.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//}