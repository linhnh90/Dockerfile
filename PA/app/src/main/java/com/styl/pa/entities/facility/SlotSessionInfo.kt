package com.styl.pa.entities.facility

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/29/2018.
 */
class SlotSessionInfo : Parcelable {
    @SerializedName("timeRangeId")
    private var timeRangeId: String? = null
    @SerializedName("timeRangeName")
    private var timeRangeName: String? = null
    @SerializedName("availabilityStatus")
    private var availabilityStatus: String? = null
    @SerializedName("isPeak")
    private var isPeak: Boolean = false
    @SerializedName("startTime")
    private var startTime: String? = null
    @SerializedName("endTime")
    private var endTime: String? = null
    @SerializedName("isAvailable")
    private var isAvailable: Boolean? = null


    private var isChoose: Int = AVAIL_TYPE

    var mTimeRangeId: String?
        get() {
            return timeRangeId
        }
        set(value) {
            this.timeRangeId = value
        }

    var mTimeRageName: String?
        get() {
            return timeRangeName
        }
        set(value) {
            this.timeRangeName = value
        }

    var mAvailabilityStatus: String?
        get() {
            return availabilityStatus
        }
        set(value) {
            this.availabilityStatus = value
        }

    var mIsPeak: Boolean
        get() {
            return isPeak
        }
        set(value) {
            this.isPeak = value
        }

    var mIsChoose: Int
        get() {
            return isChoose
        }
        set(value) {
            this.isChoose = value
        }

    var mStartTime: String?
        get() {
            return startTime
        }
        set(value) {
            this.startTime = value
        }

    var mEndTime: String?
        get() {
            return endTime
        }
        set(value) {
            this.endTime = value
        }

    var mIsAvailable: Boolean?
        get() {
            return isAvailable
        }
        set(value) {
            this.isAvailable = value
        }


    constructor()

    constructor(mTimeRangeId: String, mTimeRageName: String, mAvailabilityStatus: String, mIsChoose: Int) {
        this.timeRangeName = mTimeRageName
        this.timeRangeId = mTimeRangeId
        this.availabilityStatus = mAvailabilityStatus
        this.isChoose = mIsChoose
    }

    constructor(parcel: Parcel) : this() {
        timeRangeId = parcel.readString()
        timeRangeName = parcel.readString()
        availabilityStatus = parcel.readString()
        isPeak = parcel.readByte() != 0.toByte()
        startTime = parcel.readString()
        endTime = parcel.readString()
        isAvailable = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isChoose = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(timeRangeId)
        parcel.writeString(timeRangeName)
        parcel.writeString(availabilityStatus)
        parcel.writeByte(if (isPeak) 1 else 0)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeValue(isAvailable)
        parcel.writeInt(isChoose)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SlotSessionInfo> {
        override fun createFromParcel(parcel: Parcel): SlotSessionInfo {
            return SlotSessionInfo(parcel)
        }

        override fun newArray(size: Int): Array<SlotSessionInfo?> {
            return arrayOfNulls(size)
        }

        val BOOKING_TYPE = 1
        val PEAKING_TYPE = 2
        val AVAIL_TYPE = 0


        val AVAILABLE_TYPE = "Available"
        val BOOKED_TYPE = "Booked"
        val PEAK_TYPE = "Peak"
        val UN_AVAILABLE_TYPE = "Unavailable"
    }
}