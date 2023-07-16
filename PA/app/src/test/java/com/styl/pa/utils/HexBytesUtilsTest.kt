package com.styl.pa.utils

import org.junit.Assert
import org.junit.Test

class HexBytesUtilsTest {

    @Test(expected = IllegalStateException::class)
    fun createHexBytesUtils() {
        HexBytesUtils()
    }

    @Test
    fun hexStr2Bytes_normal() {
        val bytes = HexBytesUtils.hexStr2Bytes("0a")
        assert(bytes.contentEquals(byteArrayOf(0x0a.toByte())))
    }

    @Test
    fun hexStr2Bytes_special_characters() {
        val bytes = HexBytesUtils.hexStr2Bytes("%a")
        assert(bytes.isEmpty())
    }

    @Test
    fun hexStr2Bytes_empty_str() {
        val bytes = HexBytesUtils.hexStr2Bytes("")
        assert(bytes.isEmpty())
    }

    @Test
    fun hexStr2Bytes_null_str() {
        val bytes = HexBytesUtils.hexStr2Bytes(null)
        assert(bytes.isEmpty())
    }

    @Test
    fun hexStringToByteArray() {
        val bytes = HexBytesUtils.hexStringToByteArray("0a")
        assert(bytes.contentEquals(byteArrayOf(0x0a.toByte())))
    }

    @Test
    fun bytesToHex() {
        val str = HexBytesUtils.bytesToHex(byteArrayOf(0x0a.toByte(), 0x05.toByte()))
        assert("0a05".equals(str, true))
    }

    @Test
    fun intToHex() {
        val str = HexBytesUtils.intToHex(arrayOf(0x0a, 0x05).toIntArray())
        assert("0a05".equals(str, true))
    }
}