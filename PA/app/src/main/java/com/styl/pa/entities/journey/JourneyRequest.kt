package com.styl.pa.entities.journey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 03/29/2019.
 */
class JourneyRequest(
        @SerializedName("sessionId")
        @Expose
        var sessionId: String? = null,
        @SerializedName("screenList")
        @Expose
        var screenList: List<JourneyScreenInfo>? = null,
        @SerializedName("userInfo")
        @Expose
        var userInfo: UserInfo? = null) {
}