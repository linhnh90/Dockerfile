package com.styl.pa.entities.payment

import com.google.gson.annotations.SerializedName

class PaymentData<T> {
    @SerializedName("success")
    var success: Boolean? = null
    @SerializedName("errorCode", alternate = ["responseStatusCode"])
    var errorCode: Int? = null
    @SerializedName("message", alternate = ["errorMessages"])
    var message: String? = null
    @SerializedName("result", alternate = ["response"])
    var result: T? = null
}