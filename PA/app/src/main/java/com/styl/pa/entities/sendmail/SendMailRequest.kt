package com.styl.pa.entities.sendmail

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/30/2018
 */
class SendMailRequest(
    @SerializedName("Receivers")
    var receivers: List<String>?,
    @SerializedName("Subject")
    var subject: String?,
    @SerializedName("BodyContent")
    var content: String?
) {
}