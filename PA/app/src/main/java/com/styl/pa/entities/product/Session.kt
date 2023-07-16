package com.styl.pa.entities.product

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName

/**
 * Created by NguyenHang on 12/8/2020.
 */

class Session(): Parcelable {
    @SerializedName("productSessionId")
    var productSessionId: String? = null

    @SerializedName("productId")
    var productId: String? = null

    @SerializedName("startTime")
    var startTime: String? = null

    @SerializedName("endTime")
    var endTime: String? = null

    @SerializedName("internalVenueID")
    var internalVenueID: String? = null

    @SerializedName("internalVenue")
    var internalVenue: String? = null

    @SerializedName("externalVenue")
    var externalVenue: String? = null

    constructor(parcel: Parcel) : this() {
        productSessionId = parcel.readString()
        productId = parcel.readString()
        startTime = parcel.readString()
        endTime = parcel.readString()
        internalVenueID = parcel.readString()
        internalVenue = parcel.readString()
        externalVenue = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productSessionId)
        parcel.writeString(productId)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(internalVenueID)
        parcel.writeString(internalVenue)
        parcel.writeString(externalVenue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Session> {
        override fun createFromParcel(parcel: Parcel): Session {
            return Session(parcel)
        }

        override fun newArray(size: Int): Array<Session?> {
            return arrayOfNulls(size)
        }
    }
}