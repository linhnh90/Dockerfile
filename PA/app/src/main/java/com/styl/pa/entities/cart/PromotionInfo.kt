package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PromotionInfo() : Parcelable {
    @SerializedName("amount", alternate = ["Amount"])
    var amount: String? = null
    @SerializedName("promoCode", alternate = ["PromoCode"])
    var promoCode: String? = null
    @SerializedName("description", alternate = ["Description"])
    var description: String? = null

    constructor(parcel: Parcel) : this() {
        amount = parcel.readString()
        promoCode = parcel.readString()
        description = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(promoCode)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PromotionInfo> {
        override fun createFromParcel(parcel: Parcel): PromotionInfo {
            return PromotionInfo(parcel)
        }

        override fun newArray(size: Int): Array<PromotionInfo?> {
            return arrayOfNulls(size)
        }
    }
}