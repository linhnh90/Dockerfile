package com.styl.pa.entities.reservation

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Ngatran on 09/26/2018.
 */
class FacilityBookingAuthRequest {
    @SerializedName("outletid")
    private var outletid: String? = null
    @SerializedName("facilityid")
    var facilityid: String? = null
    @SerializedName("selectedDate")
    private var selectedDate: String? = null
    @SerializedName("selectedTimeRangeIdList")
    private var selectedTimeRangeIdList: String? = null
    @SerializedName("contactid")
    var contactid: String? = null
    @SerializedName("sessioncode")
    private var sessionCode: String? = null
    var mSessionCode: String?
        get() {
            return sessionCode
        }
        set(value) {
            this.sessionCode = value
        }

    constructor(outletId: String, facilityid: String, selectedDate: String, selectedTimeRangeIdList: String, contactid: String, sessionCode: String) {
        this.outletid = outletId
        this.facilityid = facilityid
        this.selectedDate = selectedDate
        this.selectedTimeRangeIdList = selectedTimeRangeIdList
        this.contactid = contactid
        this.sessionCode = sessionCode
    }
}