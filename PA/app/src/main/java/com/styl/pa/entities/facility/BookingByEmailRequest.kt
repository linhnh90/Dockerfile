package com.styl.pa.entities.facility

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 10/16/2018.
 */
class BookingByEmailRequest {
    @SerializedName("FullName")
    private var fullName: String? = ""

    @SerializedName("PreferredDate")
    private var preferredDate: String? = ""

    @SerializedName("Resource")
    private var resource: String? = ""

    @SerializedName("ReceiverEmail")
    private var receiverEmail: String? = ""

    @SerializedName("ContactNumber")
    private var contactNumber: String? = ""

    @SerializedName("Purpose")
    private var purpose: String? = ""

    @SerializedName("StartTime")
    private var startTime: String? = ""

    @SerializedName("Duration")
    private var duration: String? = ""

    @SerializedName("CompanyName")
    private var companyName: String? = ""

    @SerializedName("ROC")
    private var roc: String? = ""

    @SerializedName("EndPreferedDate")
    private var endPreferedDate: String? = ""

    @SerializedName("EndTime")
    private var endTime: String? = ""

    @SerializedName("EndDuration")
    private var endDuration: String? = ""

    constructor() {}

    constructor(fullName: String, preferredDate: String, resource: String?, receiverEmail: String,
                contactNumber: String, purpose: String, startTime: String, duration: String,
                companyName: String, roc: String, endPreferedDate: String?, endTime: String?, endDuration: String?) {
        this.fullName = fullName
        this.preferredDate = preferredDate
        this.resource = resource
        this.receiverEmail = receiverEmail
        this.contactNumber = contactNumber
        this.purpose = purpose
        this.startTime = startTime
        this.duration = duration
        this.companyName = companyName
        this.roc = roc
        this.endPreferedDate = endPreferedDate
        this.endTime = endTime
        this.endDuration = endDuration
    }
}