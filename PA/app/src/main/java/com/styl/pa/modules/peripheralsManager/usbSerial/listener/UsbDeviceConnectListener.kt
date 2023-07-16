package com.styl.pa.modules.peripheralsManager.usbSerial.listener

interface UsbDeviceConnectListener {

    fun onUsbDeviceConnect(error: String?)

    fun onUsbDeviceDisconnect()

    fun onUsbReadCallback(data: ByteArray?)

    fun onUsbReadDataFail(error: String?)

    fun onUsbSendDataSuccess(data: ByteArray?)
}