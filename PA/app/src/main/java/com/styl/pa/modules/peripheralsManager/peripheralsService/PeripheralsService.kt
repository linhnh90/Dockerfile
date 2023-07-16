package com.styl.pa.modules.peripheralsManager.peripheralsService

import android.content.Context
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.styl.castle_terminal_upt1000_api.message.device.SOFItem
import com.styl.pa.entities.GeneralException
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.peripheralsManager.printerManager.IPrinterManager
import com.styl.pa.modules.peripheralsManager.printerManager.PrinterManager
import com.styl.pa.modules.peripheralsManager.scannerManager.IScannerManager
import com.styl.pa.modules.peripheralsManager.scannerManager.ScannerManager
import com.styl.pa.modules.peripheralsManager.terminalManager.IPaymentResultListener
import com.styl.pa.modules.peripheralsManager.terminalManager.ITerminalManager
import com.styl.pa.modules.peripheralsManager.terminalManager.TerminalManager
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.printer.customPrinterService.HandlePrintStatus
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.modules.terminal.upt1000fterminal.SettingCallbacks
import com.styl.pa.utils.MySharedPref

/**
 * Created by NgaTran on 9/15/2020.
 */

open class PeripheralsService : IPeripheralsService, IScannerManager, IPrinterManager, ITerminalManager {
    private var peripheralsPresenter: IMainContract.IPeripheralPresenter? = null
    var peripheralsSupport: PeripheralsSupport? = null

    constructor(peripheralsPresenter: IMainContract.IPeripheralPresenter) {
        this.peripheralsSupport = PeripheralsSupport()
        this.peripheralsPresenter = peripheralsPresenter
    }

    override fun detectUsbPeripherals(context: Context?) {
        context?.let {
            val pre = MySharedPref(it)
            var supportedTerminalList = Gson().fromJson<HashMap<Int, PeripheralsInfo>>(pre.terminalList,  object : TypeToken<HashMap<Int, PeripheralsInfo>>() {}.type)

            if (supportedTerminalList.isNullOrEmpty()) {
                supportedTerminalList = HashMap<Int, PeripheralsInfo>()
                supportedTerminalList.put(0,
                        PeripheralsInfo(
                                Peripheral.TERMINAL_TYPE,
                                Peripheral.TERMINAL_UPT1000F_VID,
                                Peripheral.TERMINAL_UPT1000F_PID,
                                Peripheral.TERMINAL_UPT1000F_MANUFACTURER_NAME,
                                Peripheral.TERMINAL_UPT1000F_PRODUCT_NAME
                        )
                )
                pre.terminalList = Gson().toJson(supportedTerminalList)
            }
            peripheralsSupport?.addPeripheralsSupportedInfo(Peripheral.TERMINAL_TYPE, ArrayList<PeripheralsInfo>(supportedTerminalList.values))

            val usbManager: UsbManager? =
                    context.getSystemService(Context.USB_SERVICE) as? UsbManager
            var hasScanner = false
            var hasPrinter = false
            usbManager?.let {
                val deviceListMap = usbManager.deviceList
                val deviceList: ArrayList<UsbDevice> = ArrayList(deviceListMap.values)
                deviceList.forEach lit@{ usbDevice ->
                    var peripheralsInfo = peripheralsSupport?.isScanner(usbDevice)
                    if (peripheralsInfo != null) {
                        hasScanner = true
                        initScannerService(peripheralsInfo)
                        return@lit
                    }

                    peripheralsInfo = peripheralsSupport?.isPrinter(usbDevice)
                    if (peripheralsInfo != null) {
                        hasPrinter = true
                        initPrinterService(peripheralsInfo)
                        return@lit
                    }

                    peripheralsInfo = peripheralsSupport?.isTerminal(usbDevice)
                    if (peripheralsInfo != null) {
                        initTerminalService(peripheralsInfo)
                        return@lit
                    }
                }
            }

            if (!hasPrinter) {
                peripheralsPresenter?.printerNotFound()
            }

            if (!hasScanner) {
                peripheralsPresenter?.scannerNotFound()
            }

            // For terminal UArt
            checkInitTerminalService()
        }
    }


    // init Scanner
    private var scannerManager: ScannerManager? = null
    override fun initScannerService(peripheralsInfo: PeripheralsInfo) {
        if (scannerManager == null) {
            scannerManager = ScannerManager(peripheralsPresenter)
        }
        scannerManager?.initScannerService(peripheralsInfo)
    }

    override fun initScannerService(usbDevice: UsbDevice) {
        val peripheralsInfo = peripheralsSupport?.isScanner(usbDevice)
        if (peripheralsInfo != null) {
            initScannerService(peripheralsInfo)
        }
    }

    override fun pullTrigger(isPull: Boolean) {
        scannerManager?.pullTrigger(isPull)
    }

    override fun setConfigEvent(listener: DcssdkListener.DcssdkConfig) {
        scannerManager?.setConfigEvent(listener)
    }

