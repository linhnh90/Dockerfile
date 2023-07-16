package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PriceInfo() : Parcelable {
    @SerializedName("subTotal")
    var subTotal: String? = null
    @SerializedName("discountTotal")
    var discountTotal: String? = null
    @SerializedName("taxTotal")
    var taxTotal: String? = null
    @SerializedName("grandTotal")
    var grandTotal: String? = null
    @SerializedName("PromoDiscountAmount", alternate = ["promoDiscountAmount"])
    var promoDiscountAmount: String? = null

    constructor(parcel: Parcel) : this() {
        subTotal = parcel.readString()
        discountTotal = parcel.readString()
        taxTotal = parcel.readString()
        grandTotal = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(subTotal)
        parcel.writeString(discountTotal)
        parcel.writeString(taxTotal)
        parcel.writeString(grandTotal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PriceInfo> {
        override fun createFromParcel(parcel: Parcel): PriceInfo {
            return PriceInfo(parcel)
        }

        override fun newArray(size: Int): Array<PriceInfo?> {
            return arrayOfNulls(size)
        }
    }
}