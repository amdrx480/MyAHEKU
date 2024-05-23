package com.dicoding.picodiploma.loginwithanimation.service.data.customers

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class AllCustomersResponse(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: List<ListCustomersItem>
)

@Parcelize
data class ListCustomersItem(

    @SerializedName("id")
    val id: Int,

    @SerializedName("customer_address")
    val customer_address: String,

    @SerializedName("updated_at")
    val updated_at: String,

    @SerializedName("customer_email")
    val customer_email: String,

    @SerializedName("customer_phone")
    val customer_phone: String,

    @SerializedName("created_at")
    val created_at: String,

    @SerializedName("customer_name")
    val customer_name: String,

    @SerializedName("cart_items")
    val cart_items: List<CartItem>,

) : Parcelable

@Parcelize
data class CartItem(

    @SerializedName("selling_price")
    val selling_price: Int,

    @SerializedName("quantity")
    val quantity: String,

    @SerializedName("updated_at")
    val updated_at: String,

    @SerializedName("stock_name")
    val stock_name: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("created_at")
    val created_at: String,

    @SerializedName("id")
    val id: Int,

    @SerializedName("customer_name")
    val customer_name: String,

    @SerializedName("customer_id")
    val customer_id: Int,


    @SerializedName("stock_id")
    val stock_id: Int
) : Parcelable

//    @SerializedName("deleted_at")
//    val deletedAt: Any,



//data class AllCustomersResponse(
//
//    @SerializedName("error")
//    val error: Boolean,
//
//    @SerializedName("message")
//    val message: String,
//
//    @SerializedName("token")
//    val token: List<ListCustomersItem>,
//
////    @SerializedName("cart_items")
////    val tokenCartItem: List<CartItem>,
//    )
//
//@Parcelize
//data class ListCustomersItem(
//
//    @SerializedName("id")
//    val id: Int,
//
//    @SerializedName("customer_name")
//    val customer_Name: String,
//
//    @SerializedName("customer_address")
//    val customer_Address: String,
//
//    @SerializedName("customer_email")
//    val customer_Email: String,
//
//    @SerializedName("customer_phone")
//    val customer_Phone: String,
//
//    @SerializedName("cart_items")
//    val cart_items: List<CartItem>
//
//) : Parcelable
//
//@Parcelize
//data class CartItem(
//    @SerializedName("id")
//    val id: Int,
//
//    @SerializedName("stock_name")
//    val stock_name: String,
//
//    @SerializedName("quantity")
//    val quantity: String,
//
//    @SerializedName("price")
//    val price: String,
//) : Parcelable
