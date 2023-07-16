package com.styl.pa.entities.proxy

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class ProxyRequestHeader(
    @SerializedName("Content-Type")
    var contentType: String?
) {
    companion object {
        var JSON_TYPE = "application/json"
    }
}