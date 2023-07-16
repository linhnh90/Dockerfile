package com.styl.pa.modules.peripheralsManager.printerManager

import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import com.styl.pa.entities.GeneralException
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.printer.customPrinterService.HandlePrintStatus

/**
 * Created by NgaTran on 9/15/2020.
 */
interface IPrinterManager {
    fun initPrinterService(peripheralsInfo: PeripheralsInfo)
    fun initPrinterService(usbDevice: UsbDevice)
    fun getPrinterFontConfig(): IPrinterFontConfig?
    fun setHandlePrinterStatusEvent(event: HandlePrintStatus)
    fun printText(text: String, printerFont: Int?, fontSize: Int?,
                  isBold: Boolean, isItalic: Boolean, isUnderline: Boolean): GeneralException?

    fun printImage(bitmap: Bitmap?)
    fun cutPage()
    fun printFeed()
    fun checkStatusPrinter(): GeneralException?
    fun checkStatusPaper(): GeneralException?
    fun disconnectPrinter()
    fun startCheckStatusWhenPrint()
    fun endCheckStatusWhenPrint()
}