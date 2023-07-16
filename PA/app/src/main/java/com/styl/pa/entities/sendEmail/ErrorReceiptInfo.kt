package com.styl.pa.entities.sendEmail

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 11/19/2019.
 */
class ErrorReceiptInfo(
        @SerializedName("errorCode")
        var errorCode: String?,

        @SerializedName("errorMessage")
        var errorMessage: String?,

        @SerializedName("txnNo")
        var txnNo: String?,

        @SerializedName("lastTriedTime")
        var lastTriedTime: Long?) {
}