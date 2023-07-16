package com.styl.pa.entities.main

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class KioskAuthenticationRequest(
    @SerializedName("AccessKey")
    var accessKey: String?,
    @SerializedName("SecretKey")
    var secretKey: String?
) {
}