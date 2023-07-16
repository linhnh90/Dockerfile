package com.styl.pa.entities.event

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 03/11/2019.
 */
class EventFee : Parcelable {
    @SerializedName("feeId")
    @Expose
    var feeId: String? = null
    @SerializedName("feeTypeId")
    @Expose
    var feeTypeId: Int? = null
    @SerializedName("feeTypeName")
    @Expose
    var feeTypeName: String? = null
    @SerializedName("feeDescription")
    @Expose
    var feeDescription: String? = null
    @SerializedName("feeAmount")
    @Expose
    var feeAmount: String? = null
    @SerializedName("feeName")
    @Expose
    var feeName: String? = null

    constructor()

    constructor(parcel: Parcel) : this() {
        feeId = parcel.readString()
        feeTypeId = parcel.readValue(Int::class.java.classLoader) as? Int
        feeTypeName = parcel.readString()
        feeDescription = parcel.readString()
        feeAmount = parcel.readString()
        feeName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(feeId)
        parcel.writeValue(feeTypeId)
        parcel.writeString(feeTypeName)
        parcel.writeString(feeDescription)
        parcel.writeString(feeAmount)
        parcel.writeString(feeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventFee> {
        override fun createFromParcel(parcel: Parcel): EventFee {
            return EventFee(parcel)
        }

        override fun newArray(size: Int): Array<EventFee?> {
            return arrayOfNulls(size)
        }
    }
}
