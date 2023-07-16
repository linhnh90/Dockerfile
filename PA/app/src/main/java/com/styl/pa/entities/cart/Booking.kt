package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Booking() : Parcelable {
    @SerializedName("Date", alternate = ["date"])
    var date: String? = null
    @SerializedName("EndDate")
    var endDate: String? = null
    @SerializedName("FacilityName")
    var facilityName: String? = null
    @SerializedName("price")
    var price: PriceInfo? = null
    @SerializedName("qty")
    var qty: Int? = null
    @SerializedName("time", alternate = ["Time"])
    var time: String? = null
    @SerializedName("Title")
    var title: String? = null
    @SerializedName("isReservationSuccess", alternate = ["IsReservationSuccess"])
    var isReservationSuccess: Boolean? = null
    @SerializedName("reservationErrorMessageCRM", alternate = ["ReservationErrorMessageCRM"])
    var reservationErrorMessageCRM: String? = null
    @SerializedName("errorMessage", alternate = ["ErrorMesssage"])
    var errorMesssage: String? = null
    @SerializedName("errorCode", alternate = ["ErrorCode"])
    var errorCode: Int? = null
    @SerializedName("Participant")
    var participant: Participant? = null

    constructor(parcel: Parcel) : this() {
        date = parcel.readString()
        endDate = parcel.readString()
        facilityName = parcel.readString()
        price = parcel.readParcelable(PriceInfo::class.java.classLoader)
        qty = parcel.readValue(Int::class.java.classLoader) as? Int
        time = parcel.readString()
        title = parcel.readString()
        isReservationSuccess = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        reservationErrorMessageCRM = parcel.readString()
        errorMesssage = parcel.readString()
        errorCode = parcel.readValue(Int::class.java.classLoader) as? Int
        participant = parcel.readParcelable(Participant::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(endDate)
        parcel.writeString(facilityName)
        parcel.writeParcelable(price, flags)
        parcel.writeValue(qty)
        parcel.writeString(time)
        parcel.writeString(title)
        parcel.writeValue(isReservationSuccess)
        parcel.writeString(reservationErrorMessageCRM)
        parcel.writeString(errorMesssage)
        parcel.writeValue(errorCode)
        parcel.writeParcelable(participant, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Booking> {
        override fun createFromParcel(parcel: Parcel): Booking {
            return Booking(parcel)
        }

        override fun newArray(size: Int): Array<Booking?> {
            return arrayOfNulls(size)
        }
    }

}