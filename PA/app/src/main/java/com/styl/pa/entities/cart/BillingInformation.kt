package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class BillingInformation() : Parcelable {
    @SerializedName("contactNumber")
    var contactNumber: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("fullName")
    var fullName: String? = null

    constructor(parcel: Parcel) : this() {
        contactNumber = parcel.readString()
        email = parcel.readString()
        fullName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contactNumber)
        parcel.writeString(email)
        parcel.writeString(fullName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BillingInformation> {
        override fun createFromParcel(parcel: Parcel): BillingInformation {
            return BillingInformation(parcel)
        }

        override fun newArray(size: Int): Array<BillingInformation?> {
            return arrayOfNulls(size)
        }
    }
}