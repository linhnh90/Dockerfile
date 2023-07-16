package com.styl.pa.entities.wirecard

/**
 * Created by trangpham on 9/3/2018
 */
class WireCardMessageData(
    var header: String? = DEFAULT_HEADER,
    var responseCode: String? = DEFAULT_RESPONSE_CODE,
    var messageTypeIndicator: String?,
    var fieldSeparator: Byte? = FIELD_SEPARATOR,
    var fields: List<WireCardField>?
) {

    companion object {

        const val DEFAULT_HEADER: String = "60000000001120"
        const val DEFAULT_RESPONSE_CODE: String = "00"
        const val FIELD_SEPARATOR: Byte = 0x1c

        const val HEADER_LENGTH = 14
        const val RESPONSE_CODE_LENGTH = 2
        const val MESSAGE_TYPE_LENGTH = 1

        const val MESSAGE_TYPE_TXN_REQUEST: String = "0"
        const val MESSAGE_TYPE_TXN_INQUIRY: String = "4"
        const val MESSAGE_TYPE_SETTLEMENT_REQUEST: String = "8"
        const val MESSAGE_TYPE_SETTLEMENT_SUMMARY: String = "9"

        fun fromBytes(bytes: ByteArray?): WireCardMessageData? {
            if (bytes != null) {
                if (bytes.size < HEADER_LENGTH + RESPONSE_CODE_LENGTH + MESSAGE_TYPE_LENGTH + 1) {
                    return null
                }

                var position = 0
                val headerBuilder = StringBuilder()
                val headerBytes = bytes.copyOfRange(position, position + HEADER_LENGTH)
                for (byte in headerBytes) {
                    headerBuilder.append(byte.toString(16))
                }
                val header = headerBuilder.toString()
                position += HEADER_LENGTH

                val responseCodeBytes = bytes.copyOfRange(position, position + RESPONSE_CODE_LENGTH)

                val responseCode = String(responseCodeBytes)
                position += RESPONSE_CODE_LENGTH

                val messageTypeIndicatorBuilder = StringBuilder()
                val messageTypeIndicatorBytes =
                    bytes.copyOfRange(position, position + MESSAGE_TYPE_LENGTH)
                for (byte in messageTypeIndicatorBytes) {
                    messageTypeIndicatorBuilder.append(byte.toString(16))
                }
                val messageTypeIndicator = messageTypeIndicatorBuilder.toString()
                position += MESSAGE_TYPE_LENGTH

                val fieldSeparator = bytes.get(position)
                position++

                val fields = WireCardField.toList(bytes.copyOfRange(position, bytes.size))

                return WireCardMessageData(
                    header,
                    responseCode,
                    messageTypeIndicator,
                    fieldSeparator,
                    fields
                )
            }
            return null
        }
    }

    fun toBytes(): ByteArray? {
        if (header.isNullOrEmpty() || header!!.length.compareTo(HEADER_LENGTH) != 0 ||
            responseCode.isNullOrEmpty() || responseCode!!.length.compareTo(RESPONSE_CODE_LENGTH) != 0 ||
            messageTypeIndicator.isNullOrEmpty() || messageTypeIndicator!!.length.compareTo(
                MESSAGE_TYPE_LENGTH
            ) != 0 ||
            fieldSeparator == null
        ) {
            return null
        }

        val headerBytes = ByteArray(HEADER_LENGTH)
        val headerChars = header!!.toCharArray()
        for (i in headerChars.indices) {
            headerBytes[i] = headerChars[i].code.toByte()
        }

        val responseCodeBytes = ByteArray(RESPONSE_CODE_LENGTH)
        val responseCodeChars = responseCode!!.toCharArray()
        for (i in responseCodeChars.indices) {
            responseCodeBytes[i] = responseCodeChars[i].code.toByte()
        }

        val messageTypeIndicatorBytes = ByteArray(MESSAGE_TYPE_LENGTH)
        val messageTypeIndicatorChars = messageTypeIndicator!!.toCharArray()
        for (i in messageTypeIndicatorChars.indices) {
            messageTypeIndicatorBytes[i] = messageTypeIndicatorChars[i].code.toByte()
        }

        var total = HEADER_LENGTH + RESPONSE_CODE_LENGTH + MESSAGE_TYPE_LENGTH + 1

        if (fields != null) {
            for (field in fields!!) {
                total += field.toBytes().let { bytes -> bytes?.size ?: 0 }
            }
        }

        val raw = ByteArray(total)

        var position = 0
        System.arraycopy(headerBytes, 0, raw, position, headerBytes.size)
        position += headerBytes.size

        System.arraycopy(responseCodeBytes, 0, raw, position, responseCodeBytes.size)
        position += responseCodeBytes.size

        System.arraycopy(
            messageTypeIndicatorBytes,
            0,
            raw,
            position,
            messageTypeIndicatorBytes.size
        )
        position += messageTypeIndicatorBytes.size

        raw[position] = fieldSeparator!!
        position++

        if (fields != null) {
            for (field in fields!!) {
                val bytes = field.toBytes()
                if (bytes != null) {
                    System.arraycopy(bytes, 0, raw, position, bytes.size)
                    position += bytes.size
                }
            }
        }

        return raw
    }
}