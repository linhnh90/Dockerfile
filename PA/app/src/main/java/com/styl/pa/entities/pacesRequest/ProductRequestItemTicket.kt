package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class ProductRequestItemTicket {
    @SerializedName("ticketId")
    var ticketId: String? = null

    @SerializedName("qty")
    var qty: String? = null

    @SerializedName("externalId")
    var externalId: ArrayList<String>? = null
}