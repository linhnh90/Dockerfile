package com.styl.pa.entities.checkResidency

import com.google.gson.annotations.SerializedName

class CheckResidencyResponse {
    @SerializedName("isResident")
    var isResident: Boolean? = null
}