package com.styl.pa.entities.cart

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.reservation.ProductInfo

class Attendee(
        @SerializedName("reservationId")
        var reservationId: String?,
        @SerializedName("customerInfo")
        var customerInfo: CustomerInfo?,
        @SerializedName("productInfo")
        var productInfo: ProductInfo?
) {
    @SerializedName("isIndemnity")
    var isIndemnity: Boolean = false

    var isReservationSuccess: Boolean? = null

    constructor(isReservationSuccess: Boolean): this (null, null, null) {
        this.isReservationSuccess = isReservationSuccess
    }

    fun copyAttendee(): Attendee {
        val stringAttendee = Gson().toJson(this, Attendee::class.java)
        return Gson().fromJson(stringAttendee, Attendee::class.java)
    }
}