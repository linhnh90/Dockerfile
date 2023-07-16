package com.styl.pa.entities.ota

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

/**
 * Created by NguyenHang on 9/16/2020.
 */
class OtaStatusRequest {
    @SerializedName("KioskId")
    @Expose
    var kioskId: Int? = null

    @SerializedName("Type")
    @Expose
    var type: String? = null

    @SerializedName("NewVersion")
    @Expose
    var newVersion: String? = null

    @SerializedName("ErrorStatus")
    @Expose
    var errorStatus: String? = null

    @SerializedName("ErrorDescription")
    @Expose
    var errorDescription: String? = null

}