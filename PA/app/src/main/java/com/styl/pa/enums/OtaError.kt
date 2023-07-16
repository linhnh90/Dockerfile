package com.styl.pa.enums

/**
 * Created by NguyenHang on 12/23/2020.
 */

enum class OtaError(var errorCode: String, var errorMessage: String) {
    SUCCESS("E0", ""),
    PACKAGE_LINK_NOT_FOUND("E01", "Update package link is not found"),
    FILE_CHECKSUM_NOT_MATCH("E02", "File checksum does not match"),
    INVALID_PUBLIC_KEY("E03", "Invalid public key"),
    SIGNATURE_NOT_MATCH("E04", "Signature does not match"),
    UNABLE_UNZIP("E05", "Unable to unzip the package"),
    INSTALLS_SYSTEM_FAIL("E06", "Unable to install system firmware"),
    INSTALLS_APP_FAIL("E07", "Unable to install kiosk app"),
    COPY_ANTI_VIRUS_FAIL("E08", "Unable to copy anti virus definitions"),
    DOWNLOAD_OTA_FAIL("E09", "Unable to download update package"),
    PARSE_PACKAGE_FAIL("E10", "Unable to parse the update package"),
    PARSE_CONFIG_FAIL("E11", "Unable to parse the update configuration");
}
