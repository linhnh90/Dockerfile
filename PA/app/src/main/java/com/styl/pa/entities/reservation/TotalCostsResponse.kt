package com.styl.pa.entities.reservation

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 09/22/2018.
 */
class TotalCostsResponse() : Parcelable {
    @SerializedName("shoppingCartID")
    @Expose
    private var shoppingCartID: String? = null
    @SerializedName("paReferenceNumber")
    @Expose
    private var pAReferenceNumber: String? = null
    @SerializedName("totalBeforeDiscountAmount")
    @Expose
    private var totalBeforeDiscountAmount: Float? = null
    @SerializedName("totalDiscountAmount")
    @Expose
    private var totalDiscountAmount: Float? = null
    @SerializedName("totalAdjustmentAmount")
    @Expose
    private var totalAdjustmentAmount: Float? = null
    @SerializedName("totalPaymentAmount")
    @Expose
    private var totalPaymentAmount: Float? = null
    @SerializedName("totalGST")
    @Expose
    private var totalGST: Double? = null
    @SerializedName(value="customerId", alternate = ["bookedbyCustomerId"])
    @Expose
    private var customerId: String? = null
    @SerializedName(value="customerName", alternate = ["bookedbyCustomerName"])
    @Expose
    private var customerName: String? = null
    @SerializedName("products")
    @Expose
    private var products: List<ProductInfo>? = null

    @SerializedName("promoDiscountAmount")
    @Expose
    var promoDiscountAmount: Float? = null

    @SerializedName("promoCode")
    @Expose
    var promoCode: String? = null

    constructor(parcel: Parcel) : this() {
        shoppingCartID = parcel.readString()
        pAReferenceNumber = parcel.readString()
        totalBeforeDiscountAmount = parcel.readValue(Float::class.java.classLoader) as? Float
        totalDiscountAmount = parcel.readValue(Float::class.java.classLoader) as? Float
        totalAdjustmentAmount = parcel.readValue(Float::class.java.classLoader) as? Float
        totalPaymentAmount = parcel.readValue(Float::class.java.classLoader) as? Float
        totalGST = parcel.readValue(Double::class.java.classLoader) as? Double
        customerId = parcel.readString()
        customerName = parcel.readString()
    }

    fun getShoppingCartID(): String? {
        return shoppingCartID
    }

    fun setShoppingCartID(shoppingCartID: String) {
        this.shoppingCartID = shoppingCartID
    }

    fun getPAReferenceNumber(): String? {
        return pAReferenceNumber
    }

    fun setPAReferenceNumber(pAReferenceNumber: String) {
        this.pAReferenceNumber = pAReferenceNumber
    }

    fun getTotalBeforeDiscountAmount(): Float? {
        return totalBeforeDiscountAmount
    }

    fun setTotalBeforeDiscountAmount(totalBeforeDiscountAmount: Float?) {
        this.totalBeforeDiscountAmount = totalBeforeDiscountAmount
    }

    fun getTotalDiscountAmount(): Float? {
        return totalDiscountAmount
    }

    fun setTotalDiscountAmount(totalDiscountAmount: Float?) {
        this.totalDiscountAmount = totalDiscountAmount
    }

    fun getTotalAdjustmentAmount(): Float? {
        return totalAdjustmentAmount
    }

    fun setTotalAdjustmentAmount(totalAdjustmentAmount: Float?) {
        this.totalAdjustmentAmount = totalAdjustmentAmount
    }

    fun getTotalPaymentAmount(): Float? {
        return totalPaymentAmount
    }

    fun setTotalPaymentAmount(totalPaymentAmount: Float?) {
        this.totalPaymentAmount = totalPaymentAmount
    }

    fun getTotalGST(): Double? {
        return totalGST
    }

    fun setTotalGST(totalGST: Double?) {
        this.totalGST = totalGST
    }

    fun getProducts(): List<ProductInfo>? {
        return products
    }

    fun setProducts(products: List<ProductInfo>) {
        this.products = products
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(shoppingCartID)
        parcel.writeString(pAReferenceNumber)
        parcel.writeValue(totalBeforeDiscountAmount)
        parcel.writeValue(totalDiscountAmount)
        parcel.writeValue(totalAdjustmentAmount)
        parcel.writeValue(totalPaymentAmount)
        parcel.writeValue(totalGST)
        parcel.writeString(customerId)
        parcel.writeString(customerName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TotalCostsResponse> {
        override fun createFromParcel(parcel: Parcel): TotalCostsResponse {
            return TotalCostsResponse(parcel)
        }

        override fun newArray(size: Int): Array<TotalCostsResponse?> {
            return arrayOfNulls(size)
        }
    }
}