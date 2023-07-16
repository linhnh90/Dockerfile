package com.styl.pa.entities.participant

import com.google.gson.annotations.SerializedName

class ParticipantResponse {
    @SerializedName("CartError")
    var cartError: ArrayList<ParticipantCartError>? = null
    @SerializedName("ErrorValidation")
    var errValidation: ErrorValidation? = null
}