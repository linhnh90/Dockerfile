package com.styl.pa.entities

import com.google.gson.annotations.SerializedName

data class CertData(
    @SerializedName("Key")
    var key: String? = null,
    @SerializedName("Cert")
    var cert: String? = null
)