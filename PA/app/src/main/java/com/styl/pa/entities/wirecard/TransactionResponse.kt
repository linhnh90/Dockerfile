package com.styl.pa.entities.wirecard

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.styl.castle_terminal_upt1000_api.define.FieldData
import com.styl.castle_terminal_upt1000_api.message.payment.TxnDataEntity
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_AID
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_APPROVAL_CODE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_BATCH_NO
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_CARD_BALANCE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_CARD_LABEL
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_CARD_NUMBER
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_CARD_TYPE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_CEPAS_CURRENT_BALANCE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_CEPAS_PREVIOUS_BALANCE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_DATETIME
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_DISCOUNT_AMOUNT
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_ENTRY_MODE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_ERROR
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_EXPIRY_DATE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_INVOICE_NO
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_ISSUER_NAME
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_MERCHANT_ID
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_MID
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_NETS_ACCOUNT_TYPE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_NETS_DEBIT_BANK
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_PAN_NUMBER
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_PAYMENT_TYPE
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_REFERENCE_NUMBER
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_RRN
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_SIGNATURE_REQUIRED
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_STAN_NO
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TERMINAL_ID
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TID
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TOTAL_AMOUNT
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TSI
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TVR
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TXN_AMOUNT
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TXN_CERT_TC
import com.styl.pa.entities.wirecard.WireCardField.Companion.FIELD_TXN_RECEIPT
import com.styl.pa.modules.peripheralsManager.GeneralUtils
import com.styl.pa.modules.peripheralsManager.terminalManager.TerminalErrorCode

@SuppressLint("ParcelCreator")
/**
 * Created by trangpham on 9/27/2018
 */
class TransactionResponse() : Parcelable {

    var approvalCode: String? = null
    var cardNumber: String? = null
    var expiryDate: String? = null
    var issuerName: String? = null
    var merchantId: String? = null
    var terminalId: String? = null
    var referenceNumber: String? = null
    var transactionAmount: String? = null
    var discountAmount: String? = null
    var cardBalanceAmount: String? = null
    var errorCode: String? = null
    var paymentType: String? = null
    var dateTime: String? = null
    var cardType: String? = null
    var entryMode: String? = null
    var panNumber: String? = null
    var mid: String? = ""
    var tid: String? = ""
    var invoiceNo: String? = null
    var rrn: String? = null
    var batchNo: String? = null
    var stanNo: String? = null
    var totalAmount: String? = null
    var aid: String? = null
    var cardLabel: String? = null
    var txnCertificate: String? = null
    var tvr: String? = null
    var tsi: String? = null
    var accountType: String? = null
    var debitBank: String? = null
    var previousBalance: String? = null
    var currentBalance: String? = null
    var signatureRequired: Boolean? = null
    var txnReceipt: String? = null

    fun setErrorCode(msgStatus: Int) {
        if (msgStatus == 0) {
            errorCode = ""
        } else {
            val hexString = GeneralUtils.convertIntToHexString(msgStatus, 4)
            errorCode = "0x${hexString}"
        }
    }

    fun setPaymentType(type: Int?) {
        if (type != null) {
            val hexString = GeneralUtils.convertIntToHexString(type, 2)
            paymentType = hexString
        }
    }

