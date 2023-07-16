package com.styl.pa.entities.cart

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.facility.SlotSessionInfo
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.interestgroup.InterestGroup

class CartItem() {

    @SerializedName("id")
    var id: String? = null
    @SerializedName("classInfo")
    var classInfo: ClassInfo? = null
    @SerializedName("igInfo")
    var igInfo: InterestGroup? = null
    @SerializedName("facility")
    var facility: Facility? = null
    @SerializedName("event")
    var event: EventInfo? = null
    @SerializedName("attendees")
    var attendees: MutableList<Attendee>? = null
    @SerializedName("noOfEvent")
    var noOfEvent: Int? = null
    @SerializedName("selectedDate")
    var selectedDate: String? = null
    @SerializedName("slotList")
    var slotList: MutableList<SlotSessionInfo>? = null

    constructor(id: String?, classInfo: ClassInfo?, facility: Facility?, event: EventInfo?,
                attendees: MutableList<Attendee>?, noOfEvent: Int?, selectedDate: String?,
                slotList: MutableList<SlotSessionInfo>?, igInfo: InterestGroup? = null) : this() {
        this.id = id
        this.classInfo = classInfo
        this.facility = facility
        this.event = event
        this.attendees = attendees
        this.noOfEvent = noOfEvent
        this.selectedDate = selectedDate
        this.slotList = slotList
        this.igInfo = igInfo
    }

    @SerializedName("isIndemnity")
    var isIndemnity: Boolean? = null

    fun getItemId(): String? {
        var itemId = classInfo?.getClassId()
        if (itemId.isNullOrEmpty()) {
            itemId = igInfo?.igId
        }
        if (itemId.isNullOrEmpty()) {
            itemId = facility?.getResourceID()
        }
        if (itemId.isNullOrEmpty()) {
            itemId = event?.eventId
        }
        return itemId
    }

    fun getSku(): String? {
        var sku = classInfo?.sku
        if (sku.isNullOrEmpty()) {
            sku = igInfo?.sku
        }
        if (sku.isNullOrEmpty()) {
            sku = facility?.sku
        }
        if (sku.isNullOrEmpty()) {
            sku = event?.sku
        }
        return sku
    }

    fun copyCartItem(): Any {
        val stringCartItem = Gson().toJson(this, CartItem::class.java)
        return Gson().fromJson(stringCartItem, CartItem::class.java)
    }
}