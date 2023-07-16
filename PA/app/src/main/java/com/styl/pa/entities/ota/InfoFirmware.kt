package com.styl.pa.entities.ota

/**
 * Created by NguyenHang on 9/4/2020.
 */
class InfoFirmware {
    companion object {
        const val SYSTEM = "system"
        const val APPLICATION = "application"
        const val ANTI_VIRUS = "anti-virus"
        const val SETUP = "SetupOta"
    }

    var type: String? = null
    var version: String? = null
    var path: String? = null
    var checksum: String? = null

    constructor(type: String) {
        this.type = type
    }

    override fun toString(): String {
        return "InfoFirmware(type=$type, version=$version, path=$path, checksum=$checksum)"
    }

}