package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CartResponse() : Parcelable {
    @SerializedName("data")
    var cartData: CartData? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("success")
    var success: Boolean? = null

    constructor(parcel: Parcel) : this() {
        message = parcel.readString()
        success = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeValue(success)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartResponse> {
        override fun createFromParcel(parcel: Parcel): CartResponse {
            return CartResponse(parcel)
        }

        override fun newArray(size: Int): Array<CartResponse?> {
            return arrayOfNulls(size)
        }
    }
}