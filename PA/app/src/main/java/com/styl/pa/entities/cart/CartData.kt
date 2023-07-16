package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CartData() : Parcelable {
    @SerializedName("cart", alternate = ["Cart"])
    var cart: CartInfo? = null
    @SerializedName("CartId", alternate = ["cartId"])
    var cartId: String? = null
    @SerializedName("ErrorValidation")
    var errorValidation: CartErrorValidation? = null

    constructor(parcel: Parcel) : this() {
        cart = parcel.readParcelable(CartInfo::class.java.classLoader)
        cartId = parcel.readString()
        errorValidation = parcel.readParcelable(CartErrorValidation::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(cart, flags)
        parcel.writeString(cartId)
        parcel.writeParcelable(errorValidation, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartData> {
        override fun createFromParcel(parcel: Parcel): CartData {
            return CartData(parcel)
        }

        override fun newArray(size: Int): Array<CartData?> {
            return arrayOfNulls(size)
        }
    }

}