package com.styl.pa.entities.promocode

import com.google.gson.annotations.SerializedName

class AvailablePromoCodeRequest(
    @SerializedName("productType")
    var productType: ArrayList<String>? = null,
    @SerializedName("committeeId")
    var committeeId: String? = null,
    @SerializedName("isMember")
    var isMember: Boolean? = null
)