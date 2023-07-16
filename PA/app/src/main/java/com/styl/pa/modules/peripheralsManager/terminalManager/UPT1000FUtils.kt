package com.styl.pa.modules.peripheralsManager.terminalManager

import android.os.Handler
import com.styl.castle_terminal_upt1000_api.connector.ICountDownTimer
import com.styl.castle_terminal_upt1000_api.define.*
import com.styl.castle_terminal_upt1000_api.message.MessageFactory
import com.styl.castle_terminal_upt1000_api.message.MessageTag
import com.styl.castle_terminal_upt1000_api.message.device.SOFItem
import com.styl.castle_terminal_upt1000_api.message.device.SOFListResponse
import com.styl.castle_terminal_upt1000_api.message.device.SOFMessage
import com.styl.castle_terminal_upt1000_api.message.payment.PaymentMessage
import com.styl.castle_terminal_upt1000_api.message.payment.TxnDataEntity
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.payment.view.PaymentFragment
import com.styl.pa.modules.payment.view.PaymentFragment.Companion.PAYMENT_RESPONE_TIME_OUT
import com.styl.pa.modules.payment.view.PaymentFragment.Companion.PAYMENT_RETRY_EXTEND
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.peripheralsManager.terminalManager.TerminalErrorCode.TERMINAL_NOT_RESPONSE
import com.styl.pa.modules.peripheralsManager.terminalManager.TerminalErrorCode.TERMINAL_NOT_RESPONSE_ACK
import com.styl.pa.modules.setting.terminalSetting.view.TerminalSettingsFragment.Companion.CARD_CREDIT
import com.styl.pa.modules.setting.terminalSetting.view.TerminalSettingsFragment.Companion.CARD_EZLINK
import com.styl.pa.modules.setting.terminalSetting.view.TerminalSettingsFragment.Companion.CARD_NETS
import com.styl.pa.modules.terminal.upt1000fterminal.CastleTerminalService
import com.styl.pa.modules.terminal.upt1000fterminal.SettingCallbacks

/**
 * Created by NgaTran on 10/9/2020.
 */
@ExcludeFromJacocoGeneratedReport
class UPT1000FUtils {
    private var peripheralsPresenter: IMainContract.IPeripheralPresenter? = null
    private var terminalManager: TerminalManager? = null
    private var castleTerminalService: CastleTerminalService? = null

    private var paymentResultListener: IPaymentResultListener? = null
    private var settingResultListener: SettingCallbacks? = null

    private var isCheckTerminalStatusAfterBootUp = false

    constructor(peripheralsPresenter: IMainContract.IPeripheralPresenter?, terminalManager: TerminalManager?) {
        this.peripheralsPresenter = peripheralsPresenter
        this.terminalManager = terminalManager
    }


