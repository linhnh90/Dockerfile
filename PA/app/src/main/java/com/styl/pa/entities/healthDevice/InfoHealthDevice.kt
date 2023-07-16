package com.styl.pa.entities.healthDevice

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 10/23/2018.
 */
class InfoHealthDevice {
    companion object {
        const val TERMINAL_NAME = "Terminal"
        const val SCANNER_NAME = "Scanner"
        const val PRINTER_NAME = "Printer"

        const val TERMINAL_CONNECT = 100
        const val TERMINAL_DISCONNECT = 101

        const val SCANNER_CONNECT = 200
        const val SCANNER_DISCONNECT = 201

        const val PRINTER_CONNECT = 300
        const val PRINTER_DISCONNECT = 301
    }

    @SerializedName("name")
    @Expose
    private var name: String? = null
    @SerializedName("status")
    @Expose
    private var status: Int? = null

    constructor()

    constructor(name: String, status: Int?) {
        this.name = name
        this.status = status
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun isDisconnect(): Boolean {
        if (TERMINAL_DISCONNECT == status || SCANNER_DISCONNECT == status || PRINTER_DISCONNECT == status) {
            return true
        }

        return false
    }

    fun isConnect(): Boolean {
        if (TERMINAL_CONNECT == status || SCANNER_CONNECT == status || PRINTER_CONNECT == status) {
            return true
        }

        return false
    }
}