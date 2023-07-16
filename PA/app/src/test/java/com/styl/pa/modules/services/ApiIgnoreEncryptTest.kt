package com.styl.pa.modules.services

import com.styl.pa.services.ApiEncryptionConfig
import com.styl.pa.services.api_encryption.ApiIgnoreEncrypt
import org.junit.Test

class ApiIgnoreEncryptTest {

    @Test
    fun testApiIgnoreEncrypt() {
        var isNotEncrypt = ApiIgnoreEncrypt.getInstance().isNoNeedToEncrypt(ApiEncryptionConfig.API_GET_SCREENSAVER)
        assert(isNotEncrypt)

        isNotEncrypt = ApiIgnoreEncrypt.getInstance().isNoNeedToEncrypt("/api/kiosk/proxy/request")
        assert(!isNotEncrypt)
    }
}