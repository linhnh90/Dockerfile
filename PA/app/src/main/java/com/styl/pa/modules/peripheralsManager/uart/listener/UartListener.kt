package com.styl.pa.modules.peripheralsManager.uart.listener

interface UartListener {
    fun onOpenPort(error: String?)

    fun onReceiveData(data: ByteArray)
}