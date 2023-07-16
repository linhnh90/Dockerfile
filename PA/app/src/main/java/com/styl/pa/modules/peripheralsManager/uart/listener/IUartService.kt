package com.styl.pa.modules.peripheralsManager.uart.listener

interface IUartService {
    fun setListener(listener: UartListener)

    fun openPort(uartName: String, baudrate: Int)

    fun closePort()

    fun readData(timeout: Int): ByteArray

    fun writeData(buf: ByteArray): Int

    fun setReadTimeout(timeout: Int)

    fun getComPorts(pid: String, vid: String): Array<String>

    fun getComPort(pid: String, vid: String): String
}