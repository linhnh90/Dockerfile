package com.styl.pa.modules.scanner.zebraScannerService

import android.content.Context
import com.styl.pa.utils.LogManager
import com.zebra.scannercontrol.DCSScannerInfo
import com.zebra.scannercontrol.FirmwareUpdateEvent
import com.zebra.scannercontrol.IDcsSdkApiDelegate
import com.zebra.scannercontrol.SDKHandler

/**
 * Created by Ngatran on 09/17/2018.
 */
class ScannerService : IDcsSdkApiDelegate {

    companion object {
        private val TAG = ScannerService::class.java.simpleName
    }

    private lateinit var sdkHandler: SDKHandler
    private lateinit var settingScanner: SettingScanner

    fun initService(activity: Context?, listener: DcssdkListener.DcssdkInit?) {
        sdkHandler = SDKHandler(activity)
        sdkHandler.dcssdkSetDelegate(this)

        settingScanner = SettingScanner(sdkHandler)
        setDcssdkInitEvent(listener)
        settingScanner.configSDKHandler()
    }

    fun setDcssdkInitEvent(listener: DcssdkListener.DcssdkInit?) {
        settingScanner.setHandleDcssdkInit(listener)
    }

    fun setDcssdkConfigEvent(listener: DcssdkListener.DcssdkConfig) {
        settingScanner.setHandleDcssdkConfig(listener)
    }

    fun pullTrigger(isPull: Boolean) {
        settingScanner.pullTrigger(isPull)
    }

    fun aimConfiguration(isOn: Boolean) {
        settingScanner.aimConfig(isOn)
    }

    fun disconnectScanner() {
        settingScanner.disconnectScanner()
    }

    //dcssdk
    override fun dcssdkEventFirmwareUpdate(p0: FirmwareUpdateEvent?) {
        LogManager.d(TAG, "dcssdkEventFirmwareUpdate")
    }

    override fun dcssdkEventScannerAppeared(p0: DCSScannerInfo?) {
        settingScanner.addUSBDevice(p0)
    }

    override fun dcssdkEventCommunicationSessionTerminated(p0: Int) {
        settingScanner.autoCommunicationSessionTerminated(p0)
    }

    override fun dcssdkEventCommunicationSessionEstablished(p0: DCSScannerInfo?) {
        settingScanner.communicationSessionEstablished(p0!!)
    }

    override fun dcssdkEventVideo(p0: ByteArray?, p1: Int) {
        LogManager.d(TAG, "dcssdkEventVideo")
    }

    override fun dcssdkEventAuxScannerAppeared(p0: DCSScannerInfo?, p1: DCSScannerInfo?) {
        LogManager.d(TAG, "dcssdkEventAuxScannerAppeared")
    }

    override fun dcssdkEventBarcode(p0: ByteArray?, p1: Int, p2: Int) {
        settingScanner.receiverBarcodeEvent(p0, p1, p2)
    }

    override fun dcssdkEventScannerDisappeared(p0: Int) {
        settingScanner.scannerDisappeared()
    }

    override fun dcssdkEventImage(p0: ByteArray?, p1: Int) {
        LogManager.d(TAG, "dcssdkEventImage")
    }
}