    private val settingCallbacks = object : SettingCallbacks {
        override fun onResetSequenceNumber(p0: Boolean) {
            settingResultListener?.onResetSequenceNumber(p0)
        }

        override fun onSetSofResponse(p0: Boolean) {
            settingResultListener?.onSetSofResponse(p0)
        }

        override fun onCreditSettlementFail(p0: Int) {
            settingResultListener?.onCreditSettlementFail(p0)
            handleSettlementTerminal()
        }

        override fun onCepasSettlementSuccess() {
            settingResultListener?.onCepasSettlementSuccess()
            handleSettlementTerminal()
        }

        override fun onCepasSettlementFail(p0: Int) {
            settingResultListener?.onCepasSettlementFail(p0)
            handleSettlementTerminal()
        }

        override fun settlementCompletely(isSuccess: Boolean?) {
            settingResultListener?.settlementCompletely(isSuccess)
        }

        override fun onResetDeviceResponse(p0: Boolean) {
            settingResultListener?.onResetDeviceResponse(p0)
        }

        override fun onLogonResponse(p0: Boolean) {
            settingResultListener?.onLogonResponse(p0)
        }

        override fun onNetsSettlementFail(p0: Int) {
            settingResultListener?.onNetsSettlementFail(p0)
            handleSettlementTerminal()
        }

        override fun callForSupportAndLog(errorCode: Int, errorRes: Int) {
            paymentListener.onUnablePayment(errorCode, errorRes)
            settingResultListener?.callForSupportAndLog(errorCode, errorRes)
        }

        override fun printLog(txt: String?, colorId: Int, gravity: Int) {
            settingResultListener?.printLog(txt, colorId, gravity)
        }

        override fun onBootUpFlowDone() {
            isCheckTerminalStatusAfterBootUp = true
            Handler().postDelayed ({
                getStatusTerminal();
            }, 2000)
        }

        override fun onDeviceStatusResponse(p0: Int) {
            if (isCheckTerminalStatusAfterBootUp) {
                peripheralsPresenter?.initTerminalManagerResult(true)
                isCheckTerminalStatusAfterBootUp = false
            }
            terminalManager?.isCommunicated = true
        }

        override fun onLastTxnResponse(p0: Boolean) {
            receiveLastTxnResponse(p0)
            settingResultListener?.onLastTxnResponse(p0)
        }

        override fun connectResult(isConnected: Boolean?) {
            terminalManager?.isConnectedTerminal = isConnected
            if (true != terminalManager?.isConnectedTerminal) {
                peripheralsPresenter?.initTerminalManagerResult(false)
            }
        }

        override fun onSofListResponse(p0: SOFListResponse?) {
            settingResultListener?.onSofListResponse(p0)
        }

        override fun onCreditSettlementSuccess() {
            settingResultListener?.onCreditSettlementSuccess()
            handleSettlementTerminal()
        }

        override fun onACKTimeout() {
            settingResultListener?.onACKTimeout()
            if (isCheckTerminalStatusAfterBootUp && terminalManager?.isCommunicated != true) {
                peripheralsPresenter?.initTerminalManagerResult(false)
                isCheckTerminalStatusAfterBootUp = false
            }
        }

        override fun onTMSResponse(p0: Boolean) {
            settingResultListener?.onTMSResponse(p0)
        }

        override fun onNetsSettlementSuccess() {
            settingResultListener?.onNetsSettlementSuccess()
            handleSettlementTerminal()
        }

        override fun onFinishWaitLogonAgainTimer() {
            settingResultListener?.onFinishWaitLogonAgainTimer()
        }

    }

    fun init(peripheralsInfo: PeripheralsInfo) {
        if (castleTerminalService == null) {
            terminalManager?.isUArtInterface = false
            castleTerminalService = CastleTerminalService(peripheralsPresenter?.getContext() as MainActivity)
            castleTerminalService?.setSettingsListener(settingCallbacks)
            castleTerminalService?.onResume(peripheralsInfo)
        }
    }

    fun checkInit() {
        if (castleTerminalService == null && true != terminalManager?.isConnectedTerminal) {
            terminalManager?.isFTDI = false
            terminalManager?.isUArtInterface = true
            castleTerminalService = CastleTerminalService(peripheralsPresenter?.getContext() as MainActivity)
            castleTerminalService?.setSettingsListener(settingCallbacks)
            castleTerminalService?.onResume(null)
        }
    }

    fun onDestroy() {
        castleTerminalService?.onPause()
        castleTerminalService?.onDestroy()
        castleTerminalService = null
    }


    private val paymentListener = object : ICustomPaymentCallback {
        override fun writePaymentData(data: BaseMessage?) {
            keepPaymentRequest(data)
        }

        override fun onACKTimeout() {
            /*
            Timeout during ACK
            When the Controller did not receive ACK from the device within the specified time. The
            Controller should prompt the user to check the controller and the device connection. If both are
            connected securely, the user may check whether the device is ready. If issue persists, please call
            for support.
             */
            paymentResultListener?.onUnablePayment(ErrorCode.ERR_NONE, TERMINAL_NOT_RESPONSE_ACK,
                    R.string.can_not_connect_to_a_terminal_for_payment)

        }

        override fun onUnablePayment(libError: Int, resErrorMessage: Int?) {
            paymentResultListener?.onUnablePayment(libError, TERMINAL_NOT_RESPONSE, resErrorMessage)
        }

        override fun onPaymentFail(p0: Int, p1: Int, p2: Int) {
            // Do nothing
            return
        }

        override fun onPaymentFail(p0: TxnDataEntity?, p1: BaseMessage?) {
            val txnData = TransactionResponse.from(p0)
            if (!isReceivedLastTxnResponse) {
                val msgStatus = p1?.header?.msgStatus ?: TERMINAL_NOT_RESPONSE
//                txnData.setErrorCode(msgStatus)
                paymentResultListener?.onPaymentFail(msgStatus.toString(), txnData, p1?.serialize())
            } else {
                checkExactlyLastTxn(txnData, p1)
            }
            resetPaymentFlow()
        }

        override fun onVoidTransactionFail(p0: Int) {
            // Do nothing
            return
        }

        override fun onVoidTransactionSuccess() {
            // Do nothing
            return
        }

        override fun onPaymentACKReceived() {
            castleTerminalService?.setTimerListener(timerListener)
            paymentResultListener?.extendSessionTimeout(PAYMENT_RESPONE_TIME_OUT)
        }

        override fun onPaymentSuccess() {
            // Do nothing
            return
        }

        override fun onPaymentSuccess(p0: TxnDataEntity?, baseMessage: BaseMessage?) {
            val txnData = TransactionResponse.from(p0)

            if (!isReceivedLastTxnResponse) {
                val msgStatus = baseMessage?.header?.msgStatus ?: TERMINAL_NOT_RESPONSE
//                txnData.setErrorCode(msgStatus)
                paymentResultListener?.onPaymentSuccess(msgStatus.toString(), txnData, baseMessage?.serialize())
            } else {
                checkExactlyLastTxn(txnData, baseMessage)
            }
            resetPaymentFlow()
        }

    }

