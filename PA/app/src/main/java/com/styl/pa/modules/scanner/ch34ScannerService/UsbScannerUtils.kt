package com.styl.pa.modules.scanner.ch34ScannerService

import java.util.*

/**
 * Created by Nga Tran on 7/24/2020.
 */
object UsbScannerUtils {
    fun toHexString(arg: ByteArray?, length: Int): String {
        var result = String()
        if (arg != null) {
            for (i in 0 until length) {
                result = (result
                        + (if (Integer.toHexString(
                                if (arg[i] < 0) arg[i] + 256 else arg[i].toInt()
                        ).length == 1
                ) "0"
                        + Integer.toHexString(
                        if (arg[i] < 0) arg[i] + 256 else arg[i].toInt()
                ) else Integer.toHexString(
                        if (arg[i] < 0) arg[i] + 256 else arg[i].toInt()
                )) + " ")
            }
            return result
        }
        return ""
    }


    private fun convertStringToHex(str: String): String {
        val hex = StringBuffer()
        // loop chars one by one
        for (item in str.toCharArray()) {
            // convert char to int, for char `a` decimal 97
            val decimal = item.code
            // convert int to hex, for decimal 97 hex 61
            hex.append(Integer.toHexString(decimal))
        }

        return hex.toString().uppercase(Locale.ENGLISH)
    }

    fun toByteArray(arg: String?): ByteArray {
        if (arg != null) {
            val dataConvert = convertStringToHex(
                    arg
            ) + "0D0A"
            val newArray = CharArray(1000)
            val array = dataConvert.toCharArray()
            var length = 0
            for (i in array.indices) {
                if (array[i] != ' ') {
                    newArray[length] = array[i]
                    length++
                }
            }
            val evenLength = if (length % 2 == 0) length else length + 1
            if (evenLength != 0) {
                val data = IntArray(evenLength)
                data[evenLength - 1] = 0
                for (i in 0 until length) {
                    if (newArray[i] in '0'..'9') {
                        data[i] = newArray[i] - '0'
                    } else if (newArray[i] in 'a'..'f') {
                        data[i] = newArray[i] - 'a' + 10
                    } else if (newArray[i] in 'A'..'F') {
                        data[i] = newArray[i] - 'A' + 10
                    }
                }
                val byteArray = ByteArray(evenLength / 2)
                for (i in 0 until evenLength / 2) {
                    byteArray[i] = (data[i * 2] * 16 + data[i * 2 + 1]).toByte()
                }
                return byteArray
            }
        }
        return byteArrayOf()
    }
}