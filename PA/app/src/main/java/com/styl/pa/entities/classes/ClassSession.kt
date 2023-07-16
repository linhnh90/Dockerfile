package com.styl.pa.entities.classes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ClassSession() :Parcelable {
    @SerializedName("classSessionId", alternate = ["igSessionId"])
//    @Expose
    private var classSessionId: String? = null
    @SerializedName("classId", alternate = ["igId"])
//    @Expose
    private var classId: String? = null
    @SerializedName("startTime")
//    @Expose
    private var startTime: String? = null
    @SerializedName("endTime")
//    @Expose
    private var endTime: String? = null
    @SerializedName("internalVenueID")
//    @Expose
    private var internalVenueID: String? = null
    @SerializedName("internalVenue")
//    @Expose
    private var internalVenue: String? = null
    @SerializedName("externalVenue")
//    @Expose
    private var externalVenue: String? = null

    constructor(parcel: Parcel) : this() {
        classSessionId = parcel.readString()
        classId = parcel.readString()
        startTime = parcel.readString()
        endTime = parcel.readString()
        internalVenueID = parcel.readString()
        externalVenue = parcel.readString()
    }

    fun getClassSessionId(): String? {
        return classSessionId
    }

    fun setClassSessionId(classSessionId: String) {
        this.classSessionId = classSessionId
    }

    fun getClassId(): String? {
        return classId
    }

    fun setClassId(classId: String) {
        this.classId = classId
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(classSessionId)
        parcel.writeString(classId)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(internalVenueID)
        parcel.writeString(externalVenue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassSession> {
        override fun createFromParcel(parcel: Parcel): ClassSession {
            return ClassSession(parcel)
        }

        override fun newArray(size: Int): Array<ClassSession?> {
            return arrayOfNulls(size)
        }
    }
}