package com.styl.pa.modules.peripheralsManager.uart.listener

import com.styl.pa.modules.peripheralsManager.uart.command.CommandResponse

interface PortListener {
    fun onPortReady()

    fun onReceiveData(data: CommandResponse)
}