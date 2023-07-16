package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class IgAvailabilityRequest (
    @SerializedName("igIds")
    var igIds: ArrayList<String?>? = null
)