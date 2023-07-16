package com.styl.pa.entities.event

import com.google.gson.annotations.SerializedName

class TicketEntity {
    @SerializedName("externalLineId")
    var externalLineId: String? = null

    var listParticipant: ArrayList<EventParticipant> = ArrayList()

    var beforeDiscountAmount: Float? = null //subTotal
    var discountAmount: Float? = null //discountTotal
    var paymentAmount: Float? = null //grandTotal
    var promoDiscountAmount: Float? = null
    var ticketTypeName: String? = null
}