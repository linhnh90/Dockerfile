package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CartErrorValidation() : Parcelable {
    @SerializedName("Action")
    var action: Any? = null
    @SerializedName("ErrorCode")
    var errorCode: String? = null
    @SerializedName("Message")
    var message: String? = null

    constructor(parcel: Parcel) : this() {
        errorCode = parcel.readString()
        message = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(errorCode)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartErrorValidation> {
        override fun createFromParcel(parcel: Parcel): CartErrorValidation {
            return CartErrorValidation(parcel)
        }

        override fun newArray(size: Int): Array<CartErrorValidation?> {
            return arrayOfNulls(size)
        }
    }
}