    constructor(parcel: Parcel) : this() {
        approvalCode = parcel.readString()
        cardNumber = parcel.readString()
        expiryDate = parcel.readString()
        issuerName = parcel.readString()
        merchantId = parcel.readString()
        terminalId = parcel.readString()
        referenceNumber = parcel.readString()
        transactionAmount = parcel.readString()
        discountAmount = parcel.readString()
        cardBalanceAmount = parcel.readString()
        errorCode = parcel.readString()
        paymentType = parcel.readString()
        dateTime = parcel.readString()
        cardType = parcel.readString()
        entryMode = parcel.readString()
        panNumber = parcel.readString()
        mid = parcel.readString()
        tid = parcel.readString()
        invoiceNo = parcel.readString()
        rrn = parcel.readString()
        batchNo = parcel.readString()
        stanNo = parcel.readString()
        totalAmount = parcel.readString()
        aid = parcel.readString()
        cardLabel = parcel.readString()
        txnCertificate = parcel.readString()
        tvr = parcel.readString()
        tsi = parcel.readString()
        accountType = parcel.readString()
        debitBank = parcel.readString()
        previousBalance = parcel.readString()
        currentBalance = parcel.readString()
        signatureRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        txnReceipt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(approvalCode)
        parcel.writeString(cardNumber)
        parcel.writeString(expiryDate)
        parcel.writeString(issuerName)
        parcel.writeString(merchantId)
        parcel.writeString(terminalId)
        parcel.writeString(referenceNumber)
        parcel.writeString(transactionAmount)
        parcel.writeString(discountAmount)
        parcel.writeString(cardBalanceAmount)
        parcel.writeString(errorCode)
        parcel.writeString(paymentType)
        parcel.writeString(dateTime)
        parcel.writeString(cardType)
        parcel.writeString(entryMode)
        parcel.writeString(panNumber)
        parcel.writeString(mid)
        parcel.writeString(tid)
        parcel.writeString(invoiceNo)
        parcel.writeString(rrn)
        parcel.writeString(batchNo)
        parcel.writeString(stanNo)
        parcel.writeString(totalAmount)
        parcel.writeString(aid)
        parcel.writeString(cardLabel)
        parcel.writeString(txnCertificate)
        parcel.writeString(tvr)
        parcel.writeString(tsi)
        parcel.writeString(accountType)
        parcel.writeString(debitBank)
        parcel.writeString(previousBalance)
        parcel.writeString(currentBalance)
        parcel.writeValue(signatureRequired)
        parcel.writeString(txnReceipt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        const val CEPAS_PAYMENT_TYPE = "01"
        const val CREDIT_PAYMENT_TYPE = "02"
        const val NETS_PAYMENT_TYPE = "03"

        val CREATOR = object : Parcelable.Creator<TransactionResponse> {
            override fun createFromParcel(parcel: Parcel): TransactionResponse {
                return TransactionResponse(parcel)
            }

            override fun newArray(size: Int): Array<TransactionResponse?> {
                return arrayOfNulls(size)
            }
        }

        fun from(fields: List<WireCardField>): TransactionResponse {
            val txnResponse = TransactionResponse()
            for (field in fields) {
                when (field.fieldType) {
                    FIELD_APPROVAL_CODE -> txnResponse.approvalCode = field.data
                    FIELD_CARD_NUMBER -> txnResponse.cardNumber = field.data
                    FIELD_EXPIRY_DATE -> txnResponse.expiryDate = field.data
                    FIELD_ISSUER_NAME -> txnResponse.issuerName = field.data
                    FIELD_MERCHANT_ID -> txnResponse.merchantId = field.data
                    FIELD_TERMINAL_ID -> txnResponse.terminalId = field.data
                    FIELD_REFERENCE_NUMBER -> txnResponse.referenceNumber = field.data
                    FIELD_TXN_AMOUNT -> txnResponse.transactionAmount = field.data
                    FIELD_DISCOUNT_AMOUNT -> txnResponse.discountAmount = field.data
                    FIELD_CARD_BALANCE -> txnResponse.cardBalanceAmount = field.data
                    FIELD_ERROR -> txnResponse.errorCode = field.data
                    FIELD_PAYMENT_TYPE -> txnResponse.paymentType = field.data
                    FIELD_DATETIME -> txnResponse.dateTime = field.data
                    FIELD_CARD_TYPE -> txnResponse.cardType = field.data
                    FIELD_ENTRY_MODE -> txnResponse.entryMode = field.data
                    FIELD_PAN_NUMBER -> txnResponse.panNumber = field.data
                    FIELD_MID -> txnResponse.mid = field.data
                    FIELD_TID -> txnResponse.tid = field.data
                    FIELD_INVOICE_NO -> txnResponse.invoiceNo = field.data
                    FIELD_RRN -> txnResponse.rrn = field.data
                    FIELD_BATCH_NO -> txnResponse.batchNo = field.data
                    FIELD_STAN_NO -> txnResponse.stanNo = field.data
                    FIELD_TOTAL_AMOUNT -> txnResponse.totalAmount = field.data
                    FIELD_AID -> txnResponse.aid = field.data
                    FIELD_CARD_LABEL -> txnResponse.cardLabel = field.data
                    FIELD_TXN_CERT_TC -> txnResponse.txnCertificate = field.data
                    FIELD_TVR -> txnResponse.tvr = field.data
                    FIELD_TSI -> txnResponse.tsi = field.data
                    FIELD_NETS_ACCOUNT_TYPE -> txnResponse.accountType = field.data
                    FIELD_NETS_DEBIT_BANK -> txnResponse.debitBank = field.data
                    FIELD_CEPAS_PREVIOUS_BALANCE -> txnResponse.previousBalance = field.data
                    FIELD_CEPAS_CURRENT_BALANCE -> txnResponse.currentBalance = field.data
                    FIELD_SIGNATURE_REQUIRED -> txnResponse.signatureRequired =
                            "01".equals(field.data)
                    FIELD_TXN_RECEIPT -> txnResponse.txnReceipt = field.data
                }
            }
            return txnResponse
        }

        fun from(txnDataEntity: TxnDataEntity?): TransactionResponse {
            val txnResponse = TransactionResponse()
            txnResponse.approvalCode = txnDataEntity?.txnApproveCode
            txnResponse.merchantId = txnDataEntity?.txnMID
            txnResponse.terminalId = txnDataEntity?.txnTID
            txnResponse.mid = txnDataEntity?.txnMID
            txnResponse.tid = txnDataEntity?.txnTID
            txnResponse.referenceNumber = txnDataEntity?.txnMerRefNum
            txnResponse.transactionAmount = txnDataEntity?.txnAmount?.toString()
            txnResponse.setErrorCode(txnDataEntity?.status
                    ?: TerminalErrorCode.TERMINAL_NOT_RESPONSE)
            txnResponse.setPaymentType(txnDataEntity?.txnType?.toInt())
            txnResponse.dateTime = txnDataEntity?.txnDate + " " + txnDataEntity?.txnTime
            txnResponse.cardType = txnDataEntity?.cardSchemeName?.toString()
            txnResponse.invoiceNo = txnDataEntity?.invoiceNumber
            txnResponse.stanNo = txnDataEntity?.txnStan
//            txnResponse.totalAmount =
            txnResponse.cardLabel = txnDataEntity?.cardSchemeName
            txnResponse.txnReceipt = txnDataEntity?.txnReceipt

            when (txnDataEntity?.txnType) {
                FieldData.TxnType.PAYMENT_BY_SCHEME_CREDIT -> {
                    txnResponse.cardNumber = txnDataEntity.cardNumMasked
                    txnResponse.expiryDate = txnDataEntity.cardExpiry

                    // call FieldData.CardEntryMode.getCardEntryMode(mode) to get string
                    txnResponse.entryMode = txnDataEntity.txnCardEntryMode.toString()
                    txnResponse.panNumber = txnDataEntity.cardNumMasked
                    txnResponse.batchNo = txnDataEntity.txnBatch
                    txnResponse.aid = txnDataEntity.cardAID
                    txnResponse.txnCertificate = txnDataEntity.txnTC

                }

                FieldData.TxnType.PAYMENT_BY_EZL,
                FieldData.TxnType.PAYMENT_BY_NETS_NFP -> {
                    txnResponse.cardNumber = txnDataEntity.cardCAN
                    txnResponse.expiryDate = txnDataEntity.cardExpiry
                    txnResponse.cardBalanceAmount = txnDataEntity.cardBalance.toString()
                    txnResponse.panNumber = txnDataEntity.cardCAN
                    txnResponse.batchNo = txnDataEntity.txnBatch
                }

                FieldData.TxnType.PAYMENT_BY_NETS_EFT -> {
                    txnResponse.issuerName = txnDataEntity.txnCardName
                    txnResponse.cardBalanceAmount = txnDataEntity.cardBalance.toString()

                    // call FieldData.CardEntryMode.getCardEntryMode(mode) to get string
                    txnResponse.entryMode = txnDataEntity.txnCardEntryMode.toString()
                }
            }
            if (!txnDataEntity?.txnRRN.isNullOrEmpty()) {
                txnResponse.rrn = txnDataEntity?.txnRRN
            }
            return txnResponse
        }
    }
}