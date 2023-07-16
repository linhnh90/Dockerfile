package com.styl.pa.utils

import android.content.Context
import com.styl.pa.BuildConfig
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.modules.terminal.upt1000fterminal.CastleTerminalService

/**
 * Created by trangpham on 8/29/2018
 */

@ExcludeFromJacocoGeneratedReport
class MySharedPref(var context: Context?) {

    @ExcludeFromJacocoGeneratedReport
    companion object {
        private const val PREFERENCE_NAME = "pa"

        private const val KEY_ACTIVATION_CODE = "activationCode"
        private const val KEY_KIOSK_INFO = "kioskInfo"
        private const val KEY_EKIOSK_HEADER = "eKioskHeader"
        private const val KEY_IV = "IV"
        private const val KEY_ANTI_VIRUS_VERSION = BuildConfig.APPLICATION_ID + "keys.KEY_ANTI_VIRUS_VERSION"
        private const val KEY_FIRMWARE_VERSION = BuildConfig.APPLICATION_ID + "keys.KEY_FIRMWARE_VERSION"
        private const val TERMINAL_FIRST_TIME_HANDLING = "terminalFirstTimeHandling"
        private const val TERMINAL_SEQUENCE_NUMBER = "terminalSequenceNumber"
        private const val TERMINAL_LIST = "terminalList"
        private const val KEY_COURSE_CATEGORY_LIST = "courseCategories"
        private const val KEY_EVENT_CATEGORY_LIST = "eventCategories"
        private const val KEY_SERIAL_PORT = "serialPort"
        private const val LATEST_OTA_PACKAGE = "latestOtaPackageSize"
        private const val KEY_UPGRADE_SYSTEM_SUCCESS = "isUpgradeSystemSuccess"
        private const val KEY_UPGRADE_APPLICATION_SUCCESS = "isUpgradeAppSuccess"
        private const val KEY_COPY_ANTIVIRUS_SUCCESS = "isCopyAntivirusSuccess"
        private const val KEY_PREVIOUS_OTA_CONFIG = "previousOTAConfig"
        private const val KEY_SCREENSAVER = "screensaver"
        private const val KEY_LAST_UPDATE_SCREENSAVER = "lastUpdateScreensaver"
    }

    private var pref = context?.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    var activationCode: String?
        get() {
            if (iv.isNullOrEmpty()) {
                return pref?.getString(KEY_ACTIVATION_CODE, "")
            }
            val keyStoreUtils = KeyStoreUtils(context)
            val value = pref?.getString(KEY_ACTIVATION_CODE, "")
            try {
                return keyStoreUtils.decrypt(value)
            } catch (e: Exception) {
                return ""
            }
        }
        set(value) {
            val keyStoreUtils = KeyStoreUtils(context)
            val encrypted = keyStoreUtils.encrypt(value)
            val editor = pref?.edit()
            editor?.putString(KEY_ACTIVATION_CODE, encrypted)
            editor?.apply()
        }

    var kioskInfo: String?
        get() {
            if (iv.isNullOrEmpty()) {
                return pref?.getString(KEY_KIOSK_INFO, "")
            }
            val keyStoreUtils = KeyStoreUtils(context)
            val value = pref?.getString(KEY_KIOSK_INFO, "")
            try {
                return keyStoreUtils.decrypt(value)
            } catch (e: Exception) {
                return ""
            }
        }
        set(value) {
            val keyStoreUtils = KeyStoreUtils(context)
            val encrypted = keyStoreUtils.encrypt(value)
            val editor = pref?.edit()
            editor?.putString(KEY_KIOSK_INFO, encrypted)
            editor?.apply()
        }

    var eKioskHeader: String?
        get() {
            if (iv.isNullOrEmpty()) {
                return pref?.getString(KEY_EKIOSK_HEADER, "")
            }
            val keyStoreUtils = KeyStoreUtils(context)
            val value = pref?.getString(KEY_EKIOSK_HEADER, "")
            try {
                return keyStoreUtils.decrypt(value)
            } catch (e: Exception) {
                return ""
            }
        }
        set(value) {
            val keyStoreUtils = KeyStoreUtils(context)
            val encrypted = keyStoreUtils.encrypt(value)
            val editor = pref?.edit()
            editor?.putString(KEY_EKIOSK_HEADER, encrypted)
            editor?.apply()
        }

    var iv: String?
        get() = pref?.getString(KEY_IV, "")
        set(value) {
            val editor = pref?.edit()
            editor?.putString(KEY_IV, value!!)
            editor?.apply()
        }

