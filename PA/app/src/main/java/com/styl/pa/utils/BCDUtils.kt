/**
 *  Copyright 2010 Firat Salgur
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.styl.pa.utils

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import kotlin.experimental.and

@ExcludeFromJacocoGeneratedReport
class BCDUtils {

    @ExcludeFromJacocoGeneratedReport
    companion object {
        fun stringToBcd(string: String): ByteArray {
            var number: Long = string.toLong()
            var digits = 0

            var temp = number
            while (temp != 0L) {
                digits++
                temp /= 10
            }

            val byteLen = if (digits % 2 == 0) digits / 2 else (digits + 1) / 2

            val bcd = ByteArray(byteLen)

            for (i in 0 until digits) {
                val tmp = (number % 10).toByte()

                if (i % 2 == 0) {
                    bcd[i / 2] = tmp
                } else {
                    bcd[i / 2] = bcd[i / 2].toInt().or(tmp.toInt().shl(4)).toByte()
                }

                number /= 10
            }

            for (i in 0 until byteLen / 2) {
                val tmp = bcd[i]
                bcd[i] = bcd[byteLen - i - 1]
                bcd[byteLen - i - 1] = tmp
            }

            return bcd
        }

        fun bcdToString(bcd: ByteArray): String {
            val sb = StringBuffer()

            for (i in bcd.indices) {
                sb.append(bcdToString(bcd[i]))
            }

            return sb.toString()
        }

        private fun bcdToString(bcd: Byte): String {
            val sb = StringBuffer()

            var high = bcd and 0xf0.toByte()
            high = high.toInt().ushr(4).toByte()
            high = high and 0x0f
            val low = bcd and 0x0f

            sb.append(high.toInt())
            sb.append(low.toInt())

            return sb.toString()
        }
    }
}