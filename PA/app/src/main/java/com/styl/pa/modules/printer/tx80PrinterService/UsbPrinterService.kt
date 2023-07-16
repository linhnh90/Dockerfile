package com.styl.pa.modules.printer.tx80PrinterService

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Handler
import com.styl.pa.R
import com.styl.pa.entities.GeneralException
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.ERROR_CODE
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.VALID_CODE
import com.styl.pa.modules.printer.tx80PrinterService.permission.UsbPrinterPermission
import com.tx.printlib.Const
import com.tx.printlib.UsbPrinter


/**
 * Created by Nga Tran on 7/14/2020.
 */
class UsbPrinterService {
    companion object {
        val MAX_PAPER_CHARACTER = 46

        private var usbPrinterString: UsbPrinterService? = null

        fun newInstance(
                applicationContext: Context?,
                activity: Activity?,
                printerConnectionStatusListener: PrinterConnectionStatusListener?
        ): UsbPrinterService? {
            if (usbPrinterString == null) {
                usbPrinterString =
                        UsbPrinterService(applicationContext, activity, printerConnectionStatusListener)
            }
            return usbPrinterString
        }
    }

    private val SINGNATURE_FOLDER = "/signature/"
    private val SIGNATURE_NAME = "signature.png"
    private val THREAD_NAME = "PRINTER_THREAD"
    private var usbPrinter: UsbPrinter? = null
    private var context: Context? = null
    private var printerConnectionStatusListener: PrinterConnectionStatusListener? = null
    private val PRINTER_CONNETION_RETRY = 10

    private var myThread: Thread? = null
    private var resultHandler: Handler = Handler()

    private var usbPrinterPermission: UsbPrinterPermission? = null

    constructor(
            applicationContext: Context?,
            activity: Activity?,
            printerConnectionStatusListener: PrinterConnectionStatusListener?
    ) {
        this.context = applicationContext
        this.printerConnectionStatusListener = printerConnectionStatusListener

        activity?.let {
            usbPrinterPermission =
                    UsbPrinterPermission.newInstances(true, usbPrinterPermissionCallback)
            usbPrinterPermission?.checkStartPermissionActivity(activity)
        }
    }

    private val usbPrinterPermissionCallback =
            object : UsbPrinterPermission.IUsbPermissionCallback {
                override fun onUsbCameraPermissionResult(isAllow: Boolean) {
                    if (isAllow) {
                        initUsbPrinter()
                    } else {
                        printerConnectionStatusListener?.printerConnectionStatus(
                                GeneralException(PrinterErrorType.ERROR_CODE,
                                        context?.getString(R.string.external_storage_deny_retry))
                        )
                    }
                    usbPrinterPermission?.destroyUsbPrinterPermission()
                    usbPrinterPermission = null
                }

            }

    private fun initUsbPrinter() {
        myThread = Thread(Runnable {
            context?.let {
                usbPrinter = UsbPrinter(it)
            }

            if (false == myThread?.isInterrupted) {
                myThread?.interrupt()
            }
        }, THREAD_NAME)

        myThread?.start()
    }

    private fun getCorrectDevice(): UsbDevice? {
        val usbManager = context?.getSystemService(Context.USB_SERVICE) as? UsbManager
        val deviceMap: Map<String, UsbDevice>? = usbManager?.deviceList
        if (deviceMap != null) {
            deviceMap.keys.forEach {
                val deviceItem = deviceMap.get(it)
                if (deviceItem != null && UsbPrinter.checkPrinter(deviceItem)) {
                    return deviceItem
                }
            }
        }
        return null
    }

    fun connectPrinter() {
        myThread = Thread(Runnable {
            var status = GeneralException(ERROR_CODE, context?.getString(R.string.cant_connect_printer))
            val correctedUsbDevice = getCorrectDevice()
            if (correctedUsbDevice != null) {
                status = connectPrinterRetryFlow(correctedUsbDevice) ?: status
            }

            if (false == myThread?.isInterrupted) {
                myThread?.interrupt()
            }
            resultHandler.post {
                printerConnectionStatusListener?.printerConnectionStatus(status)
            }
        }, THREAD_NAME)

        myThread?.start()
    }

    private fun connectPrinterRetryFlow(correctedUsbDevice: UsbDevice): GeneralException? {
        val i = 0
        var connectionStatus: Boolean? = false
        while (true != connectionStatus && i < PRINTER_CONNETION_RETRY) {
            connectionStatus = usbPrinter?.open(correctedUsbDevice)
        }

        if (true == connectionStatus) {
            usbPrinter?.init()
        }

        if (true == connectionStatus) {
            return GeneralException(VALID_CODE, "")
        } else {
            return GeneralException(ERROR_CODE, context?.getString(R.string.cant_connect_printer))
        }
    }