    var previousConfig: String?
        get() {
            if (iv.isNullOrEmpty()) {
                return pref?.getString(KEY_PREVIOUS_OTA_CONFIG, "")
            }
            val keyStoreUtils = KeyStoreUtils(context)
            val value = pref?.getString(KEY_PREVIOUS_OTA_CONFIG, "")
            try {
                return keyStoreUtils.decrypt(value)
            } catch (e: Exception) {
                return ""
            }
        }
        set(value) {
            val keyStoreUtils = KeyStoreUtils(context)
            val encrypted = keyStoreUtils.encrypt(value)
            val editor = pref?.edit()
            editor?.putString(KEY_PREVIOUS_OTA_CONFIG, encrypted)
            editor?.apply()
        }

    var antiVirusVersion: String?
        get() = pref?.getString(KEY_ANTI_VIRUS_VERSION, "0")
        set(value) {
            val editor = pref?.edit()
            editor?.putString(KEY_ANTI_VIRUS_VERSION, value!!)
            editor?.apply()
        }

    var isUpgradeSystemSuccessUpdated: Boolean
        get() = pref?.getBoolean(KEY_UPGRADE_SYSTEM_SUCCESS, false) ?: false
        set(value) {
            val editor = pref?.edit()
            editor?.putBoolean(KEY_UPGRADE_SYSTEM_SUCCESS, value)
            editor?.apply()
        }

    var isUpgradeAppSuccess: Boolean
        get() = pref?.getBoolean(KEY_UPGRADE_APPLICATION_SUCCESS, false) ?: false
        set(value) {
            val editor = pref?.edit()
            editor?.putBoolean(KEY_UPGRADE_APPLICATION_SUCCESS, value)
            editor?.apply()
        }

    var isCopyAntivirusDbSuccess: Boolean
        get() = pref?.getBoolean(KEY_COPY_ANTIVIRUS_SUCCESS, false) ?: false
        set(value) {
            val editor = pref?.edit()
            editor?.putBoolean(KEY_COPY_ANTIVIRUS_SUCCESS, value)
            editor?.apply()
        }


    var firmwareVersion: String?
        get() = pref?.getString(KEY_FIRMWARE_VERSION, "0")
        set(value) {
            val editor = pref?.edit()
            editor?.putString(KEY_FIRMWARE_VERSION, value!!)
            editor?.apply()
        }

    var latestOtaPackageSize: Long
        get() = pref?.getLong(LATEST_OTA_PACKAGE, 0L) ?: 0
        set(value) {
            val editor = pref?.edit()
            editor?.putLong(LATEST_OTA_PACKAGE, value)
            editor?.apply()
        }

    var terminalFirstTimeHandling: Boolean
        get() = pref?.getBoolean(TERMINAL_FIRST_TIME_HANDLING, true) ?: true
        set(value) {
            val editor = pref?.edit()
            editor?.putBoolean(TERMINAL_FIRST_TIME_HANDLING, value)
            editor?.apply()
        }

    var terminalSequenceNumber: Int
        get() = pref?.getInt(TERMINAL_SEQUENCE_NUMBER, 1) ?: 1
        set(value) {
            val editor = pref?.edit()
            editor?.putInt(TERMINAL_SEQUENCE_NUMBER, value)
            editor?.apply()
        }

    var terminalList: String
        get() = pref?.getString(TERMINAL_LIST, "") ?: ""
        set(value) {
            val editor = pref?.edit()
            editor?.putString(TERMINAL_LIST, value)
            editor?.apply()
        }

    var serialPort: String
        get() = pref?.getString(KEY_SERIAL_PORT, CastleTerminalService.UART_PORT_NAME) ?: CastleTerminalService.UART_PORT_NAME
        set(value) {
            val editor = pref?.edit()
            editor?.putString(KEY_SERIAL_PORT, value)
            editor?.apply()
        }

    var courseCategories: String?
        get() = pref?.getString(KEY_COURSE_CATEGORY_LIST, "") ?: ""
        set(value) {
            val editor = pref?.edit()
            editor?.putString(KEY_COURSE_CATEGORY_LIST, value)
            editor?.apply()
        }

    var eventCategories: String?
        get() = pref?.getString(KEY_EVENT_CATEGORY_LIST, "") ?: ""
        set(value) {
            val editor = pref?.edit()
            editor?.putString(KEY_EVENT_CATEGORY_LIST, value)
            editor?.apply()
        }

    var screensaver: String?
        get() = pref?.getString(KEY_SCREENSAVER, "") ?: ""
        set(value) {
            val editor = pref?.edit()
            editor?.putString(KEY_SCREENSAVER, value)
            editor?.apply()
        }

    var lastUpdateScreensaver: Long
        get() = pref?.getLong(KEY_LAST_UPDATE_SCREENSAVER, 0L) ?: 0L
        set(value) {
            val editor = pref?.edit()
            editor?.putLong(KEY_LAST_UPDATE_SCREENSAVER, value)
            editor?.apply()
        }
}