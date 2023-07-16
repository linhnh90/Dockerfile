package com.styl.pa.entities.promocode

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PromoCode() : Parcelable {

    @SerializedName("code")
    var code: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("startDate")
    var startDate: String? = null
    @SerializedName("endDate")
    var endDate: String? = null
    @SerializedName("termsAndConditions1")
    var termsAndConditions1: String? = null
    @SerializedName("termsAndConditions2")
    var termsAndConditions2: String? = null
    @SerializedName("termsAndConditionsLink")
    var termsAndConditionsLink: String? = null
    @SerializedName("type")
    var type: String? = null

    constructor(parcel: Parcel) : this() {
        code = parcel.readString()
        description = parcel.readString()
        startDate = parcel.readString()
        endDate = parcel.readString()
        termsAndConditions1 = parcel.readString()
        termsAndConditions2 = parcel.readString()
        termsAndConditionsLink = parcel.readString()
        type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(description)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(termsAndConditions1)
        parcel.writeString(termsAndConditions2)
        parcel.writeString(termsAndConditionsLink)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PromoCode> {
        override fun createFromParcel(parcel: Parcel): PromoCode {
            return PromoCode(parcel)
        }

        override fun newArray(size: Int): Array<PromoCode?> {
            return arrayOfNulls(size)
        }
    }
}