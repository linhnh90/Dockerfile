package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class FacilityRequestItem(
        @SerializedName("facilityId")
        var facilityId: String?,
        @SerializedName("date")
        var date: String?,
        @SerializedName("slot")
        var slot: String?,
        @SerializedName("slotId")
        var slotId: String?
)
