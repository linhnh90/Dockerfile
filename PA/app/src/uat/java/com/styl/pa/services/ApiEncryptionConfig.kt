package com.styl.pa.services

class ApiEncryptionConfig {

    companion object {

        const val API_ENCRYPTION_ISS = "EncryptionPayload"
        const val API_ENCRYPTION_AUD = "eKioskUAT"

        /* List API ignore encryption */
        /*----------*/
        const val API_GET_SCREENSAVER       = "/api/ekioskexternal/get-screensaver-images"

        /*----------*/
    }

    enum class ApiWithoutEncrypt(var apiPath: String) {
        API_1(API_GET_SCREENSAVER),
    }
}