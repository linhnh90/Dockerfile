package com.styl.pa.entities.ota

/**
 * Created by NguyenHang on 9/21/2020.
 */
enum class ErrorMessageOta (var error: String) {
    DOWNLOAD_FAIL("Download OTA package failed"),
    UNZIP_FAIL("Unzip OTA package failed"),
    INVALID_FORMAT("The configuration file is in wrong format"),
    FILE_NOT_INTEGRITY("File is not integrity");
}