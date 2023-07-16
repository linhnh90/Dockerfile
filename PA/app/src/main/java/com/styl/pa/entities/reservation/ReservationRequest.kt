package com.styl.pa.entities.reservation

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/24/2018.
 */
class ReservationRequest{
    constructor(reservationId: String?){
        this.reservationId = reservationId
    }
    @SerializedName("reservationId")
    private var reservationId: String? = ""
    var mReservationId: String?
    get() {return reservationId}
    set(value) {this.reservationId = value}
}