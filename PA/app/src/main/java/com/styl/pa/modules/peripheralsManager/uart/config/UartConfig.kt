package com.styl.pa.modules.peripheralsManager.uart.config

class UartConfig {
    companion object {
        val BAUD_RATE = 9600
        val BAUD_RATE_READER = 57600
        val DATA_BITS = 8
        val STOP_BITS = 1
        val PARITY = 'n'

        val NON_BLOCK = 0
        val BLOCK = 1

        val TIME_OUT_READ = 50
        val DELAY_TIME_RECEIVER: Long = 3000
    }
}