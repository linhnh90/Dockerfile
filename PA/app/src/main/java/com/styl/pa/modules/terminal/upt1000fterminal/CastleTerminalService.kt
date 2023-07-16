package com.styl.pa.modules.terminal.upt1000fterminal

import android.os.Handler
import androidx.annotation.StringRes
import android.util.Log
import android.widget.Toast
import com.styl.castle_terminal_upt1000_api.connector.ICountDownTimer
import com.styl.castle_terminal_upt1000_api.define.*
import com.styl.castle_terminal_upt1000_api.message.device.SOFListResponse
import com.styl.castle_terminal_upt1000_api.message.payment.TxnDataEntity
import com.styl.castle_terminal_upt1000_api.protocol.CastleProtocol
import com.styl.castle_terminal_upt1000_api.protocol.CastleProtocol.BootUpState
import com.styl.castle_terminal_upt1000_api.protocol.ProtocolInterface
import com.styl.pa.R
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.peripheralsManager.GeneralUtils
import com.styl.pa.modules.peripheralsManager.GeneralUtils.Companion.toHexString
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.peripheralsManager.terminalManager.ICustomPaymentCallback
import com.styl.pa.modules.peripheralsManager.uart.config.UartService
import com.styl.pa.modules.peripheralsManager.uart.listener.IUartService
import com.styl.pa.modules.peripheralsManager.uart.listener.UartListener
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import java.util.*

/**
 * This class handle connection between the app and UPT1000 module
 * Handle init and interact with usbService in the UPT1000 module
 * /**
 * INTEGRATE CASTLE UPT1000 TERMINAL
 * */
 * */
class CastleTerminalService(val activity: MainActivity) : ProtocolInterface {

    companion object {
        const val FTDI_VID = 1027
        const val FTDI_PID = 24577 //0x6001
        const val ATEN_RS232_TO_USB_VID = 1367
        const val ATEN_RS232_TO_USB_PID = 8200 //0x2008
        const val FUJIVM_TERMINAL_ID = 111111
        const val CASTLE_TERMINAL_ID = 36019802

        const val UART_PORT_NAME = "ttyS3"

        private val TAG: String = "CastleTerminalService"
    }

    private var uartService: IUartService? = null
    private var paymentListener: ICustomPaymentCallback? = null
    private var settingsListener: SettingCallbacks? = null
    private var timerListener: ICountDownTimer? = null
    private var protocol: CastleProtocol? = null
    private var portReady: Boolean? = false
    private var usbErrorCode: Int? = null
    private var isRegisterUsbService = false
    private var firstTime = MySharedPref(activity).terminalFirstTimeHandling
    private var pid: Int? = null
    private var vid: Int? = null

    private var isPaymentFlow = false
    private fun resetPaymentFLowFlag() {
        if (isPaymentFlow) {
            isPaymentFlow = false
        }
    }

    fun getCastleDeviceBootUpState(): BootUpState? {
        return protocol?.bootUpState
    }

    init {
        uartService = UartService()
    }

    private var listenerUart = object : UartListener {
        override fun onOpenPort(error: String?) {
            portReady = error == null
            Log.e(TAG, error ?: "terminal port ready, firstTimeLaunchApp = $firstTime")
            if (portReady == true) {
                // skip terminal bootup in case simulate terminal
                if (!com.styl.pa.utils.GeneralUtils.isSimulateTerminal()) {
                    bootUp()
                }
                if (settingsListener != null) {
                    Log.e(TAG, error ?: "connect = true")
                    settingsListener?.connectResult(true)
                }
            } else {
                settingsListener?.connectResult(false)
            }
        }

        override fun onReceiveData(data: ByteArray) {
            Log.e(TAG, String.format(Locale.ENGLISH,
                    "terminal receive: %s", GeneralUtils.hexToString(data)))
            val protocol = CastleProtocol.getInstance()
            protocol.processRcvBytes(data)
        }
    }

    private fun bootUp() {
        if (protocol?.bootUpState == BootUpState.OFF) {
            if (firstTime) {
                protocol?.bootUpFlow(0, true)
            } else {
                protocol?.bootUpFlow(0, false)
            }
        }
    }

