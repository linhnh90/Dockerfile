package com.styl.pa.modules.peripheralsManager.uart.command

open class BaseCommand {
    companion object {
        val ACK = 0xA0.toByte()
        val NACK = 0xA1.toByte()
        val POLL = 0xA2.toByte()

        val SERVICE_RPT = 0xE0.toByte()
        val ACTION_RPT = 0xE1.toByte()
        val INFO_RPT = 0xE2.toByte()
        val STATUS_RPT = 0xE3.toByte()
        val BUTTON_RPT = 0xE4.toByte()
        val VENDOUT_RPT = 0xE5.toByte()
        val GET_CONFDATA = 0xE6.toByte()
        val GET_UPDATA = 0xE7.toByte()
        val COST_RPT = 0xEC.toByte()

        val CONFIG_IND = 0xF0.toByte()
        val CONTROL_IND = 0xF1.toByte()
        val GET_INFO = 0xF2.toByte()
        val GET_STATUS = 0xF3.toByte()
        val VENDOUT_IND = 0xF4.toByte()
        val UPDATA_DAT = 0xF5.toByte()
        val COST_IND = 0xFB.toByte()

    }

    var stx: Byte = 0x02
    var lenght: ByteArray? = null
    var command: Byte = 0
    var serialNumber: Byte = 0
    var data: ByteArray? = null
    var fcc: ByteArray? = ByteArray(2) { 0x00 }
}