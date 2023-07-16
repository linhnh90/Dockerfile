package com.styl.pa.entities.payment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 10/9/2018
 */
@Entity(
    tableName = "TransactionItem",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = PaymentRequest::class,
            parentColumns = arrayOf("txnNo"),
            childColumns = arrayOf("txnNo")
        )
    )
)
class PaymentItem(
    @ColumnInfo(name = "resourceId")
    @SerializedName("ResourceId")
    var resourceId: String?,
    @ColumnInfo(name = "resourceName")
    @SerializedName("ResourceName")
    var resourceName: String?,
    @ColumnInfo(name = "resourceType")
    @SerializedName("ResourceType")
    var resourceType: String?,
    @ColumnInfo(name = "resourceTypeName")
    @SerializedName("ResourceTypeName")
    var resourceTypeName: String?,
    @ColumnInfo(name = "beforeDiscountAmount")
    @SerializedName("BeforeDiscountAmount")
    var beforeDiscountAmount: Int?,
    @ColumnInfo(name = "discountAmount")
    @SerializedName("DiscountAmount")
    var discountAmount: Int?,
    @ColumnInfo(name = "paymentAmount")
    @SerializedName("PaymentAmount")
    var paymentAmount: Int?,
    @ColumnInfo(name = "resourceDateTime")
    @SerializedName("resourceDateTime")
    var resourceDateTime: String?
) {

    companion object {
        const val TYPE_CLASS = "Class"
        const val TYPE_INTEREST_GROUP = "InterestGroup"
        const val TYPE_FACILITY = "Facility"
        const val TYPE_EVENT = "Event"
    }

    @ColumnInfo(name = "txnNo")
    @SerializedName("TxnNo")
    var txnNo: String? = ""
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("idno")
    var id: Long = 0
    @ColumnInfo(name = "outletId")
    @SerializedName("outletId")
    var outletId: String? = null
    @ColumnInfo(name = "outletName")
    @SerializedName("outletName")
    var outletName: String? = null
    @ColumnInfo(name = "customerAge")
    @SerializedName("customerAge")
    var customerAge: String? = null
    @ColumnInfo(name = "outletTypeName")
    @SerializedName("outletTypeName")
    var outletTypeName: String? = null
}