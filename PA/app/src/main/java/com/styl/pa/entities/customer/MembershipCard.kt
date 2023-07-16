package com.styl.pa.entities.customer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 09/21/2018.
 */
class MembershipCard() : Parcelable{
    @SerializedName("canNumber")
    var canNumber: String? = null
    @SerializedName("cardStatus")
    var cardStatus: String? = null
    @SerializedName("cardType")
    var cardType: String? = null
    @SerializedName("expiryDate")
    var expiryDate: String? = null
    @SerializedName("membershipCardNo")
    var membershipCardNo: String? = null
    @SerializedName("cardCode")
    var cardCode: String? = null

    @SerializedName("TFTPoints")
    private var TFTPoints: String? = null

    constructor(parcel: Parcel) : this() {
        canNumber = parcel.readString()
        cardStatus = parcel.readString()
        cardType = parcel.readString()
        expiryDate = parcel.readString()
        membershipCardNo = parcel.readString()
        TFTPoints = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(canNumber)
        parcel.writeString(cardStatus)
        parcel.writeString(cardType)
        parcel.writeString(expiryDate)
        parcel.writeString(membershipCardNo)
        parcel.writeString(TFTPoints)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MembershipCard> {
        override fun createFromParcel(parcel: Parcel): MembershipCard {
            return MembershipCard(parcel)
        }

        override fun newArray(size: Int): Array<MembershipCard?> {
            return arrayOfNulls(size)
        }
    }
}