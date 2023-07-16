package com.styl.pa.services

/**
 * Created by trangpham on 8/29/2018
 */
class Config {

    companion object {

        const val BASE_URL              = "https://services.pa.gov.sg/api/ekioskexternal/"
        const val BASE_IMAGE_URL        = "https://www.onepa.gov.sg"

        const val OTA_URL               = "https://ekiosk.pa.gov.sg/"

        const val CONNECT_TIMEOUT: Long     = 30000
        const val READ_TIMEOUT: Long        = 200000 // 200s
        const val OTA_READ_TIMEOUT: Long    = 90000 // 90s

        const val INTRANET_POLICY_ID = "6023b69852b90e000fd17573"
        const val INTERNAL_POLICY_ID = "6023b6a7e42237000ddd4031"
        const val EXTERNAL_POLICY_ID = "6023b6b752b90e000fd17574"
        const val NAME = "activate-kiosk"
        const val IAT = "1516239022"
    }
}