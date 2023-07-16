package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class FacilityRequest (
        @SerializedName("cartId")
        var cartId: String?,
        @SerializedName("facilities")
        var items: ArrayList<FacilityRequestItem>?,

        @SerializedName("isSomeoneElse")
        var isSomeoneElse: Boolean? = false,

        @SerializedName("fullName")
        var fullName: String? = "",

        @SerializedName("email")
        var email: String? = "",

        @SerializedName("mobileNumber")
        var mobileNumber: String? = ""
)