    private fun setUpProtocolAndConfiguration() {
        protocol = CastleProtocol.getInstance()
        protocol?.clearAll()
        protocol?.setProtocolInterface(this)
        SerialPortConfig.setUsbPortPID(pid ?: ATEN_RS232_TO_USB_PID)
        SerialPortConfig.setUsbPortVID(vid ?: ATEN_RS232_TO_USB_VID)
        SerialPortConfig.setDeviceProvider(MessageHeader.DeviceProvider.NETS.value)
        SerialPortConfig.setDeviceNumber(FUJIVM_TERMINAL_ID)
        SerialPortConfig.setEncryptionMAC(
                byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        )
    }

    fun isListenerNull(): Boolean {
        return paymentListener == null
    }

    fun setPaymentListener(listener: ICustomPaymentCallback?) {
        if (listener == null) {
            resetPaymentFLowFlag()
        }
        this.paymentListener = listener
    }

    fun setSettingsListener(listener: SettingCallbacks?) {
        this.settingsListener = listener
    }

    fun clear() {
        protocol?.clear()
    }

    /**
     * Open terminal port,
     * @return is executed
     * */
    private fun openPort(): Boolean {
        var doOpen = false
        if (portReady != true) {
            var portName: String? = null
            pid?.let { pid ->
                vid?.let { vid ->
                    portName = uartService?.getComPort(pid.toHexString(), vid.toHexString())
                }
            }
            if (portName.isNullOrEmpty()) {
                portName = UART_PORT_NAME
            }

            portName?.let {
                setUpProtocolAndConfiguration()
                getSeqNumberFromSharedPref()
                uartService?.setListener(listenerUart)
                uartService?.setReadTimeout(50)
                uartService?.openPort(it, SerialPortConfig.BAUD_RATE)
                doOpen = true
            }

            Log.e(TAG, "openPort")
        }
        if (!doOpen && portReady != true) {
            Handler().postDelayed({
                openPort()
            }, 3000)
        }
        return doOpen
    }


    /**
     * Called onPause, MAYBE not called when POWER OFF
     * @return is executed
     * */
    private fun closePort(): Boolean {
        var doClose = false //RS232
        uartService?.closePort()
        portReady = false
        doClose = true
        Log.e(TAG, "closePort")
        return doClose
    }

    fun sendMessageToTerminal(msg: BaseMessage): Boolean {
        if (checkPortReady() && protocol?.sendMessage(msg, 0) == true) {
            return true
        }
        return false
    }

    fun voidLastSuccessCreditTxn(): Boolean {
        if (checkPortReady()) {
            return protocol?.voidLastSuccessCreditTxn() ?: false
        }
        return false
    }

