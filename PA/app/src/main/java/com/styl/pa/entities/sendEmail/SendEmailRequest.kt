package com.styl.pa.entities.sendEmail

import com.google.gson.annotations.SerializedName

class SendEmailRequest {

    @SerializedName("KioskId")
    var kioskId: Int? = -1
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Location")
    var location: LocationSendEmail? =null
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("ReceiptId")
    var receiptId: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("FullName")
    var fullName: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("ReceiverEmail")
    var receiverEmail: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("TotalFees")
    var totalFees: Int? = 0
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("TotalDiscounts")
    var totalDiscounts: Int? = 0
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("TotalAdjustments")
    var totalAdjustments: Int? = 0
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("TotalAmountPaid")
    var totalAmountPaid: Int? = 0
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("TotalGST")
    var totalGST: Int? = 0
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("CardInfo")
    var cardInfo: CardInfoSendEmail? = null
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("ReceiptDate")
    var receiptDate: String? = ""
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Items")
    var items: ArrayList<ItemsSendEmail>? = null
        get() = field
        set(value) {
            field = value
        }

    class LocationSendEmail{
        @SerializedName("Name")
        var name: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Address")
        var address: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Url")
        var url: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Fax")
        var fax: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Tel")
        var tel: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("PostalCode")
        var postalCode: String? = ""
            get() = field
            set(value) {
                field = value
            }
    }

    class CardInfoSendEmail {
        @SerializedName("PaymentMode")
        var paymentMode: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("CardNo")
        var cardNo: String? = ""
            get() = field
            set(value) {
                field = value
            }
    }

    class ItemsSendEmail {
        @SerializedName("ResourceId")
        var resourceId: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("ResourceName")
        var resourceName: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("ParticipantName")
        var participantName: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Venue")
        var venue: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Time")
        var time: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Date")
        var date: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Amount")
        var amount: Int? = 0
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("Discount")
        var discount: Int? = 0
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("AmountPaid")
        var amountPaid: Int? = 0
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("GSTNo")
        var gstNo: String? = ""
            get() = field
            set(value) {
                field = value
            }

        @SerializedName("LocationName")
        var locationName: String? = ""
            get() = field
            set(value) {
                field = value
            }
    }
}