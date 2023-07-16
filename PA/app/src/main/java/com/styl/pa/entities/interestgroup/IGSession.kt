package com.styl.pa.entities.interestgroup

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class IGSession(): Parcelable {
    @SerializedName("classSessionId")
    private var igSessionId: String? = null

    @SerializedName("startTime")
    private var startTime: String? = null

    @SerializedName("endTime")
    private var endTime: String? = null

    @SerializedName("internalVenueID")
    private var internalVenueID: String? = null

    @SerializedName("internalVenue")
    private var internalVenue: String? = null

    @SerializedName("externalVenue")
    private var externalVenue: String? = null

    fun getIgSessionId(): String? {
        return igSessionId
    }

    fun setIgSessionId(igSessionId: String) {
        this.igSessionId = igSessionId
    }

    fun getStartTime(): String? {
        return startTime
    }

    fun setStartTime(startTime: String) {
        this.startTime = startTime
    }

    fun getEndTime(): String? {
        return endTime
    }

    fun setEndTime(endTime: String) {
        this.endTime = endTime
    }

    fun getInternalVenueID(): String? {
        return internalVenueID
    }

    fun setInternalVenueID(internalVenueID: String) {
        this.internalVenueID = internalVenueID
    }

    fun getInternalVenue(): String? {
        return internalVenue
    }

    fun setInternalVenue(internalVenue: String) {
        this.internalVenue = internalVenue
    }

    fun getExternalVenue(): String? {
        return externalVenue
    }

    fun setExternalVenue(externalVenue: String) {
        this.externalVenue = externalVenue
    }

    constructor(parcel: Parcel) : this() {
        igSessionId = parcel.readString()
        startTime = parcel.readString()
        endTime = parcel.readString()
        internalVenueID = parcel.readString()
        internalVenue = parcel.readString()
        externalVenue = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(igSessionId)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(internalVenueID)
        parcel.writeString(internalVenue)
        parcel.writeString(externalVenue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IGSession> {
        override fun createFromParcel(parcel: Parcel): IGSession {
            return IGSession(parcel)
        }

        override fun newArray(size: Int): Array<IGSession?> {
            return arrayOfNulls(size)
        }
    }
}