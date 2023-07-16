package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class EventAvailabilityRequest(
    @SerializedName("eventId")
    var eventId: String? = null,

    @SerializedName("isMember")
    var isMember: Boolean? = null,

    @SerializedName("isResident")
    var isResident: Boolean? = null
)

