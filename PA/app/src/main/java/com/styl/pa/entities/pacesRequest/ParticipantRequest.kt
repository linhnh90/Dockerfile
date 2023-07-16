package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class ParticipantRequest<T>(
        @SerializedName("cartId")
        var cartId: String?,
        @SerializedName("itemId")
        var itemId: String?,
        @SerializedName("participant")
        var participant: ArrayList<T>?
)
