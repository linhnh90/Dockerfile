package com.styl.pa.services

class ApiEncryptionConfig {

    companion object {

        const val API_ENCRYPTION_ISS = "EncryptionPayload"
        const val API_ENCRYPTION_AUD = "eKioskSIT"

        /* List API ignore encryption */
        /*----------*/
        const val API_GET_SCREENSAVER       = "/api/kiosk/screensaver/get"
        const val API_ACTIVATE_KIOSK        = "/api/kiosk/activate"
        const val API_AUTHENTICATION_KIOSK  = "/api/kiosk/authenticate"

        /*----------*/
    }

    enum class ApiWithoutEncrypt(var apiPath: String) {
        API_1(API_GET_SCREENSAVER)
    }
}