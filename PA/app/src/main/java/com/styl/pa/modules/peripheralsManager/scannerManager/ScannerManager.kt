package com.styl.pa.modules.peripheralsManager.scannerManager

import android.hardware.usb.UsbDevice
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.peripheralsManager.peripheralsService.Peripheral
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.scanner.ch34ScannerService.UsbScannerService
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.modules.scanner.zebraScannerService.ScannerService
import com.styl.pa.utils.LogManager

/**
 * Created by NgaTran on 9/15/2020.
 */
class ScannerManager : IScannerManager {

    companion object {
        private val TAG = ScannerManager::class.java.simpleName
        var isZebra: Boolean? = null
    }

    private var peripheralsPresenter: IMainContract.IPeripheralPresenter? = null

    constructor(peripheralsPresenter: IMainContract.IPeripheralPresenter?) {
        this.peripheralsPresenter = peripheralsPresenter
    }
    private var scannerService: ScannerService? = null
    private var usbScannerService: UsbScannerService? = null

    override fun initScannerService(peripheralsInfo: PeripheralsInfo) {
        if (Peripheral.SCANNER_ZEBRA_VID == peripheralsInfo.vid &&
                Peripheral.SCANNER_ZEBRA_PID == peripheralsInfo.pid) {
            isZebra = true
            if (scannerService == null) {
                scannerService = ScannerService()
                scannerService?.initService(peripheralsPresenter?.getContext(), peripheralsPresenter?.getScannerCallback())
                peripheralsPresenter?.initScannerManager(peripheralsInfo)
            }
        } else if (Peripheral.SCANNER_CH34_VID == peripheralsInfo.vid &&
                Peripheral.SCANNER_CH34_PID == peripheralsInfo.pid) {
            isZebra = false
            if (usbScannerService == null) {
                usbScannerService = UsbScannerService(peripheralsPresenter?.getContext())
                usbScannerService?.setUsbScannerConnectionListener(peripheralsPresenter?.getScannerCallback())
                val isSupport = usbScannerService?.checkUsbScannerServiceSupport()
                if (true == isSupport) {
                    usbScannerService?.openScannerService()
                }
            }
        }
    }

    fun isZebraScanner(): Boolean {
        return isZebra ?: false
    }

    override fun initScannerService(usbDevice: UsbDevice) {
        LogManager.d(TAG, "initScannerService")
    }

    override fun pullTrigger(isPull: Boolean) {
        LogManager.i("Scanner pullTrigger $isPull")
        if (true == isZebra) {
            scannerService?.pullTrigger(isPull)
        } else if (false == isZebra) {
            if (isPull) {
                val isSuccess = usbScannerService?.pullTrigger() ?: false
                LogManager.i("Trigger scanner $isSuccess")
            } else {
                usbScannerService?.unTriggerScanner()
            }
        }
    }

    override fun setConfigEvent(listener: DcssdkListener.DcssdkConfig) {
        if (true == isZebra) {
            scannerService?.setDcssdkConfigEvent(listener)
        } else if (false == isZebra) {
            usbScannerService?.setUsbScannerResultListener(listener)
        }
    }

    override fun detachScannerUsb() {
        if (false == isZebra) {
            usbScannerService?.unTriggerScanner()
            usbScannerService?.destroyUSBScannerService()
            usbScannerService = null
        }
    }

    override fun disconnectScanner() {
        scannerService?.disconnectScanner()
        usbScannerService?.destroyUSBScannerService()
        usbScannerService = null
    }

}