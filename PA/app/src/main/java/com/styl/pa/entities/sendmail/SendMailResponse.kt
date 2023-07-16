package com.styl.pa.entities.sendmail

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/30/2018
 */
open class SendMailResponse {
    @SerializedName("Message", alternate = arrayOf("message"))
    var message: String? = null
}