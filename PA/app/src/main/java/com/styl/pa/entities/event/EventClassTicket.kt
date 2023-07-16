package com.styl.pa.entities.event

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class EventClassTicket() : Parcelable {
    @SerializedName("availableQuantity")
    var availableQuantity: Int? = null

    @SerializedName("beginDate")
    var beginDate: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("endDate")
    var endDate: String? = null

    @SerializedName("maxBookingAllowed")
    var maxBookingAllowed: Int? = null

    @SerializedName("price")
    var price: Float? = null

    @SerializedName("quantity")
    var quantity: Int? = null

    @SerializedName("ticketId")
    var ticketId: String? = null

    @SerializedName("ticketMapCount")
    var ticketMapCount: Int? = null

    @SerializedName("ticketName")
    var ticketName: String? = null

    @SerializedName("type")
    var type: String? = null

    constructor(parcel: Parcel) : this() {
        availableQuantity = parcel.readValue(Int::class.java.classLoader) as? Int
        beginDate = parcel.readString()
        description = parcel.readString()
        endDate = parcel.readString()
        maxBookingAllowed = parcel.readValue(Int::class.java.classLoader) as? Int
        price = parcel.readValue(Float::class.java.classLoader) as? Float
        quantity = parcel.readValue(Int::class.java.classLoader) as? Int
        ticketId = parcel.readString()
        ticketMapCount = parcel.readValue(Int::class.java.classLoader) as? Int
        ticketName = parcel.readString()
        type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(availableQuantity)
        parcel.writeString(beginDate)
        parcel.writeString(description)
        parcel.writeString(endDate)
        parcel.writeValue(maxBookingAllowed)
        parcel.writeValue(price)
        parcel.writeValue(quantity)
        parcel.writeString(ticketId)
        parcel.writeValue(ticketMapCount)
        parcel.writeString(ticketName)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventClassTicket> {
        override fun createFromParcel(parcel: Parcel): EventClassTicket {
            return EventClassTicket(parcel)
        }

        override fun newArray(size: Int): Array<EventClassTicket?> {
            return arrayOfNulls(size)
        }
    }
}