    private val timerListener = object : ICountDownTimer {
        override fun onPendingCountDown(p0: Long) {
            // Do nothing
            return
        }

        override fun onWaitingResponseCountDown(p0: Long) {
            // Do nothing
            return
        }

    }

    private fun resetPaymentFlow() {
        basePaymentRequest = null
        castleTerminalService?.setTimerListener(null)
        castleTerminalService?.setPaymentListener(null)

    }

    fun setCastleTerminalService(castleTerminalService: CastleTerminalService?) {
        this.castleTerminalService = castleTerminalService
    }

    fun setPaymentResultListener(listener: IPaymentResultListener?) {
        this.paymentResultListener = listener
    }

    fun setSettingResultListener(listener: SettingCallbacks?) {
        this.settingResultListener = listener
    }


    private var basePaymentRequest: BaseMessage? = null

    // Implement upt1000f payment
    fun upt1000fPayment(cardType: Int, amount: Int) {
        isReceivedLastTxnResponse = false
        this.castleTerminalService?.setPaymentListener(paymentListener)
        var type = MessageTag.PAYMENT_EZ_LINK
        when (cardType) {
            PaymentFragment.CARD_CEPAS -> {
                type = MessageTag.PAYMENT_EZ_LINK
            }
            PaymentFragment.CARD_NETS_FLASH -> {
                type = MessageTag.PAYMENT_NFP
            }
            PaymentFragment.CARD_CREDIT_CONTACTLESS -> {
                type = MessageTag.PAYMENT_CREDIT_CARD
            }
            PaymentFragment.CARD_NETS_DEBIT -> {
                type = MessageTag.PAYMENT_NETS_DEBIT
            }
        }
        val message: PaymentMessage = MessageFactory.newInstance(type)
        message.setTxnAmount(amount)

        retryTime = 0
        isSuccess = false

        retryPayment(message)
    }

    private var retryTime = 0
    private val RETRY_TIME_MAX = 3
    private var isSuccess = false
    private fun retryPayment(paymentMessage: PaymentMessage) {
        if (retryTime >= RETRY_TIME_MAX) {
            paymentResultListener?.onUnablePayment(ErrorCode.ERR_NONE, TerminalErrorCode.TERMINAL_BUSY, R.string.common_error)
            return
        }
        isSuccess = castleTerminalService?.sendMessageToTerminal(paymentMessage) ?: false
        retryTime++
        if (!isSuccess) {
            paymentResultListener?.extendSessionTimeout(PAYMENT_RETRY_EXTEND)
            Handler().postDelayed({
                retryPayment(paymentMessage)
            }, PAYMENT_RETRY_EXTEND * 1000L)
        }
    }

    private fun keepPaymentRequest(msgData: BaseMessage?) {
        if (isSuccess && msgData != null && msgData.header != null) {
            if (MsgType.MSG_TYPE_PAYMENT == msgData.header.msgType) {
                basePaymentRequest = msgData
            } else {
                if (MsgCode.MSG_CODE_DEVICE_STATUS == msgData.header.msgCode) {
                    paymentResultListener?.changePaymentProgressStatus(PaymentFragment.DEVICE_STATUS_PROCESS)
                } else if (MsgCode.MSG_CODE_DEVICE_RETRIEVE_LAST_TRANSACTION_STATUS == msgData.header.msgCode) {
                    paymentResultListener?.changePaymentProgressStatus(PaymentFragment.LAST_TXN_PROCESS)
                }
            }
        }
    }

