package com.styl.pa.entities.facility

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/25/2018.
 */
class FacilitySessionInfo : Parcelable {
    @SerializedName("resourceId")
    private var resourceId: String? = null
    @SerializedName("resourceName")
    private var resourceName: String? = null
    @SerializedName("slotList")
    private var slotList: ArrayList<SlotSessionInfo>? = ArrayList()

    var mResourceId: String?
        get() {
            return resourceId
        }
        set(value) {
            this.resourceId = value
        }

    var mResourceName: String?
        get() {
            return resourceName
        }
        set(value) {
            this.resourceName = value
        }

    var mSlotList: ArrayList<SlotSessionInfo>?
        get() {
            return slotList
        }
        set(value) {
            this.slotList = value
        }

    constructor() {}

    constructor(resourceName: String, resourceId: String, slotList: ArrayList<SlotSessionInfo>) {
        this.resourceName = resourceName
        this.resourceId = resourceId
        this.slotList = slotList
    }

    constructor(parcel: Parcel) : this() {
        resourceId = parcel.readString()
        resourceName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(resourceId)
        parcel.writeString(resourceName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FacilitySessionInfo> {
        override fun createFromParcel(parcel: Parcel): FacilitySessionInfo {
            return FacilitySessionInfo(parcel)
        }

        override fun newArray(size: Int): Array<FacilitySessionInfo?> {
            return arrayOfNulls(size)
        }
    }
}