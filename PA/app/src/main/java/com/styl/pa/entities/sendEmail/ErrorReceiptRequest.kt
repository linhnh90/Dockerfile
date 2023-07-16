package com.styl.pa.entities.sendEmail

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 11/15/2019.
 */
class ErrorReceiptRequest(
        @SerializedName("Data")
        var data: ArrayList<ErrorReceiptInfo>?) {
}