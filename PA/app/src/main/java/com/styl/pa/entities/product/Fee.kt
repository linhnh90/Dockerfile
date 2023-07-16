package com.styl.pa.entities.product

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by NguyenHang on 12/8/2020.
 */
class Fee(): Parcelable {
    @SerializedName("feeId")
    @Expose
    var feeId: String? = null
    @SerializedName("feeTypeId")
    @Expose
    var feeTypeId: Int? = null
    @SerializedName("feeTypeName")
    @Expose
    var feeTypeName: String? = null
    @SerializedName("feeDescription")
    @Expose
    var feeDescription: String? = null
    @SerializedName("feeAmount")
    @Expose
    var feeAmount: String? = null
    @SerializedName("feeName")
    @Expose
    var feeName: String? = null

    @SerializedName("productId")
    private var productId: String? = null

    constructor(parcel: Parcel) : this() {
        feeId = parcel.readString()
        productId = parcel.readString()
        feeTypeId = parcel.readValue(Int::class.java.classLoader) as? Int
        feeTypeName = parcel.readString()
        feeDescription = parcel.readString()
        feeAmount = parcel.readString()
        feeName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(feeId)
        parcel.writeString(productId)
        parcel.writeValue(feeTypeId)
        parcel.writeString(feeTypeName)
        parcel.writeString(feeDescription)
        parcel.writeString(feeAmount)
        parcel.writeString(feeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Fee> {
        const val FEE_NAME_MEMBER = "Member Fee"
        const val FEE_PASSION_MEMBER = "(PASSION MEMBER)"
        const val FEE_NAME_NON_MEMBER = "Non-Member Fee"

        const val FEE_NAME_MEMBER_PASSION = "Passion Member Fee"
        const val FEE_NAME_NON_MEMBER_PASSION = "Non-Passion Member Fee"

        const val FEE_NAME_PASSION_CARD_1 = "Rental Fee for PAssion Card Members"
        const val FEE_NAME_PASSION_CARD_2 = "Rental Fee to PAssion Card Members"
        const val FEE_PUBLIC = "Rental Fee for Public"
        const val FEE_NAME_MATERIAL = "Material"

        override fun createFromParcel(parcel: Parcel): Fee {
            return Fee(parcel)
        }

        override fun newArray(size: Int): Array<Fee?> {
            return arrayOfNulls(size)
        }
    }


}