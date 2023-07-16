package com.styl.pa.entities.wirecard

import com.styl.pa.utils.BCDUtils
import kotlin.experimental.xor

/**
 * Created by trangpham on 9/3/2018
 */
class WireCardMessage(
        var stx: Byte? = STX,
        var lengthOfMessage: String?,
        var messageData: WireCardMessageData?,
        var etx: Byte? = ETX,
        var lrc: Byte?
) {

    companion object {

        const val SUCCESS_RESPONSE_CODE = "0"

        const val STX: Byte = 0x02
        const val ETX: Byte = 0x03
        const val ACK: Byte = 0x06
        const val NAK: Byte = 0x15

        const val LLLL_LENGTH = 2

        fun fromBytes(bytes: ByteArray?): WireCardMessage? {
            if (bytes == null || bytes.size < LLLL_LENGTH + 1 || bytes.get(0) != STX) {
                return null
            }

            var position = 0

            val stx = bytes.get(position)
            position++

            if (bytes.size < LLLL_LENGTH + 1 || stx != STX) {
                return null
            }

            val lengthBytes = bytes.copyOfRange(position, position + LLLL_LENGTH)
            val length = BCDUtils.bcdToString(lengthBytes)
            position += LLLL_LENGTH

            if (bytes.size < position + length.toInt() + 2) {
                return null
            }

            val messageData = WireCardMessageData.fromBytes(
                    bytes.copyOfRange(
                            position,
                            position + length.toInt()
                    )
            )
            position += length.toInt()

            val etx = bytes.get(position)

            var lrc: Byte? = 0x00
            for (i in 1..position) {
                lrc = lrc?.xor(bytes.get(i))
            }
            position++

            if (etx != ETX) {
                return null
            }

            if (lrc != bytes.get(position)) {
                return null
            }

            return WireCardMessage(lengthOfMessage = length, messageData = messageData, lrc = lrc)
        }
    }

    fun toBytes(): ByteArray? {
        if (lengthOfMessage.isNullOrEmpty() || lengthOfMessage!!.length.compareTo(LLLL_LENGTH * 2) != 0 ||
                messageData == null
        ) {
            return null
        }

        val llllBytes = ByteArray(LLLL_LENGTH)
        val temp = BCDUtils.stringToBcd(lengthOfMessage!!)
        System.arraycopy(temp, 0, llllBytes, LLLL_LENGTH - temp.size, temp.size)

        val messageDataBytes = messageData?.toBytes()

        if (messageDataBytes == null || messageDataBytes.size.compareTo(lengthOfMessage!!.toInt()) != 0) {
            return null
        }

        val totalLength = 1 + LLLL_LENGTH + messageDataBytes.size + 1 + 1
        val raw = ByteArray(totalLength)

        // calculate lrc
        var lrc: Byte? = 0x00
        for (byte in llllBytes) {
            lrc = lrc?.xor(byte)
        }
        for (byte in messageDataBytes) {
            lrc = lrc?.xor(byte)
        }
        lrc = lrc?.xor(etx ?: ETX)

        var position = 0
        raw[position] = stx ?: STX
        position++

        System.arraycopy(llllBytes, 0, raw, position, llllBytes.size)
        position += llllBytes.size

        System.arraycopy(messageDataBytes, 0, raw, position, messageDataBytes.size)
        position += messageDataBytes.size

        raw[position] = etx ?: ETX
        position++

        raw[position] = lrc ?: 0x00

        return raw
    }
}