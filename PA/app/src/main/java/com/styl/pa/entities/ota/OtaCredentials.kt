package com.styl.pa.entities.ota

import com.google.gson.annotations.SerializedName

class OtaCredentials {
    @SerializedName("privateKey")
    var privateKey: String? = null
    @SerializedName("clientCert")
    var clientCert: String? = null
}