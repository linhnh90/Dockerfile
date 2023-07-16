package com.styl.pa.entities.pacesRequest

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.event.TicketEntity

class EventTicket() : Parcelable {
    @SerializedName("attractions")
    var attractions: String? = null

    @SerializedName("availableQty")
    var availableQty: Int? = null

    @SerializedName("beginDate")
    var beginDate: String? = null

    @SerializedName("created")
    var created: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("endDate")
    var endDate: String? = null

    @SerializedName("eventId")
    var eventId: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("maxQty")
    var maxQty: Int? = null

    @SerializedName("meta")
    var meta: Meta? = null

    @SerializedName("minQty")
    var minQty: Int? = null

    @SerializedName("modified")
    var modified: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("price")
    var price: Float? = null

    @SerializedName("qty")
    var qty: Int? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("visibleBeginDate")
    var visibleBeginDate: String? = null

    @SerializedName("visibleEndDate")
    var visibleEndDate: String? = null

    var isSelected: Boolean = false
    var selectedQty: Int? = null
    var listTicketParticipantEntity: ArrayList<TicketEntity> = ArrayList()
    var isAllParticipantRequired: Boolean = false
    var ticketMapCount: Int = 1
    var isExpandContent: Boolean = true
    var isEnablePlus: Boolean = false
    var isEnableMinus: Boolean = false

    constructor(parcel: Parcel) : this() {
        attractions = parcel.readString()
        availableQty = parcel.readValue(Int::class.java.classLoader) as? Int
        beginDate = parcel.readString()
        created = parcel.readString()
        description = parcel.readString()
        endDate = parcel.readString()
        eventId = parcel.readString()
        id = parcel.readString()
        maxQty = parcel.readValue(Int::class.java.classLoader) as? Int
        minQty = parcel.readValue(Int::class.java.classLoader) as? Int
        modified = parcel.readString()
        name = parcel.readString()
        price = parcel.readValue(Float::class.java.classLoader) as? Float
        qty = parcel.readValue(Int::class.java.classLoader) as? Int
        status = parcel.readString()
        visibleBeginDate = parcel.readString()
        visibleEndDate = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
        selectedQty = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(attractions)
        parcel.writeValue(availableQty)
        parcel.writeString(beginDate)
        parcel.writeString(created)
        parcel.writeString(description)
        parcel.writeString(endDate)
        parcel.writeString(eventId)
        parcel.writeString(id)
        parcel.writeValue(maxQty)
        parcel.writeValue(minQty)
        parcel.writeString(modified)
        parcel.writeString(name)
        parcel.writeValue(price)
        parcel.writeValue(qty)
        parcel.writeString(status)
        parcel.writeString(visibleBeginDate)
        parcel.writeString(visibleEndDate)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeValue(selectedQty)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventTicket> {
        override fun createFromParcel(parcel: Parcel): EventTicket {
            return EventTicket(parcel)
        }

        override fun newArray(size: Int): Array<EventTicket?> {
            return arrayOfNulls(size)
        }
    }
}