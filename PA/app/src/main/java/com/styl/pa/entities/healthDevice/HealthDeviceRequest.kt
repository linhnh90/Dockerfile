package com.styl.pa.entities.healthDevice

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 10/23/2018.
 */
class HealthDeviceRequest {
    @SerializedName("devices")
    @Expose
    private var devices: ArrayList<InfoHealthDevice>? = ArrayList()
    @SerializedName("updatedAt")
    @Expose
    private var updatedAt: Long? = null

    constructor()

    constructor(deviceInfo: InfoHealthDevice) {
        val infoArray = java.util.ArrayList<InfoHealthDevice>()
        infoArray.add(deviceInfo)
        this.devices = infoArray
        this.updatedAt = System.currentTimeMillis() / 1000
    }

    constructor(devices: ArrayList<InfoHealthDevice>) {
        this.devices = devices
        this.updatedAt = System.currentTimeMillis() / 1000
    }

    constructor(devices: ArrayList<InfoHealthDevice>, updatedAt: Long?) {
        this.devices = devices
        this.updatedAt = updatedAt
    }

    fun getDevices(): ArrayList<InfoHealthDevice>? {
        return devices
    }

    fun setDevices(devices: ArrayList<InfoHealthDevice>) {
        this.devices = devices
    }

    fun getUpdatedAt(): Long? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: Long?) {
        this.updatedAt = updatedAt
    }

}