package com.styl.pa.entities.main

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class KioskAuthenticationResponse(
    @SerializedName("token")
    var token: String?,
    @SerializedName("expires")
    var expiry: Int?
) {
}