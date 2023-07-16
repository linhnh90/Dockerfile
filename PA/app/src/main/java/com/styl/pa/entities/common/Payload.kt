package com.styl.pa.entities.common

import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("payload", alternate = ["Payload"])
    var payload: String? = null
)
