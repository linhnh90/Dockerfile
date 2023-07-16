package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class CheckoutRequest (
        @SerializedName("cartId")
        var cartId: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("mobile")
        var mobile: String?,
        @SerializedName("paymentReference")
        var paymentReference: String?,
        @SerializedName("paymentMethod")
        var paymentMethod: String? = PaymentRefRequest.PAYMENT_MODE_CARD,
        @SerializedName("tnCAccepted")
        var tnCAccepted: Boolean? = true,
        @SerializedName("bookForOther")
        var bookForOther: Boolean? = false
)