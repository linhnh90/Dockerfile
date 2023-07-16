package com.styl.pa.enums

import org.junit.Test

class OtaErrorTest {
    @Test
    fun getOtaError() {
        var otaError = OtaError.SUCCESS
        assert(otaError.errorCode == "E0")
        otaError = OtaError.PACKAGE_LINK_NOT_FOUND
        assert(otaError.errorCode == "E01")
        otaError = OtaError.FILE_CHECKSUM_NOT_MATCH
        assert(otaError.errorCode == "E02")
        otaError = OtaError.INVALID_PUBLIC_KEY
        assert(otaError.errorCode == "E03")
        otaError = OtaError.SIGNATURE_NOT_MATCH
        assert(otaError.errorCode == "E04")
        otaError = OtaError.UNABLE_UNZIP
        assert(otaError.errorCode == "E05")
        otaError = OtaError.INSTALLS_SYSTEM_FAIL
        assert(otaError.errorCode == "E06")
        otaError = OtaError.INSTALLS_APP_FAIL
        assert(otaError.errorCode == "E07")
        otaError = OtaError.COPY_ANTI_VIRUS_FAIL
        assert(otaError.errorCode == "E08")
        otaError = OtaError.DOWNLOAD_OTA_FAIL
        assert(otaError.errorCode == "E09")
        otaError = OtaError.PARSE_PACKAGE_FAIL
        assert(otaError.errorCode == "E10")
        otaError = OtaError.PARSE_CONFIG_FAIL
        assert(otaError.errorCode == "E11")
    }
}