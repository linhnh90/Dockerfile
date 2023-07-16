package com.styl.pa.modules.peripheralsManager.printerManager

import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import com.styl.pa.entities.GeneralException
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.peripheralsManager.peripheralsService.Peripheral
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.printer.customPrinterService.HandlePrintStatus
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType
import com.styl.pa.modules.printer.customPrinterService.PrinterService
import com.styl.pa.modules.printer.tx80PrinterService.UsbPrinterService
import com.styl.pa.utils.LogManager
import com.tx.printlib.Const
import it.custom.printer.api.android.PrinterFont

/**
 * Created by NgaTran on 9/21/2020.
 */
class PrinterManager : IPrinterManager {

    companion object {
        private val TAG = PrinterManager::class.java.simpleName
    }

    private var peripheralsPresenter: IMainContract.IPeripheralPresenter? = null

    constructor(peripheralsPresenter: IMainContract.IPeripheralPresenter?) {
        this.peripheralsPresenter = peripheralsPresenter
    }

    private val printerFontConfig = object : IPrinterFontConfig {
        override fun getMaxCharacters(): Int {
            if (false == isCustomPrinter) {
                return Peripheral.TX80_PRINTER_MAX_CHARACTERS
            }

            return Peripheral.CUSTOM_PRINTER_MAX_CHARACTERS
        }

        override fun getPrinterPageWidth(): Int {
            if (false == isCustomPrinter) {
                return Peripheral.TX80_PRINTER_PAPER_WIDTH
            }

            return Peripheral.CUSTOM_PRINTER_PAPER_WIDTH
        }

        override fun setAlignCenter(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_ALIGN_CENTER
            }

            return PrinterFont.FONT_JUSTIFICATION_CENTER
        }

        override fun setAlignLeft(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_ALIGN_LEFT
            }

            return PrinterFont.FONT_JUSTIFICATION_LEFT
        }

        override fun setAlignRight(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_ALIGN_RIGHT
            }

            return PrinterFont.FONT_JUSTIFICATION_RIGHT
        }

        override fun setSize1X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_1X
            }

            return PrinterFont.FONT_SIZE_X1
        }

        override fun setSize2X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_2X
            }

            return PrinterFont.FONT_SIZE_X2
        }

        override fun setSize3X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_3X
            }

            return PrinterFont.FONT_SIZE_X3
        }

        override fun setSize4X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_4X
            }

            return PrinterFont.FONT_SIZE_X4
        }

        override fun setSize5X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_5X
            }

            return PrinterFont.FONT_SIZE_X5
        }

        override fun setSize6X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_6X
            }

            return PrinterFont.FONT_SIZE_X6
        }

        override fun setSize7X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_7X
            }

            return PrinterFont.FONT_SIZE_X7
        }

        override fun setSize8X(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_SIZE_8X
            }

            return PrinterFont.FONT_SIZE_X8
        }

        override fun setDefaultFont(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_FONT_A
            }

            return PrinterFont.FONT_CS_DEFAULT
        }

        override fun setCustomFont1(): Int {
            if (false == isCustomPrinter) {
                return Const.TX_FONT_B
            }

            return PrinterFont.FONT_CS_RUSSIAN
        }
    }

    override fun getPrinterFontConfig(): IPrinterFontConfig? {
        return printerFontConfig
    }

    private var isCustomPrinter: Boolean? = null
    private var printerService: PrinterService? = null
    private var usbPrinterService: UsbPrinterService? = null

    var isPrinterConnected = false
    private fun usbPrinterServiceListener(peripheralsInfo: PeripheralsInfo) =
            object : UsbPrinterService.PrinterConnectionStatusListener {
                override fun printerConnectionStatus(result: GeneralException) {
                    isPrinterConnected = PrinterErrorType.VALID_CODE == result.code

                    peripheralsPresenter?.initPrinterManager(peripheralsInfo, result)
                }
            }

    override fun initPrinterService(peripheralsInfo: PeripheralsInfo) {
        if ((Peripheral.PRINTER_CUSTOM_VID == peripheralsInfo.vid &&
                Peripheral.PRINTER_CUSTOM_PID_1 == peripheralsInfo.pid) ||
                (Peripheral.PRINTER_CUSTOM_VID == peripheralsInfo.vid &&
                        Peripheral.PRINTER_CUSTOM_PID_2 == peripheralsInfo.pid)) {
            isCustomPrinter = true
            if (printerService == null) {
                printerService = PrinterService()
                val result = printerService?.appearAllUsb(null, peripheralsPresenter?.getContext()) as GeneralException
                peripheralsPresenter?.initPrinterManager(peripheralsInfo, result)
            }
        } else if (Peripheral.PRINTER_TX80_VID == peripheralsInfo.vid &&
                Peripheral.PRINTER_TX80_PID == peripheralsInfo.pid) {
            isCustomPrinter = false
            if (usbPrinterService == null) {
                usbPrinterService = UsbPrinterService.newInstance(peripheralsPresenter?.getContext(),
                        (peripheralsPresenter?.getContext() as? MainActivity), usbPrinterServiceListener(peripheralsInfo))
            }
            usbPrinterService?.connectPrinter()
        }
    }

    override fun initPrinterService(usbDevice: UsbDevice) {
        LogManager.d(TAG, "initPrinterService")
    }

    override fun setHandlePrinterStatusEvent(event: HandlePrintStatus) {
        printerService?.setHandlePrintStatus(event)
    }

    override fun printText(text: String, printerFont: Int?, fontSize: Int?,
                           isBold: Boolean, isItalic: Boolean,
                           isUnderline: Boolean): GeneralException? {
        var result: GeneralException? = null
        if (true == isCustomPrinter) {
            result = printerService?.printText(text, printerFont, fontSize, isBold,
                    isItalic, isUnderline)
        } else if (false == isCustomPrinter) {
            usbPrinterService?.printText(text, printerFont, fontSize, isBold,
                    isItalic, isUnderline)
            result = GeneralException(PrinterErrorType.VALID_CODE, "")
        }
        return result
    }

    override fun printImage(bitmap: Bitmap?) {
        if (true == isCustomPrinter) {
            printerService?.printImage(bitmap)
        } else if (false == isCustomPrinter) {
            usbPrinterService?.printImage(bitmap)
        }
    }

    override fun cutPage() {
        if (true == isCustomPrinter) {
            printerService?.cut()
        } else if (false == isCustomPrinter) {
            usbPrinterService?.endPrintReceipt()
        }
    }

    override fun printFeed() {
        if (true == isCustomPrinter) {
            printerService?.printFeed()
        } else if (false == isCustomPrinter) {
            usbPrinterService?.printFeed()
        }
    }

    override fun checkStatusPrinter(): GeneralException? {
        if (true == isCustomPrinter) {
            return printerService?.checkStatusPrinter()
        } else if (false == isCustomPrinter) {
            return usbPrinterService?.checkStatusPrinter()
        }
        return null
    }

    override fun checkStatusPaper(): GeneralException? {
        if (true == isCustomPrinter) {
            return printerService?.checkStatusPaper()
        } else if (false == isCustomPrinter) {
            return usbPrinterService?.checkStatusPrinter()
        }
        return null
    }

    override fun startCheckStatusWhenPrint() {
        printerService?.startCheckStatusWhenPrint()
    }

    override fun endCheckStatusWhenPrint() {
        printerService?.endCheckStatusWhenPrint()
    }

    override fun disconnectPrinter() {
        printerService?.closeDevice()
        printerService = null
        usbPrinterService?.disconnectPrinter()
        usbPrinterService = null
    }
}