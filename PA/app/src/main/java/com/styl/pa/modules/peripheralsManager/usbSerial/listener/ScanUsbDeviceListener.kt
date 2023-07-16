package com.styl.pa.modules.peripheralsManager.usbSerial.listener

import android.hardware.usb.UsbDevice

interface ScanUsbDeviceListener {

    fun onReceiveUsbDevice(device: UsbDevice)

}