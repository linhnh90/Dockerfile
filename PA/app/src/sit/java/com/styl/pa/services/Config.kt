package com.styl.pa.services

/**
 * Created by trangpham on 7/23/2018
 */
class Config {

    companion object {

        const val BASE_URL          = "https://intranet.pa-sit.styl.solutions/"
        const val BASE_IMAGE_URL    = "https://paces.uat.onepa.gov.sg"

        const val OTA_URL           = "https://ekiosk.uat.pa.gov.sg/"

        const val CONNECT_TIMEOUT: Long     = 30000
        const val READ_TIMEOUT: Long        = 200000 // 200s
        const val OTA_READ_TIMEOUT: Long    = 90000 //90s

        const val INTRANET_POLICY_ID = ""
        const val INTERNAL_POLICY_ID = ""
        const val EXTERNAL_POLICY_ID = ""
        const val NAME = ""
        const val IAT = ""
    }
}