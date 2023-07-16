package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class ParticipantItem(
        @SerializedName("fullName")
        var fullName: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("mobileNumber")
        var contact: String?,
        @SerializedName("idNo")
        var idNo: String?,
        @SerializedName("idType")
        var idType: String?,
        @SerializedName("nationality")
        var nationality: String?,
        @SerializedName("race")
        var race: String?,
        @SerializedName("gender")
        var gender: String?,
        @SerializedName("doB", alternate = ["dob"])
        var doB: String?,
        @SerializedName("nextOfKinName")
        var nextOfKinName: String? = null,
        @SerializedName("nextOfKinContactNumber")
        var nextOfKinContactNumber: String? = null,
        @SerializedName("saveRegistration")
        var saveRegistration: Boolean? = false

)