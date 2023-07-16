package com.styl.pa.entities.payment

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 10/9/2018
 */
class PaymentResponse(
    @SerializedName("txnNo")
    var txnNo: String?
) {
}