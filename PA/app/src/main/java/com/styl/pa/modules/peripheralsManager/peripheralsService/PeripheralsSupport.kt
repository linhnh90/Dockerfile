package com.styl.pa.modules.peripheralsManager.peripheralsService

import android.hardware.usb.UsbDevice
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by NgaTran on 9/15/2020.
 */
class PeripheralsSupport {
    private var peripheralsSupportedInfo: ArrayList<PeripheralsInfo>

    constructor() {
        peripheralsSupportedInfo = ArrayList()
        peripheralsSupportedInfo.add(
                PeripheralsInfo(
                        Peripheral.SCANNER_TYPE,
                        Peripheral.SCANNER_CH34_VID,
                        Peripheral.SCANNER_CH34_PID,
                        Peripheral.SCANNER_CH34_MANUFACTURER_NAME,
                        Peripheral.SCANNER_CH34_PRODUCT_NAME
                )
        )
        peripheralsSupportedInfo.add(
                PeripheralsInfo(
                        Peripheral.SCANNER_TYPE,
                        Peripheral.SCANNER_ZEBRA_VID,
                        Peripheral.SCANNER_ZEBRA_PID,
                        Peripheral.SCANNER_ZEBRA_MANUFACTURER_NAME,
                        Peripheral.SCANNER_ZEBRA_PRODUCT_NAME
                )
        )
        peripheralsSupportedInfo.add(
                PeripheralsInfo(
                        Peripheral.PRINTER_TYPE,
                        Peripheral.PRINTER_TX80_VID,
                        Peripheral.PRINTER_TX80_PID,
                        Peripheral.PRINTER_TX80_MANUFACTURER_NAME,
                        Peripheral.PRINTER_TX80_PRODUCT_NAME
                )
        )
        peripheralsSupportedInfo.add(
                PeripheralsInfo(
                        Peripheral.PRINTER_TYPE,
                        Peripheral.PRINTER_CUSTOM_VID,
                        Peripheral.PRINTER_CUSTOM_PID_1,
                        Peripheral.PRINTER_CUSTOM_MANUFACTURER_NAME,
                        Peripheral.PRINTER_CUSTOM_PRODUCT_NAME
                )
        )
        peripheralsSupportedInfo.add(
                PeripheralsInfo(
                        Peripheral.PRINTER_TYPE,
                        Peripheral.PRINTER_CUSTOM_VID,
                        Peripheral.PRINTER_CUSTOM_PID_2,
                        Peripheral.PRINTER_CUSTOM_MANUFACTURER_NAME,
                        Peripheral.PRINTER_CUSTOM_PRODUCT_NAME
                )
        )
//        peripheralsSupportedInfo.add(
//                PeripheralsInfo(
//                        Peripheral.TERMINAL_TYPE,
//                        Peripheral.TERMINAL_FTDI_VID,
//                        Peripheral.TERMINAL_FTDI_PID,
//                        Peripheral.TERMINAL_FTDI_MANUFACTURER_NAME,
//                        Peripheral.TERMINAL_FTDI_PRODUCT_NAME
//                )
//        )

        peripheralsSupportedInfo.add(
                PeripheralsInfo(
                        Peripheral.TERMINAL_TYPE,
                        Peripheral.TERMINAL_FT232R_VID,
                        Peripheral.TERMINAL_FT232R_PID,
                        Peripheral.TERMINAL_FT232R_MANUFACTURER_NAME,
                        Peripheral.TERMINAL_FT232R_PRODUCT_NAME
                )
        )
    }

    fun addPeripheralsSupportedInfo(type: Int, peripherals: ArrayList<PeripheralsInfo>) {
        peripherals.forEach {
            peripheralsSupportedInfo.add(PeripheralsInfo(type, it.vid, it.pid, it.manufacturerName, it.productName))
        }
    }

    fun getPeripheralsSupportedInfo(): ArrayList<PeripheralsInfo> {
        return peripheralsSupportedInfo
    }

    fun isScanner(usbDevice: UsbDevice): PeripheralsInfo? {
        val scannerInfoList = peripheralsSupportedInfo.filter {
            Peripheral.SCANNER_TYPE == it.type
        } as? ArrayList<PeripheralsInfo>

        if (scannerInfoList != null && scannerInfoList.size > 0) {
            return isSupportedPeripheral(usbDevice, scannerInfoList)
        }

        return null
    }

    fun isPrinter(usbDevice: UsbDevice): PeripheralsInfo? {
        val printerInfoList = peripheralsSupportedInfo.filter {
            Peripheral.PRINTER_TYPE == it.type
        } as? ArrayList<PeripheralsInfo>

        if (printerInfoList != null && printerInfoList.size > 0) {
            return isSupportedPeripheral(usbDevice, printerInfoList)
        }

        return null
    }

    fun isTerminal(usbDevice: UsbDevice): PeripheralsInfo? {
        val terminalInfoList = peripheralsSupportedInfo.filter {
            Peripheral.TERMINAL_TYPE == it.type
        } as? ArrayList<PeripheralsInfo>

        if (terminalInfoList != null && terminalInfoList.size > 0) {
            return isSupportedPeripheral(usbDevice, terminalInfoList)
        }

        return null
    }

    fun isSupportedPeripheral(
            usbDevice: UsbDevice,
            deviceList: ArrayList<PeripheralsInfo>
    ): PeripheralsInfo? {
        for (item in deviceList) {
            if (item.vid == usbDevice.vendorId && item.pid == usbDevice.productId) {
                return item
            }
        }
        return null
    }
}