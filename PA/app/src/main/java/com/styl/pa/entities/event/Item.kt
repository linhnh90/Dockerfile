package com.styl.pa.entities.event

import com.google.gson.annotations.SerializedName

class Item {
    @SerializedName("Title")
    var title: String? = null

    @SerializedName("Value")
    var value: String? = null

    @SerializedName("CRMValue")
    var crmValue: String? = null
}