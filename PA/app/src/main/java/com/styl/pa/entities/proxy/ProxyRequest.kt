package com.styl.pa.entities.proxy

import com.google.gson.annotations.SerializedName

/**
 * Created by trangpham on 9/26/2018
 */
class ProxyRequest<T>(
    @SerializedName("Headers")
    var header: ProxyRequestHeader?,
    @SerializedName("BodyContent")
    var body: T?,
    @SerializedName("Method")
    var method: String?,
    @SerializedName("HostName")
    var hostName: String?,
    @SerializedName("Uri")
    var uri: String?,
    @SerializedName("RequireApiVersion")
    var hasVersion: Boolean = true,
    @SerializedName("RequestSensitiveInformation")
    var isSensitive: Boolean = false
) {
    companion object {
        var GET_METHOD = "Get"
        var POST_METHOD = "Post"
        var DELETE_METHOD = "DELETE"
    }
}