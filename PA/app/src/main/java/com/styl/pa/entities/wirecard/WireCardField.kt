package com.styl.pa.entities.wirecard

import com.styl.pa.utils.BCDUtils
import com.styl.pa.utils.HexBytesUtils

/**
 * Created by trangpham on 9/3/2018
 */
class WireCardField(
        var fieldType: String?,
        var length: String?,
        var data: String?,
        var fieldSeparator: Byte? = WireCardMessageData.FIELD_SEPARATOR
) {

    companion object {

        const val FIELD_TYPE_LENGTH = 2
        const val LLLL_LENGTH = 2

        const val FIELD_PRODUCT_CODE = "33"
        const val FIELD_PAYMENT_TYPE_CODE = "34"
        const val FIELD_TXN_AMOUNT_REQUEST = "40"

        const val FIELD_APPROVAL_CODE = "01"
        const val FIELD_CARD_NUMBER = "30"
        const val FIELD_EXPIRY_DATE = "31"
        const val FIELD_ISSUER_NAME = "32"
        const val FIELD_MERCHANT_ID = "36"
        const val FIELD_TERMINAL_ID = "37"
        const val FIELD_REFERENCE_NUMBER = "38"
        const val FIELD_TXN_AMOUNT = "41"
        const val FIELD_DISCOUNT_AMOUNT = "42"
        const val FIELD_CARD_BALANCE = "43"
        const val FIELD_ERROR = "50"
        const val FIELD_PAYMENT_TYPE = "52"
        const val FIELD_DATETIME = "66"
        const val FIELD_CARD_TYPE = "67"
        const val FIELD_ENTRY_MODE = "68"
        const val FIELD_PAN_NUMBER = "69"
        const val FIELD_MID = "70"
        const val FIELD_TID = "71"
        const val FIELD_INVOICE_NO = "72"
        const val FIELD_RRN = "73"
        const val FIELD_BATCH_NO = "74"
        const val FIELD_STAN_NO = "75"
        const val FIELD_APPROVAL_CODE2 = "76"
        const val FIELD_TOTAL_AMOUNT = "80"
        const val FIELD_AID = "81"
        const val FIELD_CARD_LABEL = "82"
        const val FIELD_TXN_CERT_TC = "83"
        const val FIELD_TVR = "84"
        const val FIELD_TSI = "85"
        const val FIELD_NETS_ACCOUNT_TYPE = "86"
        const val FIELD_NETS_DEBIT_BANK = "87"
        const val FIELD_CEPAS_PREVIOUS_BALANCE = "88"
        const val FIELD_CEPAS_CURRENT_BALANCE = "89"
        const val FIELD_SIGNATURE_REQUIRED = "90"
        const val FIELD_TXN_RECEIPT = "92"

        const val TXN_REQUEST_READ_CARD_ERROR = "51"
        const val TXN_REQUEST_BALANCE_NO_VALUE_ERROR = "52"
        const val TXN_REQUEST_INVALID_CARD_ERROR = "53"
        const val TXN_REQUEST_INSUFFICIENT_BALANCE_ERROR = "54"
        const val TXN_REQUEST_TXN_CANCEL_ERROR = "55"
        const val TXN_REQUEST_READER_TIMEOUT_ERROR = "56"
        const val TXN_REQUEST_READER_NO_READY_ERROR = "57"
        const val TXN_REQUEST_TERMINAL_NO_READY_ERROR = "58"
        const val TXN_REQUEST_GENERAL_ERROR = "91"

        fun fromBytes(bytes: ByteArray?): WireCardField? {
            if (bytes != null) {
                if (bytes.size < FIELD_TYPE_LENGTH + LLLL_LENGTH) {
                    return null
                }

                var position = 0
                val fieldTypeBuilder = StringBuilder()
                val fieldBytes = bytes.copyOfRange(position, position + FIELD_TYPE_LENGTH)
                for (byte in fieldBytes) {
                    fieldTypeBuilder.append(byte.toString(16))
                }
                val fieldType = fieldTypeBuilder.toString()
                position += FIELD_TYPE_LENGTH

                val lengthBuilder = StringBuilder()
                val lengthBytes = bytes.copyOfRange(position, position + LLLL_LENGTH)
                for (byte in lengthBytes) {
                    lengthBuilder.append(byte.toString(16))
                }
                val length = lengthBuilder.toString()
                position += LLLL_LENGTH

                if (bytes.size != FIELD_TYPE_LENGTH + LLLL_LENGTH + length.toInt() + 1) {
                    return null
                }

                val data: String
                val dataBytes = bytes.copyOfRange(position, position + length.toInt())
                if (FIELD_PAYMENT_TYPE.equals(fieldType) || FIELD_SIGNATURE_REQUIRED.equals(fieldType)) {
                    data = HexBytesUtils.bytesToHex(dataBytes)
                } else {
                    val dataBuilder = StringBuilder()
                    for (byte in dataBytes) {
                        dataBuilder.append(byte.toString(16))
                    }
                    data = dataBuilder.toString()
                }
                position += length.toInt()

                val fieldSeparator = bytes.get(position)

                return WireCardField(fieldType, length, data, fieldSeparator)
            }
            return null
        }

        fun toList(bytes: ByteArray?): List<WireCardField>? {
            if (bytes == null) {
                return null
            }

            val wireCardFields: MutableList<WireCardField>? = ArrayList()

            var position = 0
            do {
                if (bytes.size < position + FIELD_TYPE_LENGTH + LLLL_LENGTH) {
                    break
                }

                val fieldBytes = bytes.copyOfRange(position, position + FIELD_TYPE_LENGTH)
                val fieldType = String(fieldBytes)
                position += FIELD_TYPE_LENGTH

                val lengthBuilder = StringBuilder()
                val lengthBytes = bytes.copyOfRange(position, position + LLLL_LENGTH)
                for (byte in lengthBytes) {
                    lengthBuilder.append(byte.toString(16))
                }
                val length = lengthBuilder.toString()
                position += LLLL_LENGTH

                val totalLength = position + length.toInt() + 1
                if (bytes.size < totalLength || bytes[totalLength - 1] != WireCardMessageData.FIELD_SEPARATOR) {
                    break
                }

                var dataBytes = bytes.copyOfRange(position, position + length.toInt())

                val index = dataBytes.indexOf(0x00)
                if (index >= 0) {
                    dataBytes = bytes.copyOfRange(position, position + index)
                }
                val data: String
                if (FIELD_PAYMENT_TYPE.equals(fieldType) || FIELD_SIGNATURE_REQUIRED.equals(fieldType)) {
                    data = HexBytesUtils.bytesToHex(dataBytes)
                } else {
                    data = String(dataBytes)
                }
                position += length.toInt()

                val fieldSeparator = bytes.get(position)
                position++

                wireCardFields?.add(WireCardField(fieldType, length, data, fieldSeparator))
            } while (position < bytes.size)

            return wireCardFields
        }
    }

    fun toBytes(): ByteArray? {
        if (fieldType.isNullOrEmpty() || fieldType!!.length.compareTo(FIELD_TYPE_LENGTH) != 0 ||
                length.isNullOrEmpty() || length!!.length.compareTo(LLLL_LENGTH * 2) != 0 ||
                data.isNullOrEmpty() || data?.length?.compareTo(length!!.toInt()) != 0 ||
                fieldSeparator == null
        ) {
            return null
        }

        val fieldTypeBytes = ByteArray(FIELD_TYPE_LENGTH)
        val fieldTypeChars = fieldType!!.toCharArray()
        for (i in fieldTypeChars.indices) {
            fieldTypeBytes[i] = fieldTypeChars[i].code.toByte()
        }

        val llllBytes = ByteArray(LLLL_LENGTH)
        val temp = BCDUtils.stringToBcd(length!!)
        System.arraycopy(temp, 0, llllBytes, LLLL_LENGTH - temp.size, temp.size)

        val dataBytes = ByteArray(data!!.length)
        val dataChars = data!!.toCharArray()
        for (i in dataChars.indices) {
            dataBytes[i] = dataChars[i].code.toByte()
        }

        val raw = ByteArray(FIELD_TYPE_LENGTH + LLLL_LENGTH + (data?.length ?: 0) + 1)
        var position = 0
        System.arraycopy(fieldTypeBytes, 0, raw, position, fieldTypeBytes.size)
        position += fieldTypeBytes.size
        System.arraycopy(llllBytes, 0, raw, position, llllBytes.size)
        position += llllBytes.size
        System.arraycopy(dataBytes, 0, raw, position, dataBytes.size)
        position += dataBytes.size
        raw[position] = fieldSeparator ?: WireCardMessageData.FIELD_SEPARATOR

        return raw
    }
}