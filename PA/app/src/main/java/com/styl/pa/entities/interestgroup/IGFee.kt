package com.styl.pa.entities.interestgroup

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class IGFee(): Parcelable {
    @SerializedName("name")
    private var name: String? = null

    @SerializedName("feeName")
    private var feeName: String? = null

    @SerializedName("feeType")
    private var feeType: String? = null

    @SerializedName("feeAmount")
    private var feeAmount: String? = null

    @SerializedName("peakAmount")
    private var peakAmount: String? = null

    fun getName(): String? {
        return name
    }
    fun setName(name: String?){
        this.name = name
    }

    fun getFeeName(): String? {
        return feeName
    }
    fun setFeeName(feeName: String?){
        this.feeName = feeName
    }

    fun getFeeType(): String? {
        return feeType
    }
    fun setFeeType(feeType: String?){
        this.feeType = feeType
    }

    fun getFeeAmount(): String? {
        return feeAmount
    }
    fun setFeeAmount(feeAmount: String?){
        this.feeAmount = feeAmount
    }

    fun getPeakAmount(): String? {
        return peakAmount
    }
    fun setPeakAmount(peakAmount: String?){
        this.peakAmount = peakAmount
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        feeName = parcel.readString()
        feeType = parcel.readString()
        feeAmount = parcel.readString()
        peakAmount = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(feeName)
        parcel.writeString(feeType)
        parcel.writeString(feeAmount)
        parcel.writeString(peakAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IGFee> {
        override fun createFromParcel(parcel: Parcel): IGFee {
            return IGFee(parcel)
        }

        override fun newArray(size: Int): Array<IGFee?> {
            return arrayOfNulls(size)
        }
    }


}