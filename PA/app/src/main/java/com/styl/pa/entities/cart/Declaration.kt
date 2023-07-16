package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Declaration() : Parcelable {
    @SerializedName("accepted")
    var accepted: Boolean? = null
    @SerializedName("description")
    var description: String? = null

    constructor(parcel: Parcel) : this() {
        accepted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        description = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accepted)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Declaration> {
        override fun createFromParcel(parcel: Parcel): Declaration {
            return Declaration(parcel)
        }

        override fun newArray(size: Int): Array<Declaration?> {
            return arrayOfNulls(size)
        }
    }
}