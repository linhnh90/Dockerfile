package com.styl.pa.entities.reservation

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/22/2018.
 */
class BookingAuthResponse {
    @SerializedName("reservationID")
    private var ReservationID: String? = null
    var mReservationID: String?
        get() {
            return ReservationID
        }
        set(value) {
            this.ReservationID = value
        }

    @SerializedName("isValid")
    private var isValid: Boolean = true
    var mIsValid: Boolean
        get() {
            return isValid
        }
        set(value) {
            this.isValid = value
        }
}