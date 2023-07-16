package com.styl.pa.entities.keepAlive

import com.google.gson.annotations.SerializedName

class KeepAliveRequest(
    @SerializedName("paperStatus")
    var paperStatus: Int? = null,
    @SerializedName("printerStatus")
    var printerStatus: Int? = null,
    @SerializedName("scannerStatus")
    var scannerStatus: Int? = null,
    @SerializedName("networkStatus")
    var networkStatus: Int? = null
) {
    companion object {
        const val PAPER_STATUS_NORMAL       = 0
        const val PAPER_STATUS_ERROR     = 1
        const val PAPER_STATUS_LOW_PAPER    = 2
        const val PRINTER_STATUS_NORMAL     = 0
        const val PRINTER_STATUS_ERROR      = 1
        const val SCANNER_STATUS_NORMAL     = 0
        const val SCANNER_STATUS_ERROR      = 1
        const val NETWORK_STATUS_NORMAL     = 0
        const val NETWORK_STATUS_ERROR      = 1
    }
}