    override fun detachScannerUsb() {
        scannerManager?.detachScannerUsb()
    }

    override fun disconnectScanner() {
        scannerManager?.disconnectScanner()
    }

    // init Printer
    private var printerManager: PrinterManager? = null
    override fun initPrinterService(peripheralsInfo: PeripheralsInfo) {
        if (printerManager == null) {
            printerManager = PrinterManager(peripheralsPresenter)
        }
        printerManager?.initPrinterService(peripheralsInfo)
    }

    override fun initPrinterService(usbDevice: UsbDevice) {
        val peripheralsInfo = peripheralsSupport?.isPrinter(usbDevice)
        if (peripheralsInfo != null) {
            initPrinterService(peripheralsInfo)
        }
    }

    override fun getPrinterFontConfig(): IPrinterFontConfig? {
        return printerManager?.getPrinterFontConfig()
    }

    override fun setHandlePrinterStatusEvent(event: HandlePrintStatus) {
        printerManager?.setHandlePrinterStatusEvent(event)
    }

    override fun printText(text: String, printerFont: Int?, fontSize: Int?, isBold: Boolean, isItalic: Boolean, isUnderline: Boolean): GeneralException? {
        return printerManager?.printText(text, printerFont, fontSize, isBold,
                isItalic, isUnderline)
    }

    override fun printImage(bitmap: Bitmap?) {
        printerManager?.printImage(bitmap)
    }

    override fun cutPage() {
        printerManager?.cutPage()
    }

    override fun printFeed() {
        printerManager?.printFeed()
    }

    override fun checkStatusPrinter(): GeneralException? {
        return printerManager?.checkStatusPrinter()
    }

    override fun checkStatusPaper(): GeneralException? {
        return printerManager?.checkStatusPaper()
    }

    override fun disconnectPrinter() {
        printerManager?.disconnectPrinter()
    }

    override fun startCheckStatusWhenPrint() {
        printerManager?.checkStatusPrinter()
    }

    override fun endCheckStatusWhenPrint() {
        printerManager?.endCheckStatusWhenPrint()
    }


    // init Terminal
    private var terminalManager: TerminalManager? = null
    override fun initTerminalService(peripheralsInfo: PeripheralsInfo) {
        if (terminalManager == null) {
            terminalManager = TerminalManager(peripheralsPresenter)
        }

        terminalManager?.initTerminalService(peripheralsInfo)
    }

    override fun initTerminalService(usbDevice: UsbDevice) {
        val peripheralsInfo = peripheralsSupport?.isTerminal(usbDevice)
        if (peripheralsInfo != null) {
            initTerminalService(peripheralsInfo)
        }
    }

    override fun checkInitTerminalService() {
        if (terminalManager == null) {
            terminalManager = TerminalManager(peripheralsPresenter)
            terminalManager?.checkInitTerminalService()
        }
    }

    override fun isTerminalConnected(): Boolean {
        return terminalManager?.isTerminalConnected() ?: false
    }

    override fun isTerminalCommunicated(): Boolean {
        return terminalManager?.isCommunicated ?: false
    }

    override fun setPaymentCallbacks(listener: IPaymentResultListener?) {
        terminalManager?.setPaymentCallbacks(listener)
    }

    override fun payProduct(cardType: Int, amount: Int): Boolean {
        return terminalManager?.payProduct(cardType, amount) ?: false
    }

    override fun logOnTerminal(): Boolean {
        return terminalManager?.logOnTerminal() ?: false
    }

    override fun resetTerminal(): Boolean {
        return terminalManager?.resetTerminal() ?: false
    }

    override fun resetSequenceNumberTerminal(): Boolean {
        return terminalManager?.resetSequenceNumberTerminal() ?: false
    }

    override fun getStatusTerminal(): Boolean {
        return terminalManager?.getStatusTerminal() ?: false
    }

    override fun settlementTerminal(listCardType: ArrayList<String>): Boolean {
        return terminalManager?.settlementTerminal(listCardType) ?: false
    }

    override fun updateTerminalFirmware(): Boolean {
        return terminalManager?.updateTerminalFirmware() ?: false
    }

    override fun getLastTxnTerminal(): Boolean {
        return terminalManager?.getLastTxnTerminal() ?: false
    }

    override fun setSettingResultListener(settingResultListener: SettingCallbacks?) {
        terminalManager?.setSettingResultListener(settingResultListener)
    }

    override fun detachTerminalUsb() {
        terminalManager?.detachTerminalUsb()
    }

    override fun disconnectTerminalService() {
        terminalManager?.disconnectTerminalService()
        terminalManager = null
    }

    override fun requestSofList(): Boolean {
        return terminalManager?.requestSofList() ?: false
    }

    override fun setSofPriority(sofList: List<SOFItem>): Boolean {
        return terminalManager?.setSofPriority(sofList) ?: false
    }
}