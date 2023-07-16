package com.styl.pa.services

/**
 * Created by trangpham on 7/23/2018
 */
class Config {

    companion object {

        const val BASE_URL              = "https://services.uat.pa.gov.sg/api/ekioskexternal/"
        const val BASE_IMAGE_URL        = "https://paces.uat.onepa.gov.sg"

        const val OTA_URL               = "https://ekiosk.uat.pa.gov.sg/"

        const val CONNECT_TIMEOUT: Long     = 30000
        const val READ_TIMEOUT: Long        = 200000 // 200s
        const val OTA_READ_TIMEOUT: Long    = 90000 // 90s

        const val INTRANET_POLICY_ID = "6023b34af1a89900014eef3a"
        const val INTERNAL_POLICY_ID = "6023b35ef1a89900014eef3b"
        const val EXTERNAL_POLICY_ID = "6023b380f1a89900014eef3c"
        const val NAME = "activate-kiosk"
        const val IAT = "1516239022"
    }
}