package com.styl.pa.modules.peripheralsManager.usbSerial.config

class UsbConfig {
    companion object {
        val ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        val ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"
        val ACTION_USB_PERMISSION = "com.example.stylvn.demovending.USB_PERMISSION"
        val BAUD_RATE = 9600

        val VID = 1367
        val PID = 8200

//        val VID = 1027
//        val PID = 24577
//        const val ATEN_RS232_TO_USB_PID = 8200
//        const val ATEN_RS232_TO_USB_VID = 1367

        val DELAY_TIME_RECEIVER: Long = 5000
    }
}