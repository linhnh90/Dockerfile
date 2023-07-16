package com.styl.pa.entities.sendEmail

import com.google.gson.annotations.SerializedName

class SendEmailResponse {
    @SerializedName("message")
    var message: String? = ""
        get() = field
        set(value) {
            field = value
        }
}