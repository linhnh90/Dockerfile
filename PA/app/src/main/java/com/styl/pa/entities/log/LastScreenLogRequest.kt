package com.styl.pa.entities.log

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 10/22/2018.
 */
class LastScreenLogRequest {
    @SerializedName("lastScreen")
    @Expose
    private var lastScreen: String? = null
    @SerializedName("reportedTime")
    @Expose
    private var reportedTime: Long? = null
    @SerializedName("payerId")
    @Expose
    private var payerId: String? = null

    constructor()

    constructor(lastScreen: String, reportedTime: Long) {
        this.lastScreen = lastScreen
        this.reportedTime = reportedTime
    }

    constructor(lastScreen: String, reportedTime: Long, payerId: String?) {
        this.lastScreen = lastScreen
        this.reportedTime = reportedTime
        this.payerId = payerId
    }

    fun getLastScreen(): String? {
        return lastScreen
    }

    fun setLastScreen(lastScreen: String) {
        this.lastScreen = lastScreen
    }

    fun getReportedTime(): Long? {
        return reportedTime
    }

    fun setReportedTime(reportedTime: Long?) {
        this.reportedTime = reportedTime
    }

}