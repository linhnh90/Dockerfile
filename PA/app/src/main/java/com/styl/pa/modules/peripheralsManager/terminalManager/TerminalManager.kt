package com.styl.pa.modules.peripheralsManager.terminalManager

import android.hardware.usb.UsbDevice
import com.styl.castle_terminal_upt1000_api.message.device.SOFItem
import com.styl.pa.modules.main.IMainContract
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.terminal.upt1000fterminal.SettingCallbacks
import com.styl.pa.utils.LogManager

/**
 * Created by NgaTran on 10/2/2020.
 */
class TerminalManager : ITerminalManager {
    private var peripheralsPresenter: IMainContract.IPeripheralPresenter? = null
    private var upT1000FUtils: UPT1000FUtils? = null
    private var paymentResultListener: IPaymentResultListener? = null

    constructor(peripheralsPresenter: IMainContract.IPeripheralPresenter?) {
        this.peripheralsPresenter = peripheralsPresenter
        if (upT1000FUtils == null) {
            upT1000FUtils = UPT1000FUtils(peripheralsPresenter, this)
        }
    }


    var isFTDI: Boolean? = null
    var isUArtInterface: Boolean? = null
    var isConnectedTerminal: Boolean? = null
    var isCommunicated: Boolean? = null

    override fun initTerminalService(peripheralsInfo: PeripheralsInfo) {
            isFTDI = false
            upT1000FUtils?.init(peripheralsInfo)
    }

    override fun initTerminalService(usbDevice: UsbDevice) {
        LogManager.d("initTerminalService: pid = ${usbDevice.productId}, vid = ${usbDevice.vendorId}")
    }

    override fun checkInitTerminalService() {
        upT1000FUtils?.checkInit()
    }

    override fun isTerminalConnected(): Boolean {
        return isConnectedTerminal ?: false
    }

    override fun isTerminalCommunicated(): Boolean {
        return isCommunicated ?: false
    }

    override fun payProduct(cardType: Int, amount: Int): Boolean {
        if (false == isFTDI) {
            upT1000FUtils?.upt1000fPayment(cardType, amount)
            return true
        }
        return false
    }

    override fun setPaymentCallbacks(listener: IPaymentResultListener?) {
        this.paymentResultListener = listener
        upT1000FUtils?.setPaymentResultListener(listener)
    }


    override fun setSettingResultListener(settingResultListener: SettingCallbacks?) {
        upT1000FUtils?.setSettingResultListener(settingResultListener)
    }

    override fun logOnTerminal(): Boolean {
        return upT1000FUtils?.logOnTerminal() ?: false
    }

    override fun resetTerminal(): Boolean {
        return upT1000FUtils?.resetTerminal() ?: false
    }

    override fun resetSequenceNumberTerminal(): Boolean {
        return upT1000FUtils?.resetSequenceNumberTerminal() ?: false
    }

    override fun getStatusTerminal(): Boolean {
        return upT1000FUtils?.getStatusTerminal() ?: false
    }

    override fun settlementTerminal(listCardType: ArrayList<String>): Boolean {
        return upT1000FUtils?.settlementTerminal(listCardType) ?: false
    }

    override fun updateTerminalFirmware(): Boolean {
        return upT1000FUtils?.updateTerminalFirmware() ?: false
    }

    override fun getLastTxnTerminal(): Boolean {
        return upT1000FUtils?.getLastTxnTerminal() ?: false
    }

    override fun detachTerminalUsb() {
        isConnectedTerminal = false
        isCommunicated = false
        upT1000FUtils?.onDestroy()
    }

    override fun disconnectTerminalService() {
        isConnectedTerminal = false
        isCommunicated = false
        upT1000FUtils?.onDestroy()
        upT1000FUtils = null
    }

    override fun requestSofList(): Boolean {
        return upT1000FUtils?.requestSofList() ?: false
    }

    override fun setSofPriority(sofList: List<SOFItem>): Boolean {
        return upT1000FUtils?.setSofPriority(sofList) ?: false
    }
}