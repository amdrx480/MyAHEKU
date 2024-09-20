package com.dicoding.picodiploma.loginwithanimation.ui.main.dashboard.purchaseorder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.local.repository.AuthRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders.PurchaseOrderModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.ResultResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class PurchaseOrderViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _purchaseOrder = MutableLiveData<List<PurchaseOrderModel>>()
    val purchaseOrder: LiveData<List<PurchaseOrderModel>> get() = _purchaseOrder

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> get() = _authToken

    private val _currentDayOrderCount = MutableLiveData<Int>()
    val currentDayOrderCount: LiveData<Int> get() = _currentDayOrderCount

    private val _currentDayOrdersText = MutableLiveData<String>()
    val currentDayOrdersText: LiveData<String> get() = _currentDayOrdersText

    private val _deliveryAddressText = MutableLiveData<String>()
    val deliveryAddressText: LiveData<String> get() = _deliveryAddressText

    init {
        loadAuthToken()
        authToken.observeForever { token ->
            token?.let {
                getPurchaseOrder(it)
            }
        }
    }

    private fun getPurchaseOrder(token: String?) {
        viewModelScope.launch {
            authRepository.getPurchaseOrder(token).observeForever { result ->
                when (result) {
                    is ResultResponse.Loading -> {
                        Log.d("PurchaseOrderViewModel", "Fetching PurchaseOrder: Loading...")
                    }
                    is ResultResponse.Success -> {
                        val now = OffsetDateTime.now()

                        // Sort data by:
                        // 1. If the reminderTime is before or after now (past or future)
                        // 2. The reminderTime itself (nearest time first)
                        val sortedData = result.data.sortedWith(compareBy(
                            { OffsetDateTime.parse(it.reminderTime).isBefore(now) },  // Past or future
                            { OffsetDateTime.parse(it.reminderTime) }  // Reminder time
                        ))

                        _purchaseOrder.value = sortedData

                        // Calculate orders for today
                        val today = now.toLocalDate()
                        val ordersToday = sortedData.count {
                            OffsetDateTime.parse(it.reminderTime).toLocalDate() == today
                        }
                        _currentDayOrderCount.value = ordersToday

                        // Update the message for today’s orders
                        _currentDayOrdersText.value = if (ordersToday > 0) {
                            "Ada PO hari ini"
                        } else {
                            "Tidak ada PO hari ini"
                        }

                        val deliveryAddress = sortedData.firstOrNull()?.deliveryAddress
                        _deliveryAddressText.value = if (deliveryAddress.isNullOrEmpty()) {
                            "Tidak ada pengiriman hari ini"
                        } else {
                            "Kirim barang ke $deliveryAddress"
                        }

                        Log.d("PurchaseOrderViewModel", "Fetching PurchaseOrder: Success")
                    }
                    is ResultResponse.Error -> {
                        Log.e("PurchaseOrderViewModel", "Error loading PurchaseOrder: ${result.error}")
                    }
                }
            }
        }
    }

    private fun loadAuthToken() {
        viewModelScope.launch {
            authRepository.getAuthToken()
                .catch { e ->
                    Log.e("PurchaseOrderViewModel", "Error loading auth token", e)
                }
                .collect { token ->
                    _authToken.postValue(token)
                }
        }
    }
}


//                        // Sort the data based on reminderTime
//                        val sortedData = result.data.sortedBy { purchaseOrder ->
//                            // Convert reminderTime to a comparable format
//                            OffsetDateTime.parse(purchaseOrder.reminderTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
//                        }