package com.styl.pa.entities.pacesRequest.addEventParticipant

import com.google.gson.annotations.SerializedName

class EventParticipantItem {
    @SerializedName("itemName")
    var itemName: String? = null

    @SerializedName("externalLineId", alternate = ["externalLineID"])
    var externalLineId: String? = null

    @SerializedName("userId")
    var userId: String? = null

    @SerializedName("componentId")
    var componentId: String? = null

    @SerializedName("FormData")
    var formData: HashMap<String, String>? = null
}