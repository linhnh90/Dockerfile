package com.styl.pa.entities.reservation

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class BookingDetail() : Parcelable {
    @SerializedName("receiptNo")
    var receiptNo: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("receiptId")
    var receiptId: String? = ""
        get() = field
        set(value) {
            field = value
        }
    @SerializedName("paymentMode")
    var paymentMode: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("bookingSource")
    var bookingSource: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("totalBeforeDiscount")
    var totalBeforeDiscount: Float? = 0F
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("totalDiscount")
    var totalDiscount: Float? = 0F
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("totalAdjustment")
    var totalAdjustment: Float? = 0F
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("totalGST")
    var totalGST: Float? = 0F
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("totalPayment")
    var totalPayment: Float? = 0F
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("receiptDateTime")
    var receiptDateTime: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName(value = "customerId", alternate = ["bookedbyCustomerId"])
    var customerId: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName(value = "customerName", alternate = ["bookedbyCustomerName"])
    var customerName: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("products")
    var products: ArrayList<ProductInfo>? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(parcel: Parcel) : this() {
        receiptNo = parcel.readString()
        receiptId = parcel.readString()
        paymentMode = parcel.readString()
        bookingSource = parcel.readString()
        totalBeforeDiscount = parcel.readFloat()
        totalDiscount = parcel.readFloat()
        totalAdjustment = parcel.readFloat()
        totalGST = parcel.readFloat()
        totalPayment = parcel.readFloat()
        receiptDateTime = parcel.readString()
        customerId = parcel.readString()
        customerName = parcel.readString()
        products = parcel.readParcelableArray(ProductInfo::class.java.classLoader) as ArrayList<ProductInfo>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(receiptNo)
        parcel.writeString(receiptId)
        parcel.writeString(paymentMode)
        parcel.writeString(bookingSource)
        parcel.writeFloat(totalBeforeDiscount ?: 0F)
        parcel.writeFloat(totalDiscount ?: 0F)
        parcel.writeFloat(totalAdjustment ?: 0F)
        parcel.writeFloat(totalGST ?: 0F)
        parcel.writeFloat(totalPayment ?: 0F)
        parcel.writeString(receiptDateTime)
        parcel.writeString(customerId)
        parcel.writeString(customerName)
        parcel.writeList(products as List<*>?)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingDetail> {
        override fun createFromParcel(parcel: Parcel): BookingDetail {
            return BookingDetail(parcel)
        }

        override fun newArray(size: Int): Array<BookingDetail?> {
            return arrayOfNulls(size)
        }
    }
}