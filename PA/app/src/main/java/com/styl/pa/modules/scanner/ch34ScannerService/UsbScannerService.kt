package com.styl.pa.modules.scanner.ch34ScannerService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.*
import android.widget.Toast
import cn.wch.ch34xuartdriver.CH34xUARTDriver
import com.styl.pa.entities.scanner.Barcode
import com.styl.pa.modules.peripheralsManager.scannerManager.ScannerManager
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager

/**
 * Created by Nga Tran on 7/24/2020.
 */
class UsbScannerService {
    companion object {
        private const val TAG = "UsbScannerService"
        const val ACTION_USB_PERMISSION = "com.styl.ch34scannerservice.action.USB_PERMISSION"
        const val SCANNER_PRODUCT_ID_01 = 29987
        const val SCANNER_PRODUCT_ID_02 = 21795
        const val SCANNER_PRODUCT_ID_03 = 21778
        const val SCANNER_VENDOR_ID = 6790

        private const val MAX_RETRY_CYCLE = 3
        private const val DELAY_BETWEEN_RETRY_CYCLE = 100L
    }

    private var OK_RESULT = "4f 4b 0d 0a"
    private var OK_OK_RESULT = "4f 4b 0d 0a 4f 4b 0d 0a"
    private var END_RESULT = "\r\n"
    private var HEXA_END_RESULT = "0d 0a"

    private var driver: CH34xUARTDriver? = null
    private var context: Context? = null
    private var usbScannerConnectionListener: DcssdkListener.DcssdkInit? = null
    private var usbScannerResultListener: DcssdkListener.DcssdkConfig? = null
    private var usbScannerListener: UsbScannerListener? = null

    private var qrCodeResponse = StringBuilder()

    constructor(context: Context?) {
        this.context = context

        if (driver == null) {
            driver = CH34xUARTDriver(
                context?.getSystemService(Context.USB_SERVICE) as UsbManager,
                context,
                ACTION_USB_PERMISSION
            )
        }
    }

    fun setUsbScannerConnectionListener(usbScannerConnectionListener: DcssdkListener.DcssdkInit?) {
        this.usbScannerConnectionListener = usbScannerConnectionListener
    }

    fun setUsbScannerListener(usbScannerListener: UsbScannerListener?) {
        this.usbScannerListener = usbScannerListener
    }

    fun setUsbScannerResultListener(usbScannerResultListener: DcssdkListener.DcssdkConfig?) {
        this.usbScannerResultListener = usbScannerResultListener
    }

