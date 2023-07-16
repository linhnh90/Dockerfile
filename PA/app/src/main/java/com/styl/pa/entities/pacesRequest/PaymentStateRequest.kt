package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class PaymentStateRequest (
        @SerializedName("referenceId")
        var referenceId: String?,
        @SerializedName("state")
        var state: String?
)