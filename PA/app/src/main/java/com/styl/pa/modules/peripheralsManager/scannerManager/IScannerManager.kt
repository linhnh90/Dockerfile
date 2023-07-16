package com.styl.pa.modules.peripheralsManager.scannerManager

import android.hardware.usb.UsbDevice
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener

/**
 * Created by NgaTran on 9/15/2020.
 */
interface IScannerManager {
    fun initScannerService(peripheralsInfo: PeripheralsInfo)
    fun initScannerService(usbDevice: UsbDevice)
    fun pullTrigger(isPull: Boolean)
    fun setConfigEvent(listener: DcssdkListener.DcssdkConfig)
    fun detachScannerUsb()
    fun disconnectScanner()
}