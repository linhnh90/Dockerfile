package com.styl.pa.modules.terminal.upt1000fterminal

class ConnConfig {
    companion object {
        const val VM_RS232_PORT_NAME = "ttymxc1"
        const val TERMINAL_RS232_PORT_NAME = "ttymxc2"
        const val READER_RS232_PORT_NAME = "ttymxc3"
        const val FTDI_PID = 24577
        const val FTDI_VID = 1027
        val VM_USB_PID = FTDI_PID
        val VM_USB_VID = FTDI_VID
    }

    enum class ConnectionType(val value: Int) {
        RS232(0), USB(1)
    }
}