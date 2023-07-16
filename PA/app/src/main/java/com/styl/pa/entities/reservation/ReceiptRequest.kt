package com.styl.pa.entities.reservation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.payment.PaymentRequest
import java.util.*


/**
 * Created by Ngatran on 09/26/2018.
 */
@Entity(
        tableName = "Receipt",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = PaymentRequest::class,
                        parentColumns = arrayOf("txnNo"),
                        childColumns = arrayOf("txnNo")
                )
        )
)
class ReceiptRequest(
        @PrimaryKey
        @ColumnInfo(name = "shoppingCartId")
        @SerializedName("shoppingCartId")
        var shoppingCartId: String = "",
        @ColumnInfo(name = "paymentRefNo")
        @SerializedName("paymentRefNo")
        var paymentRefNo: String?,
        @ColumnInfo(name = "cardRefNo")
        @SerializedName("cardRefNo")
        var cardRefNo: String?,
        @ColumnInfo(name = "paymentMethod")
        @SerializedName("paymentMethod")
        var paymentMethod: Int?,
        @ColumnInfo(name = "committeeId")
        @SerializedName("committeeID")
        var committeeId: String?
) {
    @ColumnInfo(name = "txnNo")
    @SerializedName("txnNo")
    var txnNo: String? = ""

    @ColumnInfo(name = "errorCode")
    @SerializedName("errorCode")
    var errorCode: String? = null

    @ColumnInfo(name = "errorMessage")
    @SerializedName("errorMessage")
    var errorMessage: String? = null

    @ColumnInfo(name = "errorLatestUpdate")
    @SerializedName("errorLatestUpdate")
    var errorLatestUpdate: Long? = null

    @ColumnInfo(name = "createdAt")
    @SerializedName("createdAt")
    var createdAt: Long? = null

    companion object {

        const val NETS_METHOD = 15
        const val EZ_LINK_METHOD = 16
        const val DEBIT_CREDIT_METHOD = 17
    }

    fun updateErrorGenerateReceipt(errorCode: String?, errorMessage: String?) {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
        this.createdAt = (Calendar.getInstance().timeInMillis / 1000)
        this.errorLatestUpdate = this.createdAt
    }
}