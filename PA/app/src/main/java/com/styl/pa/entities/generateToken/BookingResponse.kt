package com.styl.pa.entities.generateToken

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class BookingResponse() : Parcelable {
    @SerializedName("CartId", alternate = ["cartId"])
    var cartId: String? = null
    @SerializedName("SessionId", alternate = ["sessionId"])
    var sessionId: String? = null
    @SerializedName("CartItemsCount", alternate = ["cartItemsCount"])
    var cartItemCount: Int = 0

    constructor(parcel: Parcel) : this() {
        cartId = parcel.readString()
        sessionId = parcel.readString()
        cartItemCount = parcel.readValue(Int::class.java.classLoader) as Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cartId)
        parcel.writeString(sessionId)
        parcel.writeValue(cartItemCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingResponse> {
        override fun createFromParcel(parcel: Parcel): BookingResponse {
            return BookingResponse(parcel)
        }

        override fun newArray(size: Int): Array<BookingResponse?> {
            return arrayOfNulls(size)
        }
    }
}