    private fun setFont(
            printerFont: Int?, fontSize: Int?, isBold: Boolean,
            isItalic: Boolean, isUnderline: Boolean
    ) {
        usbPrinter?.resetFont()
        usbPrinter?.doFunction(Const.TX_SEL_FONT, Const.TX_FONT_A, 0)
        usbPrinter?.doFunction(Const.TX_UNIT_TYPE, Const.TX_UNIT_PIXEL, 0)
        usbPrinter?.doFunction(Const.TX_ALIGN, printerFont ?: Const.TX_ALIGN_LEFT, 0)
        usbPrinter?.doFunction(Const.TX_FONT_SIZE, fontSize ?: Const.TX_SIZE_1X, fontSize
                ?: Const.TX_SIZE_1X)
        if (isBold) {
            usbPrinter?.doFunction(Const.TX_FONT_BOLD, Const.TX_ON, 0)
        } else {
            usbPrinter?.doFunction(Const.TX_FONT_BOLD, Const.TX_OFF, 0)
        }
        if (isUnderline) {
            usbPrinter?.doFunction(Const.TX_FONT_ULINE, Const.TX_ON, 0)
        } else {
            usbPrinter?.doFunction(Const.TX_FONT_ULINE, Const.TX_OFF, 0)
        }
    }

    fun printText(
            text: String, printerFont: Int?, fontSize: Int?, isBold: Boolean,
            isItalic: Boolean, isUnderline: Boolean
    ) {
        setFont(printerFont, fontSize, isBold, isItalic, isUnderline)
        usbPrinter?.outputStringLn(text)
    }

    fun printImage(bitmap: Bitmap?) {

        // to print signature (removed due to sonarqube security hotspots)
    }


    fun endPrintReceipt() {
        setPixelUnit()
        printFeed()
        cutPage()
    }

    fun setPixelUnit() {
        usbPrinter?.doFunction(Const.TX_UNIT_TYPE, Const.TX_UNIT_PIXEL, 0)
    }

    fun printFeed() {
        usbPrinter?.doFunction(Const.TX_FEED, 200, 0)
    }

    fun cutPage() {
        usbPrinter?.doFunction(Const.TX_CUT, Const.TX_CUT_FULL, 0)
    }

    fun checkStatusPrinter(): GeneralException? {
        val usbStatus: Int? = usbPrinter?.status?.toInt()
                ?: return GeneralException(PrinterErrorType.ERR_NULL, context?.getString(R.string.err_null))

        val usbStatus2: Long? = usbPrinter?.status2
                ?: return GeneralException(PrinterErrorType.ERR_NULL, context?.getString(R.string.err_null))

        if (usbStatus2 == 0L) {
            return GeneralException(
                    PrinterErrorType.ERR_CALL_STATUS_FAIL,
                    context?.getString(R.string.err_call_status_fail)
            )
        }

        getStatusByCommand()
        readStatusByCommand()
        if (usbStatus2 == PrinterError.NORMAL_STATUS.toLong()) {
            return GeneralException(
                    PrinterErrorType.VALID_CODE, ""
            )
        } else {
            if (usbStatus2 == PrinterError.ERR_NO_PAPER) {
                return GeneralException(PrinterErrorType.ERROR_NO_PAPER,
                        PrinterErrorType.errorHash.get(PrinterErrorType.ERROR_NO_PAPER))
            } else if (usbStatus2 == PrinterError.ERR_PAPER_NOT_PROPERLY_INSERTED) {
                return GeneralException(PrinterErrorType.ERROR_NO_PAPER,
                        context?.getString(R.string.error_paper_not_properly_inserted))
            } else if (usbStatus2 == PrinterError.ERR_LOW_PAPER) {
                return GeneralException(PrinterErrorType.LOW_PAPER,
                        PrinterErrorType.errorHash.get(PrinterErrorType.LOW_PAPER))
            } else if (usbStatus == PrinterError.ERR_PRINTER_TURN_OFF) {
                return GeneralException(PrinterErrorType.LOST_CONNECT_PRINTER,
                        context?.getString(R.string.lost_connect_printer))
            } else {
                return GeneralException(PrinterErrorType.ERROR_CODE,
                        context?.getString(R.string.error_check_status_with_code, usbStatus, usbStatus2))
            }
        }
    }

    fun getPrinterStatusMessage(status: PrinterError.PrinterStatus): String? {
        context?.let {
            return status.getErrorMessage(it)
        }
        return null
    }

    fun handlePrinterTask(runnable: Runnable) {
        myThread = Thread(runnable, THREAD_NAME)
        myThread?.start()
    }

    fun stopPrintTask() {
        if (false == myThread?.isInterrupted) {
            myThread?.interrupt()
        }
    }

    fun disconnectPrinter() {
        usbPrinter?.close()
        usbPrinter = null
    }

    interface PrinterConnectionStatusListener {
        fun printerConnectionStatus(result: GeneralException)
    }

    interface PrinterStatusListener {
        fun printerStatus()
    }

    private fun getStatusByCommand() {
        val buff = byteArrayOf(0x10.toByte(), 0x04, 0x04)
        usbPrinter?.write(buff, buff.size)
    }

    private fun readStatusByCommand() {
        val buff = ByteArray(512)
        usbPrinter?.read(buff, buff.size)
    }
}