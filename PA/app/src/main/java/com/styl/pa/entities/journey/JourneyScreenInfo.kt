package com.styl.pa.entities.journey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 03/29/2019.
 */
class JourneyScreenInfo {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("startTime")
    @Expose
    var startTime: String? = null
    @SerializedName("duration")
    @Expose
    var duration: Double? = null

    constructor()

    constructor(name: String, startTime: String, duration: Double) {
        this.name = name
        this.startTime = startTime
        this.duration = duration
    }
}