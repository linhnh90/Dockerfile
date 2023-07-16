package com.styl.pa.entities.pacesRequest.addEventParticipant

import com.google.gson.annotations.SerializedName

class AddEventParticipantRequest {
    @SerializedName("cartId")
    var cartId: String? = null

    @SerializedName("Participants", alternate = ["participants"])
    var participants: ArrayList<EventParticipantItem>? = null
}