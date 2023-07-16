package com.styl.pa.entities.reservation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 09/26/2018.
 */
class ReceiptResponse {
    @SerializedName("receiptid")
    @Expose
    private var receiptid: String? = null

    fun getReceiptid(): String? {
        return receiptid
    }

    fun setReceiptid(receiptid: String) {
        this.receiptid = receiptid
    }
}