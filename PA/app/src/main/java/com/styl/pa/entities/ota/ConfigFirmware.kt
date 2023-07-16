package com.styl.pa.entities.ota

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.lang.Exception

/**
 * Created by NguyenHang on 9/8/2020.
 */
class ConfigFirmware {
    @SerializedName("version")
    var version: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("path")
    var path: String? = null
    @SerializedName("checksum")
    var checksum: String? = null
    @SerializedName("kioskIds")
    var kioskIds: ArrayList<String>? = null
    @SerializedName("firmwares")
    var firmwares: ArrayList<InfoFirmware>? = null

    override fun toString(): String {
        return try {
            Gson().toJson(this)
        } catch (e: Exception) {
            ""
        }
    }
}