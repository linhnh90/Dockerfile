package com.styl.pa.entities.participant

import com.google.gson.annotations.SerializedName

class ParticipantCartError {
    @SerializedName("ItemName")
    var itemName: String? = null
    @SerializedName("ParticipantsError")
    var participantsError: ArrayList<ParticipantsError>? = null
    @SerializedName("Message")
    var message: String? = null

    class ParticipantsError {
        @SerializedName("ParticipantId")
        var participantId: String? = null
        @SerializedName("ErrorMessage")
        var errorMessage: String? = null
        @SerializedName("CrmErrorMessage")
        var crmErrorMessage: String? = null
    }
}