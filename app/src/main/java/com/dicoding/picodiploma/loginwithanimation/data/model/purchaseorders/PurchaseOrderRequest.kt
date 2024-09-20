package com.dicoding.picodiploma.loginwithanimation.data.model.purchaseorders

import com.google.gson.annotations.SerializedName

class PurchaseOrderRequest(
    @field:SerializedName("ReminderTime")
    val reminderTime: String
)