package com.styl.pa.entities.cart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CartInfo() : Parcelable {
    @SerializedName("items")
    var items: MutableList<Item>? = null
    @SerializedName("promotions")
    var promotions: Any? = null
    @SerializedName("total")
    var total: PriceInfo? = null
    @SerializedName("billingInformation")
    var billingInformation: BillingInformation? = null
    @SerializedName("expiry")
    var expiry: String? = null
    @SerializedName("isValidCart", alternate = ["IsValidCart"])
    var isValidCart: Boolean? = null
    @SerializedName("declaration")
    var declaration: Declaration? = null
    @SerializedName("promotion")
    var promotion: PromotionInfo? = null

    @SerializedName("isQuickBook", alternate = ["IsQuickBook"])
    var isQuickBook: Boolean? = null

    @SerializedName("isOtherUser", alternate = ["IsOtherUser"])
    var isOtherUser: Boolean? = null

    constructor(parcel: Parcel) : this() {
        total = parcel.readParcelable(PriceInfo::class.java.classLoader)
        billingInformation = parcel.readParcelable(BillingInformation::class.java.classLoader)
        expiry = parcel.readString()
        isValidCart = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        declaration = parcel.readParcelable(Declaration::class.java.classLoader)
        isQuickBook = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isOtherUser = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(total, flags)
        parcel.writeParcelable(billingInformation, flags)
        parcel.writeString(expiry)
        parcel.writeValue(isValidCart)
        parcel.writeParcelable(declaration, flags)
        parcel.writeValue(isQuickBook)
        parcel.writeValue(isOtherUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartInfo> {
        override fun createFromParcel(parcel: Parcel): CartInfo {
            return CartInfo(parcel)
        }

        override fun newArray(size: Int): Array<CartInfo?> {
            return arrayOfNulls(size)
        }
    }


}