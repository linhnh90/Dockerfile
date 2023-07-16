package com.styl.pa.entities.kioskactivation

import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.Outlet

/**
 * Created by trangpham on 9/26/2018
 */
class KioskInfo(
    @SerializedName("location")
    var outlet: Outlet?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("hardwareId")
    var hardwareId: String?,
    @SerializedName("createdAt")
    var createdAt: Long?,
    @SerializedName("activatedAt")
    var activatedAt: Long?,
    @SerializedName("updatedAt")
    var updatedAt: Long?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("activationCode")
    var activationCode: String?,
    @SerializedName("locationId")
    var locationId: Int?,
    @SerializedName("accessKey")
    var accessKey: String?,
    @SerializedName("secretKey")
    var secretKey: String?,
    @SerializedName("appVersion")
    var appVersion: String?,
    @SerializedName("osVersion")
    var osVersion: String?,
    @SerializedName("hardwareVersion")
    var hardwareVersion: String?

) {

    companion object {

        const val STATUS_REACTIVATED = 2
    }
}