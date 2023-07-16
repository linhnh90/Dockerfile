package com.styl.pa.modules.printer.customPrinterService

import android.content.Context
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.entities.GeneralException
import com.styl.pa.modules.peripheralsManager.peripheralsService.Peripheral
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.ERROR_CODE
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.ERROR_NO_COVER
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.ERROR_NO_HEAD
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.ERROR_NO_PAPER
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.ERROR_PAPER_JAM
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.LOST_CONNECT_PRINTER
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.LOW_PAPER
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.VALID_CODE
import com.styl.pa.utils.GeneralUtils
import it.custom.printer.api.android.CustomAndroidAPI
import it.custom.printer.api.android.CustomException
import it.custom.printer.api.android.CustomException.ERR_PRINTERCOMMUNICATIONERROR
import it.custom.printer.api.android.CustomException.ERR_PRINTERNOTCONNECTED
import it.custom.printer.api.android.CustomPrinter
import it.custom.printer.api.android.PrinterFont
import it.custom.printer.api.android.PrinterFont.FONT_TYPE_B

/**
 * Created by Ngatran on 09/17/2018.
 */
class PrinterService {

    companion object {
        const val ALIGN_LEFT_PX = 30
    }

    private var TIMER_MAX = 30

    var GETSTATUS_TIME = 500

    val lock = BuildConfig.APPLICATION_ID

    private var context: Context? = null
    private var printer: CustomPrinter? = null
    private var usbDeviceList: ArrayList<UsbDevice> = ArrayList()
    private var handlePrintStatus: HandlePrintStatus? = null

    private var hGetStatus = Handler()

    private var timerGetResult = 0

    private var PrinterDevice: CustomPrinter?
        get() {
            return printer
        }
        set(value) {
            this.printer = value
        }

    private var USBDeviceList: ArrayList<UsbDevice>
        get() {
            return usbDeviceList
        }
        set(value) {
            this.usbDeviceList = value
        }

    fun setHandlePrintStatus(event: HandlePrintStatus) {
        this.handlePrintStatus = event
    }

    fun appearAllUsb(savedInstanceState: Bundle?, context: Context?): GeneralException {
        this.context = context
        if (savedInstanceState == null) {
            try {
                //Get the list of devices
                usbDeviceList = CustomAndroidAPI.EnumUsbDevices(context).toCollection(ArrayList())
            } catch (e: CustomException) {
                Log.i("Error", "Get usb printer list error...")
            } catch (e: Exception) {
                Log.i("Error", "Enum devices error...")
            }

        }
        if (usbDeviceList.size > 0) {
            return connectDevice(context)
        } else {
            return GeneralException(ERROR_CODE, context?.getString(R.string.cant_connect_printer))
        }
    }