    private var isReceivedLastTxnResponse = false
    fun receiveLastTxnResponse(p0: Boolean) {
        isReceivedLastTxnResponse = true
    }

    private fun getLastTxnHeader(lastTxn: BaseMessage): MessageHeader? {
        val fieldList = lastTxn.payload?.fieldList
        if (!fieldList.isNullOrEmpty()) {
            for (field in fieldList) {
                if (field != null && Field.ID_TXN_LAST_HEADER == field.id) {
                    val messageHeader = MessageHeader()
                    messageHeader.deserialize(field.data)
                    return messageHeader
                }
            }
        }
        return null
    }

    private fun checkExactlyLastTxn(txnData: TransactionResponse, baseMessage: BaseMessage?) {
        if (baseMessage != null) {
            val lastTxnHeader = getLastTxnHeader(baseMessage)
            if (lastTxnHeader != null && basePaymentRequest != null &&
                    lastTxnHeader.msgType == basePaymentRequest?.header?.msgType &&
                    lastTxnHeader.msgCode == basePaymentRequest?.header?.msgCode &&
                    lastTxnHeader.msgSequence == basePaymentRequest?.header?.msgSequence) {
                val msgStatus = lastTxnHeader.msgStatus
//                txnData.setErrorCode(msgStatus)
                if (MsgStatus.SUCCESS == lastTxnHeader.msgStatus) {
                    paymentResultListener?.onPaymentSuccess(msgStatus.toString(), txnData, baseMessage.serialize())
                } else {
                    paymentResultListener?.onPaymentFail(msgStatus.toString(), txnData, baseMessage.serialize())
                }
            } else {
                paymentResultListener?.onUnablePayment(TERMINAL_NOT_RESPONSE, TERMINAL_NOT_RESPONSE, R.string.common_error)
            }
        } else {
            paymentResultListener?.onUnablePayment(TERMINAL_NOT_RESPONSE, TERMINAL_NOT_RESPONSE, R.string.common_error)
        }
    }


    fun logOnTerminal(): Boolean {
        return castleTerminalService?.requestLogon() ?: false
    }

    fun resetTerminal(): Boolean {
        return castleTerminalService?.requestResetDevice() ?: false
    }

    fun resetSequenceNumberTerminal(): Boolean {
        return castleTerminalService?.requestResetSequenceNumber() ?: false
    }

    fun getStatusTerminal(): Boolean {
        return castleTerminalService?.requestDeviceStatus() ?: false
    }

    fun requestSofList(): Boolean {
        return castleTerminalService?.requestSofList() ?: false
    }

    fun setSofPriority(sofList: List<SOFItem>): Boolean {
        val sofMsg: SOFMessage = MessageFactory.newInstance(MessageTag.DEVICE_SOF_PRIORITY)
        sofMsg.setSofList(sofList)
        return castleTerminalService?.sendMessageToTerminal(sofMsg) ?: false
    }

    private var cardTypeCounter = -1
    private var listCardType: ArrayList<String>? = null
    private var hasSuccess = false
    fun settlementTerminal(listCardType: ArrayList<String>): Boolean {
        cardTypeCounter = -1
        this.listCardType = listCardType
        handleSettlementTerminal()

        return true
    }

    fun handleSettlementTerminal() {
        cardTypeCounter++
        if (cardTypeCounter < listCardType?.size ?: 0) {
            val cardType = listCardType?.get(cardTypeCounter)
            when (cardType) {
                CARD_NETS -> {
                    val isSuccess = castleTerminalService?.requestNetsSettlement()
                    if (false == isSuccess) {
                        handleSettlementTerminal()
                    } else {
                        hasSuccess = true
                    }
                }

                CARD_CREDIT -> {
                    val isSuccess = castleTerminalService?.requestCreditSettlement()
                    if (false == isSuccess) {
                        handleSettlementTerminal()
                    } else {
                        hasSuccess = true
                    }
                }

                CARD_EZLINK -> {
                    val isSuccess = castleTerminalService?.requestCepasSettlement()
                    if (false == isSuccess) {
                        handleSettlementTerminal()
                    } else {
                        hasSuccess = true
                    }
                }
            }
        } else {
            settingResultListener?.settlementCompletely(hasSuccess)
        }
    }

    fun updateTerminalFirmware(): Boolean {
        return castleTerminalService?.requestTmsOTA() ?: false
    }

    fun getLastTxnTerminal(): Boolean {
        return castleTerminalService?.requestLastTransactionStatus() ?: false
    }
}