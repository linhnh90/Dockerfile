package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class Meta {
    @SerializedName("maxAge")
    var maxAge: Int? = null

    @SerializedName("maxTicketQty")
    var maxTicketQty: Int? = null

    @SerializedName("minAge")
    var minAge: Int? = null

    @SerializedName("minTicketQty")
    var minTicketQty: Int? = null

    @SerializedName("nationality")
    var nationality: Int? = null

    @SerializedName("residentType")
    var residentType: Int? = null

    @SerializedName("ticketFor")
    var ticketFor: Int? = null

    @SerializedName("crmFeeId")
    var crmFeeId: String? = null
}