    fun closeDevice(): Boolean {
        try {
            if (printer != null) {
                printer!!.close()
                printer = null
            }

            usbDeviceList.clear()
        } catch (e: CustomException) {
            Log.i("Error", "Close usb printer error...")
            return false
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun openDevice(usbDevice: UsbDevice, context: Context?): GeneralException {

        try {
            printer = CustomAndroidAPI().getPrinterDriverUSB(usbDeviceList.get(0), context)
        } catch (e: CustomException) {
            Log.i("Error", "Get printer drive USB error...")
            return GeneralException(ERROR_CODE, if (e != null && TextUtils.isEmpty(e.message)) e.message!! else context?.getString(R.string.cant_connect_printer))
        } catch (e: Exception) {
            Log.i("Error", "Open Print Error...")
            return GeneralException(ERROR_CODE, if (e != null && TextUtils.isEmpty(e.message)) e.message!! else context?.getString(R.string.cant_connect_printer))
        }
        return GeneralException(VALID_CODE, "")
    }

    fun connectDevice(context: Context?): GeneralException {
        if (usbDeviceList.size > 0) {
            var connectTimer = 0
            var resultPrint = false
            while (resultPrint == false && connectTimer < TIMER_MAX) {
                resultPrint = openDevice(usbDeviceList.get(0), context).code != ERROR_CODE
                connectTimer++
            }

            if (resultPrint == true && printer != null) {
                return GeneralException(VALID_CODE, "")
            } else {
                Log.i("Error...", "The printer don't print...")
                return GeneralException(ERROR_CODE, context?.getString(R.string.cant_connect_printer))
            }
        } else {
            return GeneralException(ERROR_CODE, context?.getString(R.string.cant_connect_printer))
        }
    }

    fun getPageWidth(): Int? {
        return printer?.pageWidth
    }

    fun createPrinterFont(fontSize: Int, isBold: Boolean, isItalic: Boolean, isUnderline: Boolean, justification: Int, font: Int): PrinterFont {
        val fntPrinterNormal = PrinterFont()
        fntPrinterNormal.charHeight = fontSize
        fntPrinterNormal.charWidth = fontSize
        fntPrinterNormal.emphasized = isBold
        fntPrinterNormal.italic = isItalic
        fntPrinterNormal.underline = isUnderline
        fntPrinterNormal.justification = justification
        fntPrinterNormal.internationalCharSet = font
        fntPrinterNormal.charFontType = FONT_TYPE_B

        return fntPrinterNormal
    }

    fun checkStatusPaper(): GeneralException {
        if (printer != null) {
            try {
                var printerStatus = printer!!.printerFullStatus
                if (printerStatus.stsNOHEAD)
                    return GeneralException(ERROR_NO_HEAD, PrinterErrorType.errorHash.get(ERROR_NO_HEAD))
                else if (printerStatus.stsNOCOVER)
                    return GeneralException(ERROR_NO_COVER, PrinterErrorType.errorHash.get(ERROR_NO_COVER))
                else if (printerStatus.stsNOPAPER)
                    return GeneralException(ERROR_NO_PAPER, PrinterErrorType.errorHash.get(ERROR_NO_PAPER))
                else if (printerStatus.stsPAPERJAM)
                    return GeneralException(ERROR_PAPER_JAM, PrinterErrorType.errorHash.get(ERROR_PAPER_JAM))
                else if (printerStatus.stsNEARENDPAP)
                    return GeneralException(LOW_PAPER, PrinterErrorType.errorHash.get(ERROR_PAPER_JAM))
//                    else if (printerStatus.stsSPOOLING)
//                        return GeneralException(ERROR_PAPER_JAM, "")
            } catch (e: CustomException) {
                var errorMessage = if (!TextUtils.isEmpty(e.message)) e.message else context?.getString(R.string.error_check_status_paper)
                return GeneralException(e.GetErrorCode().toInt(), errorMessage)
            } catch (ex: Exception) {
                return GeneralException(ERROR_CODE, context?.getString(R.string.error_check_status_paper))
            }
        } else {
            return GeneralException(LOST_CONNECT_PRINTER, context?.getString(R.string.lost_connect_printer))
        }
        return GeneralException(VALID_CODE, "")
    }

    fun checkStatusPrinter(): GeneralException {
        var result = checkStatusPaper()

        if (result.code == ERR_PRINTERNOTCONNECTED || result.code == ERR_PRINTERCOMMUNICATIONERROR || result.code == LOST_CONNECT_PRINTER) {

            result = connectDevice(context)

            if (result.code != VALID_CODE) {
                return result
            }
        }

        return result
    }

    fun cut() {
        if (printer != null) {
            try {
                printer?.feed(3)
                printer?.cut(CustomPrinter.CUT_TOTAL)
            } catch (e: CustomException) {
                Log.i("Error", "Printer cut error")
                callEventWhenError(GeneralException(e.GetErrorCode().toInt(), e.message))
            } catch (e: Exception) {
                Log.i("Error", "Print Text Error...")
                callEventWhenError(GeneralException(ERROR_CODE, context?.getString(R.string.error_cut_paper)))
            }
        }
    }

    fun printText(text: String, printerFont: Int?, fontSize: Int?, isBold: Boolean,
                  isItalic: Boolean, isUnderline: Boolean): GeneralException {
        timerGetResult = 0

        if (printer != null) {
            var fntPrinterNormal = PrinterFont()

            try {
                fntPrinterNormal = createPrinterFont(fontSize
                        ?: PrinterFont.FONT_SIZE_X1, isBold, isItalic, isUnderline,
                        printerFont
                                ?: PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_CS_DEFAULT)
            } catch (e: CustomException) {
                Log.i("Error", "Error create printer font")
                callEventWhenError(GeneralException(e.GetErrorCode().toInt(), e.message))
            } catch (e: Exception) {
                Log.i("Error", "Set font properties error...")
                callEventWhenError(GeneralException(ERROR_CODE, context?.getString(R.string.error_generate_font)))
            }

            try {
                printer?.setWriteTimeout(GETSTATUS_TIME)
                if (usbDeviceList.size > 0) {
                    if (Peripheral.PRINTER_CUSTOM_PID_1 == usbDeviceList[0].productId) {
                        printer?.printTextLF(text, fntPrinterNormal)
                    } else {
                        printer?.printTextLF(text, ALIGN_LEFT_PX, -1, fntPrinterNormal)
                    }
                }
            } catch (e: CustomException) {
                Log.i("Error", "Print Text Error...")
                callEventWhenError(GeneralException(e.GetErrorCode().toInt(), e.message))
            } catch (e: Exception) {
                Log.i("Error", "Print Text Error...")
                callEventWhenError(GeneralException(ERROR_CODE, context?.getString(R.string.error_print_text)))
            }
        }

        return GeneralException(VALID_CODE, "")
    }

    fun printImage(bitmap: Bitmap?) {
        if (printer != null) {
            try {
                var b = bitmap
                if (bitmap?.width != null) {
                    b = GeneralUtils.getResizedBitmap(bitmap, (bitmap.width / 1.5).toInt(), bitmap.height / 4)
                }

                printer?.printImage(b, CustomPrinter.IMAGE_ALIGN_TO_CENTER, CustomPrinter.IMAGE_SCALE_NONE, 0)

            } catch (e: CustomException) {
                Log.i("Error", "Print image error")
                callEventWhenError(GeneralException(e.GetErrorCode().toInt(), e.message))
            } catch (e: Exception) {
                Log.i("Error", context?.getString(R.string.error_print_image))
                callEventWhenError(GeneralException(ERROR_CODE, context?.getString(R.string.error_print_image)))
            }
        }
    }

    fun printFeed() {
        printer?.feed(30)
    }

    fun startCheckStatusWhenPrint() {
        hGetStatus.post(getStatusRunnable)
    }

    fun endCheckStatusWhenPrint() {
        isError = true
        handlePrintStatus?.endCheckStatus()
    }

    private var isError = false

    private fun callEventWhenError(error: GeneralException) {
        isError = true
        handlePrintStatus?.getStatus(error)
        clearReadBuffer()
    }

    private val getStatusRunnable = object : Runnable {
        override fun run() {

            //If the device is open
            if (printer != null) {

                var result = checkStatusPaper()
                Log.e("printer", "checkStatusPaper")

                if (result.code != VALID_CODE && result.code != LOW_PAPER) {
                    Log.e("printer", "checkStatusPaper   ERROR" + result.code + " : " + PrinterErrorType.errorHash.get(result.code))
                    callEventWhenError(result)
                    return
                }
            }

            Log.e("printer", "Run again")
//            run again in GETSTATUS_TIME msec
            if (isError == false) {
                Log.e("printer", "Run again inside")
                timerGetResult++
                if (timerGetResult < TIMER_MAX) {
                    hGetStatus.postDelayed(this, GETSTATUS_TIME.toLong())
                } else {
                    endCheckStatusWhenPrint()
                }
            }
        }
    }

    private fun clearReadBuffer() {
        var buffer = ByteArray(2)
        buffer[0] = 0x1b
        buffer[1] = 0x40
        try {
            printer?.clearReadBuffer()
            printer?.writeData(buffer)
        } catch (e: CustomException) {
            Log.i("Error", "Printer clear buffer error")
        } catch (e: Exception) {
            Log.i("Error", "Print Clear Error...")
        }
    }

}