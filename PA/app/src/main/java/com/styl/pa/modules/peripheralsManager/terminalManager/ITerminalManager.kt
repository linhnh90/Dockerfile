package com.styl.pa.modules.peripheralsManager.terminalManager

import android.hardware.usb.UsbDevice
import com.styl.castle_terminal_upt1000_api.message.device.SOFItem
import com.styl.pa.modules.peripheralsManager.peripheralsService.PeripheralsInfo
import com.styl.pa.modules.terminal.upt1000fterminal.SettingCallbacks

/**
 * Created by NgaTran on 10/2/2020.
 */
interface ITerminalManager {
    fun initTerminalService(peripheralsInfo: PeripheralsInfo)
    fun initTerminalService(usbDevice: UsbDevice)
    fun checkInitTerminalService()
    fun isTerminalConnected(): Boolean
    fun isTerminalCommunicated(): Boolean
    fun setPaymentCallbacks(listener: IPaymentResultListener?)
    fun payProduct(cardType: Int, amount: Int): Boolean
    fun setSettingResultListener(settingResultListener: SettingCallbacks?)
    fun logOnTerminal(): Boolean
    fun resetTerminal(): Boolean
    fun resetSequenceNumberTerminal(): Boolean
    fun getStatusTerminal(): Boolean
    fun settlementTerminal(listCardType: ArrayList<String>): Boolean
    fun updateTerminalFirmware(): Boolean
    fun getLastTxnTerminal(): Boolean
    fun detachTerminalUsb()
    fun disconnectTerminalService()
    fun requestSofList(): Boolean
    fun setSofPriority(sofList: List<SOFItem>): Boolean
}