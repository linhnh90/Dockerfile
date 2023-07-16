package com.styl.pa.entities.payment

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PaymentOption() : Parcelable {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("key")
    var key: String? = null
    @SerializedName("loadUrl")
    var loadUrl: String? = null
    @SerializedName("iconImage")
    var iconImage: String? = null
    @SerializedName("iconImageType")
    var iconImageType: String? = null
    @SerializedName("iconImageBase64")
    var iconImageBase64: String? = null
    @SerializedName("created")
    var created: String? = null
    @SerializedName("name")
    var selected: Boolean? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        key = parcel.readString()
        loadUrl = parcel.readString()
        iconImage = parcel.readString()
        iconImageType = parcel.readString()
        iconImageBase64 = parcel.readString()
        created = parcel.readString()
        selected = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(key)
        parcel.writeString(loadUrl)
        parcel.writeString(iconImage)
        parcel.writeString(iconImageType)
        parcel.writeString(iconImageBase64)
        parcel.writeString(created)
        parcel.writeValue(selected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentOption> {
        override fun createFromParcel(parcel: Parcel): PaymentOption {
            return PaymentOption(parcel)
        }

        override fun newArray(size: Int): Array<PaymentOption?> {
            return arrayOfNulls(size)
        }
    }
}