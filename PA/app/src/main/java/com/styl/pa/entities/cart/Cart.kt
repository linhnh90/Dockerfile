package com.styl.pa.entities.cart

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.customer.CustomerInfo

class Cart(
        @SerializedName("items")
        var items: MutableList<CartItem>?,
        @SerializedName("payer")
        var payer: CustomerInfo?,
        @SerializedName("sessionCode")
        var sessionCode: String?
) {
    var isLocked: Boolean? = false

    var hasReservation: Boolean? = null

    fun copyCart(): Cart {
        val stringCart = Gson().toJson(this, Cart::class.java)
        return Gson().fromJson(stringCart, Cart::class.java)
    }
}