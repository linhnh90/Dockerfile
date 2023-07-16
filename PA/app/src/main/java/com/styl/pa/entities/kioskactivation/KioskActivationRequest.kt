package com.styl.pa.entities.kioskactivation

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class KioskActivationRequest(
    @SerializedName("hardwareId")
    var hardwareId: String?,
    @SerializedName("activateCode")
    var activationCode: String?
) {
}