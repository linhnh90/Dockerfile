package com.styl.pa.entities.participant

import com.google.gson.annotations.SerializedName

class ErrorValidation {
    @SerializedName("Actions")
    var action: Any? = null
    @SerializedName("HasError")
    var hasErr: Boolean? = false
    @SerializedName("Message")
    var message: String? = null
}