    fun requestTmsOTA(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestTMS_OTA() ?: false
        }
        return false
    }

    fun requestLastTransactionStatus(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestLastTransactionStatus(0, false) ?: false
        }
        return false
    }

    override fun onSetSofResponse(p0: Boolean) {
        settingsListener?.onSetSofResponse(p0)
    }

    fun requestSofList(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestSofList(0) ?: false
        }
        return false
    }

    fun requestLogon(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestLogon(0) ?: false
        }
        return false
    }

    fun requestResetSequenceNumber(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestResetSequenceNumber(0) ?: false
        }
        return false
    }

    fun requestDeviceStatus(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestDeviceStatus(0) ?: false
        }
        return false
    }

    fun requestResetDevice(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestResetDevice(0) ?: false
        }
        return false
    }

    private fun checkPortReady(): Boolean {
        if (portReady != true) {
            usbErrorCode = usbErrorCode ?: R.string.terminal_connection_port_not_ready
            showUsbError(usbErrorCode)
        }
        return portReady == true
    }

    private fun showUsbError(@StringRes usbErrorCode: Int?) {
        Log.e(TAG, "terminal_usb_port_not_ready")
        usbErrorCode?.let {
            showErrorMessage(it)
        }
    }

    fun onCreate() {
        Log.e(TAG, "onCreate()")
        setAutoSettlementAtATime()
    }

    fun onResume() {
        Log.e(TAG, "onResume()")
        openPort()
    }

    fun onResume(peripheralsInfo: PeripheralsInfo?) {
        pid = peripheralsInfo?.pid
        vid = peripheralsInfo?.vid
        openPort()
    }

    fun onPause() {
        Log.e(TAG, "onPause()")
        closePort()
    }

    fun onDestroy() {
        Log.e(TAG, "onDestroy()")
        protocol?.onDestroy()
        portReady = false
        isRegisterUsbService = false
    }

    override fun sendBytes(byteSend: ByteArray, delay: Int): Boolean {
        return if (uartService != null && protocol != null) {
            Handler().postDelayed({
                paymentListener?.writePaymentData(protocol?.decodeBytesToMessages(byteSend))

                uartService?.writeData(byteSend)
                protocol?.sendBytesCallback()

            }, delay.toLong())
            true
        } else {
            false
        }
    }

    fun autoSettlement() {
        if (portReady == true) {
            protocol?.autoSettlement()
        }
    }

    fun onNetsTerminalConnectionTypeChange(type: Int) {
        closePort()
        Handler().postDelayed({
            uartService = UartService()
            openPort()
        }, 2000)
    }

    fun requestNetsSettlement(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestNetsSettlement(0) ?: false
        }
        return false
    }

    fun requestCreditSettlement(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestCreditSettlement(0) ?: false
        }
        return false
    }

    fun requestCepasSettlement(): Boolean {
        if (checkPortReady()) {
            return protocol?.requestCepasSettlement(0) ?: false
        }
        return false
    }

    fun setTimerListener(listener: ICountDownTimer?) {
        this.timerListener = listener
    }

    override fun onBootUpFlowDone() {
        Log.e(TAG, "onBootUpFlowDone")
        if (firstTime) {
            MySharedPref(activity).terminalFirstTimeHandling = protocol?.isResetSequenceSuccess != true
            firstTime = false
        }
        settingsListener?.onBootUpFlowDone()
//        dismissLoading()
    }

    override fun onNetsSettlementFail(error: Int) {
        settingsListener?.onNetsSettlementFail(error)
    }

    override fun onNetsSettlementSuccess() {
        settingsListener?.onNetsSettlementSuccess()
    }

    override fun onCreditSettlementSuccess() {
        settingsListener?.onCreditSettlementSuccess()
    }

    override fun onCreditSettlementFail(error: Int) {
        settingsListener?.onCreditSettlementFail(error)
    }

    override fun onCepasSettlementSuccess() {
        settingsListener?.onCepasSettlementSuccess()
    }

    override fun onCepasSettlementFail(p0: Int) {
        settingsListener?.onCepasSettlementFail(p0)
    }

    override fun onLogonResponse(success: Boolean) {
        settingsListener?.onLogonResponse(success)
    }

    override fun onFinishWaitLogonAgainTimer() {
        settingsListener?.onFinishWaitLogonAgainTimer()
    }

    override fun onACKReceived(msgType: Int, msgCode: Int) {
        Log.e(TAG, "onACKReceived")
        if (msgType == MsgType.MSG_TYPE_PAYMENT) {
            isPaymentFlow = true
            onPaymentACKReceived()
        } else {
            if (isPaymentFlow) {
                onPaymentACKReceived()
            }
        }
    }

    override fun onPaymentACKReceived() {
        Log.e(TAG, "onPaymentACKReceived")
        paymentListener?.onPaymentACKReceived()
    }

    override fun onACKTimeout() {
        resetPaymentFLowFlag()
        paymentListener?.onACKTimeout()
        settingsListener?.onACKTimeout()
    }

    override fun onResponseReceived(msgType: Int, msgCode: Int, msgStatus: Int) {
        if (msgCode != MsgCode.MSG_CODE_DEVICE_LOGON && msgCode != MsgCode.MSG_CODE_DEVICE_SETTLEMENT) {
//            dismissLoading()
        }
        if (msgCode == MsgCode.MSG_CODE_DEVICE_RESET_SEQUENCE_NUMBER && msgStatus == MsgStatus.SUCCESS) {
            Log.e(TAG, "Reset sequence number successfully")
        }
    }

    override fun onPaymentSuccess() {
        resetPaymentFLowFlag()
        paymentListener?.onPaymentSuccess()
    }

    override fun onPaymentSuccess(p0: TxnDataEntity?, p1: BaseMessage?) {
        Log.e(TAG, "onPaymentSuccess")
        resetPaymentFLowFlag()
        paymentListener?.onPaymentSuccess(p0, p1)
    }

    override fun onPaymentFail(errorCode: Int, errorStrId: Int, p2: Int) {
        LogManager.d(TAG, "onPaymentFail $errorCode, $errorStrId, $p2")
    }

    override fun onPaymentFail(p0: TxnDataEntity?, p1: BaseMessage?) {
        resetPaymentFLowFlag()
        paymentListener?.onPaymentFail(p0, p1)
    }

    override fun onVoidTransactionSuccess() {
        Log.e(TAG, "onVoidTransactionSuccess")
        paymentListener?.onVoidTransactionSuccess()
    }

    override fun onVoidTransactionFail(error: Int) {
        Log.e(TAG, "onVoidTransactionFail")
        paymentListener?.onVoidTransactionFail(error)
    }

    override fun onResetDeviceResponse(success: Boolean) {
        settingsListener?.onResetDeviceResponse(success)
    }

    override fun onResetSequenceNumber(success: Boolean) {
        settingsListener?.onResetSequenceNumber(success)
        if (firstTime) {
            MySharedPref(activity).terminalFirstTimeHandling = false
        }
    }

    override fun onTMSResponse(success: Boolean) {
        val result = if (success) "TMS successfully" else "TMS failed"
        showToast(result)
        settingsListener?.onTMSResponse(success)
    }

    override fun onLastTxnResponse(success: Boolean) {
        settingsListener?.onLastTxnResponse(success)
    }

    override fun onSofListResponse(p0: SOFListResponse?) {
        settingsListener?.onSofListResponse(p0)
    }

    override fun onWaitingResponseCountDown(timeLeft: Long) {
        timerListener?.onWaitingResponseCountDown(timeLeft)
    }

    override fun onPendingCountDown(timeLeft: Long) {
        timerListener?.onPendingCountDown(timeLeft)
    }

    override fun onProtocolError(errorCode: Int, errorStrId: Int) {
        Log.e(TAG, "onProtocolError(), errorCode = $errorCode, ${activity.getString(errorStrId)}")
//        dismissLoading()
        settingsListener?.callForSupportAndLog(errorCode, errorStrId)
    }

    override fun onSequenceNumberChanged(seq: Int) {
        updateSeqNumberToSharedPref(seq)
    }

    private fun updateSeqNumberToSharedPref(seq: Int) {
        MySharedPref(activity).terminalSequenceNumber = seq
        Log.e(TAG, "Update sequence number, saved SEQ = ${MySharedPref(activity).terminalSequenceNumber}")
    }

    override fun onDeviceStatusResponse(msgStatus: Int) {
        settingsListener?.onDeviceStatusResponse(msgStatus)
    }

    override fun callForSupportAndLog(errorCode: Int, errorStrId: Int) {
        Log.e(TAG, "callForSupportAndLog()")
//        dismissLoading()
        settingsListener?.callForSupportAndLog(errorCode, errorStrId)
    }

    override fun printLog(txt: String?, colorId: Int, gravity: Int) {
        settingsListener?.printLog(txt, colorId, gravity)
    }

    /**
     * If the reboot time is from 11pm to 12:00am (1 hour),
     * after reboot, trigger settle but usb port is not ready
     * Should handle this case
     * */
    private fun setAutoSettlementAtATime() {
        LogManager.d(TAG, "setAutoSettlementAtATime")
        return
    }

    private fun showToast(text: String) {
        if (!activity.isFinishing) {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun dismissLoading() {
        if (protocol?.bootUpState != BootUpState.RUNNING) {
            activity.dismissLoading()
        }
    }

    private fun showErrorMessage(resError: Int) {
        activity.showErrorMessage(resError)
    }

    private fun getSeqNumberFromSharedPref() {
        val sharedPref = MySharedPref(activity)
        if (!sharedPref.terminalFirstTimeHandling) {
            CastleProtocol.setSequenceNumber(sharedPref.terminalSequenceNumber)
            Log.e(TAG, "firstTimeLaunchApp = false, SEQ = ${sharedPref.terminalSequenceNumber}")
        }
    }
}
