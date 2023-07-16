package com.styl.pa.entities.main

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class KioskUpdateRequest(
    @SerializedName("HardwareId")
    var hardwareId: String?,
    @SerializedName("HardwareVersion")
    var hardwareVersion: String?,
    @SerializedName("AppVersion")
    var appVersion: String?,
    @SerializedName("OsVersion")
    var osVersion: String?
) {
}