package com.styl.pa.modules.scanner.zebraScannerService

import com.styl.pa.entities.scanner.Barcode
import com.styl.pa.utils.LogManager
import com.zebra.scannercontrol.DCSSDKDefs
import com.zebra.scannercontrol.DCSScannerInfo
import com.zebra.scannercontrol.SDKHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by Ngatran on 09/17/2018.
 */
class SettingScanner {
    private var sdkHandler: SDKHandler? = null
    private var notifications_mask = 0
    private var listUSBDevice: ArrayList<DCSScannerInfo> = ArrayList()
    private var dcssdkHandle: DcssdkHandle = DcssdkHandle()

    constructor(sdkHandler: SDKHandler?) {
        this.sdkHandler = sdkHandler
    }

    fun setHandleDcssdkInit(listener: DcssdkListener.DcssdkInit?) {
        dcssdkHandle.setDcssdkInit(listener)
    }

    fun setHandleDcssdkConfig(listener: DcssdkListener.DcssdkConfig) {
        dcssdkHandle.setDcssdkConfig(listener)
    }

    fun configSDKHandler() {
        registerNotification()
        listenEventScannerAppeared()
    }

    fun removeConfigSDKHandler() {
        unRegisterNotification()
        enableAvailableScannersDetection(false)
        setOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_DISABLED)
    }

    private fun registerNotification() {
        notifications_mask = notifications_mask or (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value or DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value)
        notifications_mask = notifications_mask or (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value or DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value)
        notifications_mask = notifications_mask or DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value

        sdkHandler?.dcssdkSubsribeForEvents(notifications_mask)
    }

    private fun unRegisterNotification() {
        sdkHandler?.dcssdkUnsubsribeForEvents(notifications_mask)
    }

    private fun enableAvailableScannersDetection(boolean: Boolean) {
        sdkHandler?.dcssdkEnableAvailableScannersDetection(boolean)
    }

    private fun setOperationalMode(mode: DCSSDKDefs.DCSSDK_MODE) {
        sdkHandler?.dcssdkSetOperationalMode(mode)
    }

    private fun listenEventScannerAppeared() {
        enableAvailableScannersDetection(true)
        setOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI)
    }

    fun addUSBDevice(usbDevice: DCSScannerInfo?) {
        if (usbDevice != null &&
                usbDevice.connectionType == DCSSDKDefs.DCSSDK_CONN_TYPES.DCSSDK_CONNTYPE_USB_SNAPI) {

            dcssdkHandle.getDcssdkHandler().obtainMessage(DcsskApiTypes.SCANNER_APPEARED, usbDevice).sendToTarget()

            var isExist = false

            for (i in 0..(listUSBDevice.size - 1)) {
                if (listUSBDevice.get(i).scannerID == usbDevice.scannerID && listUSBDevice.get(i).scannerName.equals(usbDevice.scannerName)) {
                    isExist = true
                    break
                }
            }

            if (!isExist) {
                listUSBDevice.add(usbDevice)
            }
        }

        writeCommandConnect()
    }

    private fun writeCommandConnect() {
        if (listUSBDevice.size > 0) {
            commandRequest(listUSBDevice.get(0).scannerID, DcsskApiTypes.SCANNER_CONNECTED, null)
        }
    }

    fun communicationSessionEstablished(p0: DCSScannerInfo): Boolean {
        val result: DCSSDKDefs.DCSSDK_RESULT
        if (sdkHandler != null) {
            result = sdkHandler!!.dcssdkEnableAutomaticSessionReestablishment(true, p0!!.scannerID)
        } else {
            result = DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE
        }

        return (result != DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
    }

    fun autoCommunicationSessionTerminated(p0: Int): Boolean {
        val result: DCSSDKDefs.DCSSDK_RESULT
        if (sdkHandler != null) {
            result = sdkHandler!!.dcssdkEnableAutomaticSessionReestablishment(true, p0)
        } else {
            result = DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE
        }

        return (result != DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
    }

    fun scannerDisappeared(){
        if (listUSBDevice.size > 0) {
            commandRequest(listUSBDevice.get(0).scannerID, DcsskApiTypes.SCANNER_DISAPPEARED, null)
        }
    }

    fun receiverBarcodeEvent(p0: ByteArray?, p1: Int, p2: Int) {
        val barcode = Barcode(p0, p1, p2)
        dcssdkHandle.getDcssdkHandler().obtainMessage(DcsskApiTypes.SCANNER_BARCODE_RECEIVE, barcode).sendToTarget()
    }

    fun pullTrigger(isPull: Boolean) {
        if (listUSBDevice.size > 0) {
            var opCode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_PULL_TRIGGER

            if (isPull) {
                opCode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_PULL_TRIGGER
            } else {
                opCode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_RELEASE_TRIGGER
            }
            commandRequest(listUSBDevice.get(0).scannerID, DcsskApiTypes.TRIGGER_CONFIGURATION, opCode)
            LogManager.i("Scanner", "Zebra trigger successful");
        }
    }

    fun aimConfig(isOn: Boolean) {
        if (listUSBDevice.size > 0) {
            var opCode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_AIM_OFF

            if (isOn) {
                opCode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_AIM_ON
            } else {
                opCode = DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_AIM_OFF
            }

            commandRequest(listUSBDevice.get(0).scannerID, DcsskApiTypes.AIM_CONFIGURATION, opCode)
        }
    }

    private fun connect(scannerID: Int): Boolean {
        val result: DCSSDKDefs.DCSSDK_RESULT

        if (sdkHandler != null) {
            result = sdkHandler!!.dcssdkEstablishCommunicationSession(scannerID)
        } else {
            result = DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE
        }

        return (result != DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
    }

    private fun disconnect(scannerID: Int): Boolean {

        var result: DCSSDKDefs.DCSSDK_RESULT

        if (sdkHandler != null) {
            result = sdkHandler!!.dcssdkTerminateCommunicationSession(scannerID)
        } else {
            result = DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE
        }

        if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE) {
            return false
        } else {
            result = sdkHandler!!.dcssdkEnableAutomaticSessionReestablishment(true, scannerID)
        }

        return (result != DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
    }

    fun disconnectScanner() {
        if (listUSBDevice.size > 0) {
            sdkHandler!!.dcssdkTerminateCommunicationSession(listUSBDevice.get(0).scannerID)
            sdkHandler!!.dcssdkEnableAutomaticSessionReestablishment(false, listUSBDevice.get(0).scannerID)

//            commandRequest(listUSBDevice.get(0).scannerID, DcsskApiTypes.SCANNER_DISCONNECTED, null)

            listUSBDevice.clear()
        }
        sdkHandler?.dcssdkUnsubsribeForEvents(notifications_mask)
        sdkHandler?.dcssdkClose()
    }

    private fun executeCommand(opCode: DCSSDKDefs.DCSSDK_COMMAND_OPCODE, outXML: StringBuilder, scannerID: Int): Boolean {
        val inXML = "<inArgs><scannerID>$scannerID</scannerID></inArgs>"
        var outXML = outXML

        if (sdkHandler != null) {

            if (outXML == null) {
                outXML = StringBuilder()
            }

            val result = sdkHandler!!.dcssdkExecuteCommandOpCodeInXMLForScanner(opCode, inXML, outXML, scannerID)

            if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS)
                return true
            else if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE) {
                return false
            }
        }
        return false
    }

    private fun commandRequest(scannerID: Int, dcssdkApiTypes: Int, opCode: DCSSDKDefs.DCSSDK_COMMAND_OPCODE?) {
        Observable.just(scannerID)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map {
                    when (dcssdkApiTypes) {
                        DcsskApiTypes.SCANNER_CONNECTED -> {
                            connect(scannerID)
                        }
                        DcsskApiTypes.SCANNER_DISCONNECTED -> {
                            disconnectScanner()
                        }
                        DcsskApiTypes.SCANNER_DISAPPEARED -> {
                            disconnect(scannerID)
                        }
                        DcsskApiTypes.TRIGGER_CONFIGURATION,
                        DcsskApiTypes.AIM_CONFIGURATION -> {
                            executeCommand(opCode!!, StringBuilder(), scannerID)
                        }
                        else -> {
                            LogManager.d("dcssdkApiTypes = ${dcssdkApiTypes}")
                        }
                    }
                }
                .subscribe(
                        { result ->
                            dcssdkHandle!!.getDcssdkHandler().obtainMessage(dcssdkApiTypes, result).sendToTarget()
                        },
                        { error ->
                            dcssdkHandle!!.getDcssdkHandler().obtainMessage(dcssdkApiTypes, false).sendToTarget()
                        }
                )
    }
}