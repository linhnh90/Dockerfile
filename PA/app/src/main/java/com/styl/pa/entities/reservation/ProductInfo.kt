package com.styl.pa.entities.reservation

import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.pacesRequest.EventTicket


/**
 * Created by Ngatran on 09/26/2018.
 */
class ProductInfo {
    @SerializedName("productId")
    @Expose
    var productId: String? = null
    @SerializedName("productCode")
    @Expose
    private var productCode: String? = null
    @SerializedName("productTitle")
    @Expose
    private var productTitle: String? = null
    @SerializedName("gstRegNo")
    @Expose
    private var gSTRegNo: String? = null
    @SerializedName("venue")
    @Expose
    private var venue: String? = null
    @SerializedName("dateTime")
    @Expose
    private var dateTime: String? = null
    @SerializedName("beforeDiscountAmount")
    @Expose
    private var beforeDiscountAmount: Float? = null
    @SerializedName("discountAmount")
    @Expose
    private var discountAmount: Float? = null
    @SerializedName("paymentAmount")
    @Expose
    private var paymentAmount: Float? = null
    @SerializedName("adjustmentAmount")
    @Expose
    var adjustmentAmount: Float? = null

    @SerializedName("customerId", alternate = ["participantID"])
    @Expose
    private var customerId: String? = ""
    @SerializedName("customerName", alternate = ["participantName"])
    @Expose
    private var customerName: String? = ""
    @SerializedName("productDateTime")
    @Expose
    private var productDateTime: String? = null
    @SerializedName("outletName")
    @Expose
    private var outletName: String? = null
    @SerializedName("reservationID")
    @Expose
    private var reservationID: String? = null

    @SerializedName("promoDiscountAmount")
    @Expose
    var promoDiscountAmount: Float? = null

    var listSelectedTicket: ArrayList<EventTicket> = ArrayList()

    @SerializedName("productType", alternate = ["ProductType"])
    @Expose
    var productType: String? = null

    fun getCustomerId(): String? {
        return customerId
    }

    fun setCustomerId(customerId: String?) {
        this.customerId = customerId
    }

    fun getCustomerName(): String? {
        return customerName
    }

    fun setCustomerName(customerName: String?) {
        this.customerName = customerName
    }

    fun getProductDateTime(): String? {
        return productDateTime
    }

    fun setProductDateTime(productDateTime: String) {
        this.productDateTime = productDateTime
    }

    fun getOutletName(): String? {
        return outletName
    }

    fun setOutletName(outletName: String?) {
        this.outletName = outletName
    }

    fun setProductCode(productCode: String?) {
        this.productCode = productCode
    }

    fun getProductCode(): String? {
        return productCode
    }

    fun getProductTitle(): String? {
        return productTitle
    }

    fun getDecodedTitle(): Spanned {
        return HtmlCompat.fromHtml(productTitle ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun setProductTitle(productTitle: String?) {
        this.productTitle = productTitle
    }

    fun getGSTRegNo(): String? {
        return gSTRegNo
    }

    fun setGSTRegNo(gSTRegNo: String) {
        this.gSTRegNo = gSTRegNo
    }

    fun getVenue(): String? {
        return venue
    }

    fun setVenue(venue: String) {
        this.venue = venue
    }

    fun getDateTime(): String? {
        return dateTime
    }

    fun setDateTime(dateTime: String) {
        this.dateTime = dateTime
    }

    fun getBeforeDiscountAmount(): Float? {
        return beforeDiscountAmount
    }

    fun setBeforeDiscountAmount(beforeDiscountAmount: Float?) {
        this.beforeDiscountAmount = beforeDiscountAmount
    }

    fun getDiscountAmount(): Float? {
        return discountAmount
    }

    fun setDiscountAmount(discountAmount: Float?) {
        this.discountAmount = discountAmount
    }

    fun getPaymentAmount(): Float? {
        return paymentAmount
    }

    fun setPaymentAmount(paymentAmount: Float?) {
        this.paymentAmount = paymentAmount
    }
}