    private val scannerUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (isScannerUsb(device)) {
                        usbScannerConnectionListener?.dcssdkConnectEvent(false)
                    }
                }

                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (isScannerUsb(device)) {
                        driver?.let {
                            if (!it.isConnected) {
                                openScannerService()
                            }
                        }
                    }
                }
            }
        }

    }

    private var isRegisterScannerUsb = false
    fun registerScannerUsbReceiver() {
        // Suggest to register in onStart
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        context?.let {
            it.registerReceiver(scannerUsbReceiver, filter, GeneralUtils.COMMON_PERMISSION, null)
            isRegisterScannerUsb = true
        }
    }

    fun unregisterScannerUsbReceiver() {
        // Suggest to unregister in onStop
        context?.let {
            if (isRegisterScannerUsb) {
                it.unregisterReceiver(scannerUsbReceiver)
                isRegisterScannerUsb = false
            }
        }
    }

    fun isScannerUsb(device: UsbDevice?): Boolean {
        if (device == null) {
            return false
        }

        return ((SCANNER_PRODUCT_ID_01 == device.productId || SCANNER_PRODUCT_ID_02 == device.productId || SCANNER_PRODUCT_ID_03 == device.productId) && SCANNER_VENDOR_ID == device.vendorId)
    }

    fun checkUsbScannerServiceSupport(): Boolean {
        if (true != driver?.UsbFeatureSupported()) {
            Toast.makeText(context, "Not supported yet", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }


    fun openScannerService(): Boolean {
        if (true != driver?.isConnected) {
            var retval = driver?.ResumeUsbPermission()
            if (0 == retval) {
                retval = driver?.ResumeUsbList()
                if (-1 == retval) {
                    usbScannerConnectionListener?.dcssdkConnectEvent(false)
                    closeUSBScannerService()
                    return false
                } else if (0 == retval) {
                    if (driver?.mDeviceConnection != null) {
                        if (false == driver?.UartInit()) {
                            usbScannerConnectionListener?.dcssdkConnectEvent(false)
                            return false
                        }

                        usbScannerConnectionListener?.dcssdkConnectEvent(true)
                        setCameraDefaultConfig()
                        return true
                    } else {
                        usbScannerConnectionListener?.dcssdkConnectEvent(false)
                        return false
                    }
                } else {
                    usbScannerConnectionListener?.dcssdkConnectEvent(false)
                    return false
                }
            } else {
                usbScannerConnectionListener?.notFoundScannerUsb()
            }
        } else {
            usbScannerConnectionListener?.dcssdkConnectEvent(true)
            return true
        }

        return false
    }

    private fun setCameraDefaultConfig() {
        // After connect to scanner successfully. Set default config
        setCameraConfig(115200, 1, 8, 0, 0)
    }

    fun setCameraConfig(
        baudRate: Int,
        dataBit: Byte,
        stopBit: Byte,
        parity: Byte,
        flowControl: Byte
    ): Boolean {
        return driver?.SetConfig(baudRate, dataBit, stopBit, parity, flowControl) ?: false
    }


    fun writeCommand(command: String): Boolean {
        val data = command.toByteArray(Charsets.UTF_8) + byteArrayOf(0x0d.toByte(), 0x0a.toByte())

        val isSuccess = driver?.WriteData(data, data.size) ?: -1
        LogManager.i(TAG, "sent data len = $isSuccess bytes")
        if (isSuccess != 12) {
            usbScannerListener?.writeCommandStatus(false, command)
            return false
        } else {
            usbScannerListener?.writeCommandStatus(true, command)
            return true
        }
    }

    fun closeUSBScannerService() {
        driver?.CloseDevice()
        if (ScannerManager.isZebra == false) {
            usbScannerConnectionListener?.dcssdkDisappeareEvent()
        }
    }

    fun destroyUSBScannerService() {
        closeUSBScannerService()
        stopEventReadScannerResponse()
    }

    private var isDone = false

    private var handlerThread: HandlerThread =
        HandlerThread("MyUsbScannerHandlerThread", Process.THREAD_PRIORITY_BACKGROUND)
    private var runnable = Runnable {
        val buffer = ByteArray(4096)

        while (true) {
            val msg = Message.obtain()
            if (true != driver?.isConnected) {
                break
            }
            val length: Int = driver?.ReadData(buffer, 4096) ?: 0
            if (length > 0) {
                val recv: String =
                    UsbScannerUtils.toHexString(
                        buffer,
                        length
                    )
                if (recv.trim() == OK_OK_RESULT || recv.trim() == OK_RESULT) {
                    LogManager.i(TAG, "revc result OK: $recv")
                }

                if (recv.trim() != OK_RESULT.trim() &&
                    recv.trim() != OK_OK_RESULT.trim()) {
                    val data: String
                    var endLength = 0
                    if (recv.trim().endsWith(HEXA_END_RESULT.trim())) {
                        isDone = true
                        endLength = 2
                    }
                    data = String(buffer, 0, length - endLength)

                    qrCodeResponse.append(data)

                    if (isDone) {
                        msg.obj = qrCodeResponse.toString()
                        usbScannerListener?.readBytes(msg)
                        responseScanResult(qrCodeResponse.toString())
                    }
                }
            }
        }
    }
    private var handler: Handler? = null

    private fun responseScanResult(data: String) {
        isDone = false
        enableScanCode(false)
        var result = data
        if (data.trim().endsWith(END_RESULT, true)) {
            result = data.trim().replace(END_RESULT, "").trim()
        }
        usbScannerResultListener?.dcssdkBarCodeResultEvent(Barcode(result.toByteArray(Charsets.UTF_8)))
    }

    fun startEventReadScannerResponse() {
        if (!handlerThread.isAlive || handlerThread.isAlive && handlerThread.isInterrupted) {
            handlerThread.start()
        }
        if (handler == null) {
            handler = Handler(handlerThread.looper)
            handler?.post(runnable)
        }
    }

    fun stopEventReadScannerResponse() {
        if (handlerThread.isAlive && !handlerThread.isInterrupted) {
            handlerThread.quitSafely()
        }

        handler?.removeCallbacks(runnable)
        handler = null
    }

    inner class MyHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            usbScannerListener?.readBytes(msg)
        }
    }


    inner class ReadThread : Thread() {
        override fun run() {
            val buffer = ByteArray(4096)

            while (true) {
                val msg = Message.obtain()
                if (true != driver?.isConnected) {
                    break
                }
                val length: Int = driver?.ReadData(buffer, 4096) ?: 0
                if (length > 0) {
                    val recv: String =
                        UsbScannerUtils.toHexString(
                            buffer,
                            length
                        )
                    msg.obj = recv
                    handler?.sendMessage(msg)
                }
            }
        }
    }

    private var isPulling: Boolean = false
    fun pullTrigger(): Boolean {
        isDone = false
        qrCodeResponse.clear()
        if (!isPulling) {
            var isSuccess = false
            if (true == driver?.isConnected) {
                isSuccess = triggerScanner(100)
            }

            if (isSuccess) {
                startEventReadScannerResponse()
                return true
            }

            return false
        } else {
            return true
        }
    }

    private fun triggerScanCodeAndRetry(isEnable: Boolean): Boolean {
        var result = false
        for(i in 0 until MAX_RETRY_CYCLE) {
            LogManager.i(TAG, "triggerScanCodeAndRetry cycle = $i")
            result = enableScanCode(isEnable)
            if (result) {
                break
            } else {
                delay(DELAY_BETWEEN_RETRY_CYCLE)
            }
        }
        return result
    }

    private fun triggerLightAndRetry(isOpen: Boolean): Boolean {
        var result = false
        for(i in 0 until MAX_RETRY_CYCLE) {
            LogManager.i(TAG, "triggerLightAndRetry cycle = $i")
            result = triggerScannerLight(isOpen)
            if (result) {
                break
            } else {
                delay(DELAY_BETWEEN_RETRY_CYCLE)
            }
        }
        return result
    }


    fun triggerScanner(millisDelayBetweenCmd: Long): Boolean {
        var isTriggerSuccess = triggerLightAndRetry(true)
        LogManager.i(TAG, "triggerLightAndRetry isTriggerSuccess = $isTriggerSuccess")
        delay(millisDelayBetweenCmd)
        isTriggerSuccess = triggerScanCodeAndRetry(true)
        LogManager.i(TAG, "triggerScanCodeAndRetry isTriggerSuccess = $isTriggerSuccess")

        if (!isTriggerSuccess) {
            unTriggerScanner()
            usbScannerResultListener?.dcssdkPullTriggerEvent(false)
            return false
        } else {
            isPulling = true
            usbScannerResultListener?.dcssdkPullTriggerEvent(true)
            return true
        }
    }

    fun unTriggerScanner() {
        isPulling = false
        enableScanCode(false)
        delay(100)
        triggerScannerLight(false)
    }

    /* trigger light,
        open light cmd: R_CMD_3001<CR><LF>
        close light cmd: R_CMD_3000<CR><LF>
    */
    private fun triggerScannerLight(isOpen: Boolean): Boolean {
        if (isOpen) {
            return writeCommand(ScannerCommand.OPEN_LIGHT_CMD)
        } else {
            return writeCommand(ScannerCommand.CLOSE_LIGHT_CMD)
        }
    }

    /* trigger scan code,
        enable scan code cmd: R_CMD_100B<CR><LF>
        disable scan code cmd: R_CMD_100A<CR><LF>
    */
    private fun enableScanCode(isEnable: Boolean): Boolean {
        if (isEnable) {
            return writeCommand(ScannerCommand.ENABLE_SCAN_CODE_CMD)
        } else {
            return writeCommand(ScannerCommand.DISABLE_SCAN_CODE_CMD)
        }
    }

    private fun delay(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            LogManager.d(TAG, "delay error")
        }
    }

    interface UsbScannerConnectionListener {
        fun notFoundScannerUsb()
        fun isConnect(isConnect: Boolean)
    }

    interface UsbScannerListener {
        fun writeCommandStatus(isSuccess: Boolean, command: String)
        fun readBytes(msg: Message)
    }
}