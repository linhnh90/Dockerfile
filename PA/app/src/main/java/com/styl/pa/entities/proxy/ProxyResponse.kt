package com.styl.pa.entities.proxy

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class ProxyResponse<T>(
    @SerializedName("bodyContent")
    var body: T?,
    @SerializedName("bodyContentText")
    var bodyText: String?,
    @SerializedName("httpStatusCode", alternate = ["responseStatusCode"])
    var httpStatusCode: Int?
) {
}