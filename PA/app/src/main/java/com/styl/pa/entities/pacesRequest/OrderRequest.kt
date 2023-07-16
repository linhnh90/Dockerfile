package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class OrderRequest (
        @SerializedName("email")
        var email: String?,
        @SerializedName("cartId")
        var cartId: String?,
        @SerializedName("amount")
        var amount: Float?,
        @SerializedName("reference")
        var reference: String?,
        @SerializedName("paymentReference")
        var paymentReference: String?,
        @SerializedName("billingReference")
        var billingReference: String?,
        @SerializedName("paymentCode")
        var paymentCode: String?,
        @SerializedName("status")
        var status: String?
)