package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CartTotalPrice() : Parcelable {
    @SerializedName("memberPrice", alternate = ["MemberPrice"])
    var memberPrice: PriceInfo? = null
    @SerializedName("nonMemberPrice", alternate = ["NonMemberPrice"])
    var nonMemberPrice: PriceInfo? = null
    @SerializedName("totalPrice", alternate = ["TotalPrice"])
    var totalPrice: Float? = null

    constructor(parcel: Parcel) : this() {
        memberPrice = parcel.readParcelable(PriceInfo::class.java.classLoader)
        nonMemberPrice = parcel.readParcelable(PriceInfo::class.java.classLoader)
        totalPrice = parcel.readValue(Float::class.java.classLoader) as? Float
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(memberPrice, flags)
        parcel.writeParcelable(nonMemberPrice, flags)
        parcel.writeValue(totalPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartTotalPrice> {
        override fun createFromParcel(parcel: Parcel): CartTotalPrice {
            return CartTotalPrice(parcel)
        }

        override fun newArray(size: Int): Array<CartTotalPrice?> {
            return arrayOfNulls(size)
        }
    }
}