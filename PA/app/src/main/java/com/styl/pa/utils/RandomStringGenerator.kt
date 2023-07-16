package com.styl.pa.utils;

import java.lang.StringBuilder
import java.security.SecureRandom


class RandomStringGenerator @JvmOverloads constructor(
    private val random: SecureRandom = SecureRandom(
        System.nanoTime().toString().toByteArray()
    ), defaultLength: Int = 0
) {

    @JvmOverloads
    fun randomString(length: Int = 0): String {
        var len = length
        if (len <= 0) {
            len = 16
        }
        val stringBuilder = StringBuilder()
        var i = 0
        var lastSame: Boolean
        while (i < len) {
            lastSame = false
            var next = random.nextInt(122)
            next = if (next < 48) next + 48 else next
            if (next > 57 && next < 65 || next > 90 && next < 97) {
                continue
            }
            val nextChar = next.toChar()
            if (i > 0) {
                val lastChar = stringBuilder[stringBuilder.length - 1]
                if (lastChar == nextChar) {
                    lastSame = true
                }
            }
            if (i > 1) {
                val secondLastChar = stringBuilder[stringBuilder.length - 2]
                if (secondLastChar == nextChar && lastSame) {
                    continue
                }
            }
            stringBuilder.append(nextChar)
            i++
        }
        return stringBuilder.toString()
    }
}
