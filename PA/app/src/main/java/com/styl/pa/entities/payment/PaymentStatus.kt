package com.styl.pa.entities.payment

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PaymentStatus() : Parcelable {

    @SerializedName("requestId")
    var requestId: String? = null
    @SerializedName("transactionReferencce")
    var transactionReference: String? = null
    @SerializedName("billingReference")
    var billingReference: String? = null
    @SerializedName("transactionStatus")
    var transactionStatus: String? = null
    @SerializedName("paymentCode")
    var paymentCode: String? = null

    constructor(parcel: Parcel) : this() {
        requestId = parcel.readString()
        transactionReference = parcel.readString()
        billingReference = parcel.readString()
        transactionStatus = parcel.readString()
        paymentCode = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(requestId)
        parcel.writeString(transactionReference)
        parcel.writeString(billingReference)
        parcel.writeString(transactionStatus)
        parcel.writeString(paymentCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        val CREATOR = object : Parcelable.Creator<PaymentStatus> {
            override fun createFromParcel(parcel: Parcel): PaymentStatus {
                return PaymentStatus(parcel)
            }

            override fun newArray(size: Int): Array<PaymentStatus?> {
                return arrayOfNulls(size)
            }
        }

        enum class Status(val value: String) {
            CREATED("CREATED"),
            INPROGRESS("INPROGRESS"),
            PENDING("PENDING"),
            FAILED("FAILED"),
            EXPIRED("EXPIRED"),
            COMPLETED("COMPLETED"),
            CANCELLED("CANCELLED")
        }
    }

}