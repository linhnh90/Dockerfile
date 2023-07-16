package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Item() : Parcelable {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("outlet")
    var outlet: String? = null
    @SerializedName("outletId")
    var outletId: String? = null
    @SerializedName("productType")
    var productType: String? = null
    @SerializedName("productCode")
    var productCode: String? = null
    @SerializedName("itemId")
    var itemId: String? = null
    @SerializedName("qty")
    var qty: Double? = null
    @SerializedName("isRegistrationOpen")
    var isRegistrationOpen: Boolean? = null
    @SerializedName("isSkillsFutureCourse")
    var isSkillsFutureCourse: Boolean? = null
    @SerializedName("totalVacancies")
    var totalVacancies: Int? = null
    @SerializedName("availableVacancies")
    var availableVacancies: Int? = null
    @SerializedName("booking")
    var booking: ArrayList<Booking>? = null
    @SerializedName("total")
    var total: CartTotalPrice? = null
    @SerializedName("itemName")
    var itemName: String? = null
    @SerializedName("itemType")
    var itemType: Int? = null
    @SerializedName("maxPrice", alternate = ["MaxPrice"])
    var maxPrice: Double? = null
    @SerializedName("minPrice", alternate = ["MinPrice"])
    var minPrice: Double? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("endDate")
    var endDate: String? = null

    @SerializedName("time")
    var time: String? = null

    @SerializedName("ticketGroups", alternate = ["TicketGroups"])
    var ticketGroups: ArrayList<TicketGroup>? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        title = parcel.readString()
        location = parcel.readString()
        outlet = parcel.readString()
        outletId = parcel.readString()
        productType = parcel.readString()
        productCode = parcel.readString()
        itemId = parcel.readString()
        qty = parcel.readValue(Double::class.java.classLoader) as? Double
        isRegistrationOpen = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isSkillsFutureCourse = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        totalVacancies = parcel.readValue(Int::class.java.classLoader) as? Int
        availableVacancies = parcel.readValue(Int::class.java.classLoader) as? Int
        total = parcel.readParcelable(CartTotalPrice::class.java.classLoader)
        itemName = parcel.readString()
        itemType = parcel.readValue(Int::class.java.classLoader) as? Int
        maxPrice = parcel.readValue(Double::class.java.classLoader) as? Double
        minPrice = parcel.readValue(Double::class.java.classLoader) as? Double
        date = parcel.readString()
        endDate = parcel.readString()
        time = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(title)
        parcel.writeString(location)
        parcel.writeString(outlet)
        parcel.writeString(outletId)
        parcel.writeString(productType)
        parcel.writeString(productCode)
        parcel.writeString(itemId)
        parcel.writeValue(qty)
        parcel.writeValue(isRegistrationOpen)
        parcel.writeValue(isSkillsFutureCourse)
        parcel.writeValue(totalVacancies)
        parcel.writeValue(availableVacancies)
        parcel.writeParcelable(total, flags)
        parcel.writeString(itemName)
        parcel.writeValue(itemType)
        parcel.writeValue(maxPrice)
        parcel.writeValue(minPrice)
        parcel.writeString(date)
        parcel.writeString(endDate)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }


}