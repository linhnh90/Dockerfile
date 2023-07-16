package com.styl.pa.entities.event

import com.google.gson.annotations.SerializedName

class EventFormData {
    @SerializedName("FormID")
    var formId: String? = null

    @SerializedName("Title")
    var title: String? = null

    @SerializedName("Type")
    var type: String? = null

    @SerializedName("IsMyInfo")
    var isMyInfo: Boolean? = null

    @SerializedName("PcmStatus")
    var pcmStatus: Boolean? = null

    @SerializedName("IsRenewable")
    var isRenewable: Boolean? = null

    @SerializedName("ClearMyInfo")
    var clearMyInfo: Boolean? = null

    @SerializedName("Header")
    var header: String? = null

    @SerializedName("RetrieveMyInfo")
    var retrieveMyInfo: Boolean? = null

    @SerializedName("DayRange")
    var dayRange: Int? = null

    @SerializedName("FacilityId")
    var facilityId: String? = null

    @SerializedName("MinSlotDuration")
    var minSlotDuration: Int? = null

    @SerializedName("MaxSlotDuration")
    var maxSlotDuration: Int? = null

    @SerializedName("FieldSections")
    var fieldSections: FieldSections? = null

    @SerializedName("FormSubmissionErrorMessage")
    var formSubmissionErrorMessage: String? = null

    @SerializedName("IsEligibleForMemberShip")
    var isEligibleForMembership: Boolean? = null


}