package com.styl.pa.entities.payment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 10/9/2018
 */
@Entity(tableName = "TransactionLog")
class PaymentRequest(
        @ColumnInfo(name = "kioskId")
        @SerializedName("KioskId")
        var kioskId: Int?,
        @ColumnInfo(name = "createdAt")
        @SerializedName("CreatedAt")
        var createdAt: Long?,
        @ColumnInfo(name = "customerId")
        @SerializedName("CustomerId")
        var customerId: String?,
        @ColumnInfo(name = "customerName")
        @SerializedName("CustomerName")
        var customerName: String?
) {

    companion object {
        const val STATUS_UNSUCCESSFUL = -1
        const val STATUS_CREATED = 0
        const val STATUS_PAID = 1
        const val STATUS_GENERATED = 2
        const val STATUS_DONE = 3
        const val STATUS_UPDATED = 4

        fun getPaymentStatusString(status: Int): String {
            return when (status) {
                STATUS_DONE -> "Done"
                STATUS_CREATED -> "Created"
                STATUS_PAID -> "Paid"
                STATUS_GENERATED -> "Generated"
                STATUS_UPDATED -> "Updated"
                STATUS_UNSUCCESSFUL -> "Unsuccessful"
                else -> ""
            }
        }
    }

    @ColumnInfo(name = "rrn")
    @SerializedName("RRN")
    var rrn: String? = null

    @ColumnInfo(name = "tid")
    @SerializedName("TID")
    var tid: String? = ""

    @ColumnInfo(name = "mid")
    @SerializedName("MID")
    var mid: String? = ""

    @ColumnInfo(name = "approvalCode")
    @SerializedName("ApprovalCode")
    var approvalCode: String? = null

    @ColumnInfo(name = "errorCode")
    @SerializedName("ErrorCode")
    var errorCode: String? = null

    @ColumnInfo(name = "issuerName")
    @SerializedName("IssuerName")
    var issuerName: String? = null

    @ColumnInfo(name = "beforeDiscountAmount")
    @SerializedName("BeforeDiscountAmount")
    var beforeDiscountAmount: Int? = 0

    @ColumnInfo(name = "discountAmount")
    @SerializedName("DiscountAmount")
    var discountAmount: Int? = 0

    @ColumnInfo(name = "adjustmentAmount")
    @SerializedName("AdjustmentAmount")
    var adjustmentAmount: Int? = 0

    @ColumnInfo(name = "paymentAmount")
    @SerializedName("PaymentAmount")
    var paymentAmount: Int? = 0

    @ColumnInfo(name = "gst")
    @SerializedName("GST")
    var gst: Double? = 0.0

    @ColumnInfo(name = "paymentTypeCode")
    @SerializedName("PaymentTypeCode")
    var paymentTypeCode: String? = null

    @ColumnInfo(name = "paymentDate")
    @SerializedName("PaymentDate")
    var paymentDate: String? = null

    @ColumnInfo(name = "pan")
    @SerializedName("PAN")
    var pan: String? = null

    @ColumnInfo(name = "invoiceNo")
    @SerializedName("InvoiceNo")
    var invoiceNo: String? = null

    @ColumnInfo(name = "batch")
    @SerializedName("Batch")
    var batch: String? = null

    @ColumnInfo(name = "stan")
    @SerializedName("STAN")
    var stan: String? = null

    @ColumnInfo(name = "aid")
    @SerializedName("AID")
    var aid: String? = null

    @ColumnInfo(name = "cardLabel")
    @SerializedName("CardLabel")
    var cardLabel: String? = null

    @ColumnInfo(name = "signatureRequired")
    @SerializedName("SignatureRequired")
    var signatureRequired: Boolean? = null

    @ColumnInfo(name = "signatureImage")
    @SerializedName("SignatureImage")
    var signatureImage: String? = null

    @ColumnInfo(name = "paReceiptId")
    @SerializedName("PAReceiptId")
    var paReceiptId: String? = ""

    @ColumnInfo(name = "paymentStatus")
    @SerializedName("PaymentStatus")
    var paymentStatus: Int? = null

    @ColumnInfo(name = "paReceiptStatus")
    @SerializedName("PAReceiptStatus")
    var paReceiptStatus: Int? = null

    @ColumnInfo(name = "txnNo")
    @PrimaryKey
    @SerializedName("TxnNo")
    var txnNo: String = ""

    @ColumnInfo(name = "rawData")
    @SerializedName("RawData")
    var rawData: String? = null

    @Ignore
    @SerializedName("Items")
    var items: List<PaymentItem>? = null

    @ColumnInfo(name = "pdfFile")
    @SerializedName("pdfFile")
    var pdfFile: String? = null

    @ColumnInfo(name = "completedAt")
    @SerializedName("completedAt")
    var completedAt: Long? = null

    @ColumnInfo(name = "referenceId")
    @SerializedName("referenceId")
    var referenceId: String? = null

    @ColumnInfo(name = "email")
    @SerializedName("email")
    var email: String? = null

    @ColumnInfo(name = "cartId")
    @SerializedName("cartId")
    var cartId: String? = null

    @ColumnInfo(name = "paymentCode")
    @SerializedName("paymentCode")
    var paymentCode: Int? = null

    @ColumnInfo(name = "outletId")
    @SerializedName("outletId")
    var outletId: String? = null

    /**
     * 0: S300 terminal
     * 1: UPT1000F terminal
     */
    @Ignore
    @SerializedName("ErrorType", alternate = ["errorType"])
    var errorType: Int = 1

    @Ignore
    var isSelected = false

    @Ignore
    constructor(
            kioskId: Int?,
            rrn: String?,
            tid: String?,
            mid: String?,
            approvalCode: String?,
            errorCode: String?,
            issuerName: String?,
            beforeDiscountAmount: Int?,
            discountAmount: Int?,
            adjustmentAmount: Int?,
            paymentAmount: Int?,
            gst: Double?,
            paymentTypeCode: String?,
            paymentDate: String?,
            pan: String?,
            invoiceNo: String?,
            batch: String?,
            stan: String?,
            aid: String?,
            cardLabel: String?,
            signatureRequired: Boolean?,
            signatureImage: String?,
            paReceiptId: String?,
            paymentStatus: Int?,
            paReceiptStatus: Int?,
            txnNo: String,
            rawData: String?,
            items: List<PaymentItem>?,
            referenceId: String?,
            email: String?,
            cartId: String?,
            paymentCode: Int?,
            outletId: String?
    ) : this(kioskId, null, null, null) {
        this.rrn = rrn
        this.tid = tid
        this.mid = mid
        this.approvalCode = approvalCode
        this.errorCode = errorCode
        this.issuerName = issuerName
        this.beforeDiscountAmount = beforeDiscountAmount
        this.discountAmount = discountAmount
        this.adjustmentAmount = adjustmentAmount
        this.paymentAmount = paymentAmount
        this.gst = gst
        this.paymentTypeCode = paymentTypeCode
        this.paymentDate = paymentDate
        this.pan = pan
        this.invoiceNo = invoiceNo
        this.batch = batch
        this.stan = stan
        this.aid = aid
        this.cardLabel = cardLabel
        this.signatureRequired = signatureRequired
        this.signatureImage = signatureImage
        this.paReceiptId = paReceiptId
        this.paymentStatus = paymentStatus
        this.paReceiptStatus = paReceiptStatus
        this.txnNo = txnNo
        this.rawData = rawData
        this.items = items
        this.referenceId = referenceId
        this.cartId = cartId
        this.email = email
        this.paymentCode = paymentCode
        this.outletId = outletId
    }
}