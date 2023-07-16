package com.styl.pa.entities.common

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("response", alternate = ["Response"])
    var response: String? = null
)
