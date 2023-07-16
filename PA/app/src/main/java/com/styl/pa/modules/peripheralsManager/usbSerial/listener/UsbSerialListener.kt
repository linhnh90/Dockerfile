package com.styl.pa.modules.peripheralsManager.usbSerial.listener

import android.hardware.usb.UsbDevice

interface UsbSerialListener {

    fun stopScanDevices()

    fun scanUsbDevices(scanUsbDeviceListener: ScanUsbDeviceListener)

    fun connectDevice(device: UsbDevice, listener: UsbDeviceConnectListener)

    fun writeData(data: ByteArray, command: Byte)

}