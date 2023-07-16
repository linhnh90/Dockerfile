package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResourceFeeList {
    @SerializedName("feeId")
    @Expose
    private var feeId: String? = null
    @SerializedName("feeName")
    @Expose
    private var feeName: String? = null
    @SerializedName("feeNormalAmount", alternate = ["feeAmount"])
    @Expose
    private var feeNormalAmount: String? = null
    @SerializedName("feePeakAmount", alternate = ["peakAmount"])
    @Expose
    private var feePeakAmount: String? = null

    @SerializedName("feeType")
    var feeType: String? = null

    @SerializedName("name")
    var name: String? = null


    fun getFeeId(): String? {
        return feeId
    }

    fun setFeeId(feeId: String) {
        this.feeId = feeId
    }

    fun getFeeName(): String? {
        return feeName
    }

    fun setFeeName(feeName: String) {
        this.feeName = feeName
    }

    fun getFeeNormalAmount(): String? {
        return feeNormalAmount
    }

    fun setFeeNormalAmount(feeNormalAmount: String) {
        this.feeNormalAmount = feeNormalAmount
    }

    fun getFeePeakAmount(): String? {
        return feePeakAmount
    }

    fun setFeePeakAmount(feePeakAmount: String) {
        this.feePeakAmount = feePeakAmount
    }
}