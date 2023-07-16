package com.styl.pa.entities.keepAlive

import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.sendmail.SendMailResponse

/**
 * Created by Ngatran on 09/27/2019.
 */
class KeepAliveResponse : SendMailResponse {
    @SerializedName("maintenance")
    val maintenance: Maintenance? = null

    @SerializedName("health")
    var health: Heath? = null

    constructor()


    class Maintenance {
        @SerializedName("startTime", alternate = ["StartTime"])
        var startTime: Long? = null
        @SerializedName("endTime", alternate = ["EndTime"])
        var endTime: Long? = null
    }

    class Heath {
        @SerializedName("status", alternate = ["Status"])
        var status: String? = null
    }
}