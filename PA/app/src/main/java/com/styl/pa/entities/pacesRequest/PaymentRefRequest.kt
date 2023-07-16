package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class PaymentRefRequest (
        @SerializedName("customer")
        var customerName: String?,
        @SerializedName("uniqueReference")
        var uniqueReference: String?,
        @SerializedName("amount")
        var amount: Float?,
        @SerializedName("expiryDateTime")
        var expiryDateTime: String?,
        @SerializedName("paymentMode")
        var paymentMode: String? = PAYMENT_MODE_CARD,
        @SerializedName("merchantId")
        var merchantId: String? = null
) {
        companion object {
                const val PAYMENT_MODE_CARD = "ekioskCreditCard"
                const val PAYMENT_MODE_NETS = "ekiosknets"
                const val PAYMENT_MODE_EZLINK = "ekioskezlink"
        }
}