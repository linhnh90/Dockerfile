package com.styl.pa.entities.cart

import com.google.gson.annotations.SerializedName

class TicketGroup {
    @SerializedName("AvailableVacancies", alternate = ["availableVacancies"])
    var availableVacancies: Int? = null

    @SerializedName("IsRegistrationOpen", alternate = ["isRegistrationOpen"])
    var isRegistrationOpen: Boolean? = null

    @SerializedName("MaxBookingAllowed", alternate = ["maxBookingAllowed"])
    var maxBookingAllowed: Int? = null

    @SerializedName("MaxPrice", alternate = ["maxPrice"])
    var maxPrice: Float? = null

    @SerializedName("Qty", alternate = ["qty"])
    var qty: Int? = null

    @SerializedName("TicketId", alternate = ["ticketId"])
    var ticketId: String? = null

    @SerializedName("TicketMapCount", alternate = ["ticketMapCount"])
    var ticketMapCount: Int? = null

    @SerializedName("TicketName", alternate = ["ticketName"])
    var ticketName: String? = null

    @SerializedName("Tickets", alternate = ["tickets"])
    var tickets: ArrayList<Ticket>? = null

    @SerializedName("TotalVacancies", alternate = ["totalVacancies"])
    var totalVacancies: Int? = null
}