package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OutletType {
    @SerializedName("outletDetailTypeId")
    @Expose
    private var outletDetailTypeId: String? = null
    @SerializedName("outletName")
    @Expose
    private var outletName: String? = null

    fun getOutletDetailTypeId(): String? {
        return outletDetailTypeId
    }

    fun setOutletDetailTypeId(outletDetailTypeId: String) {
        this.outletDetailTypeId = outletDetailTypeId
    }

    fun getOutletName(): String? {
        return outletName
    }

    fun setOutletName(outletName: String) {
        this.outletName = outletName
    }
}