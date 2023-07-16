package com.styl.pa.modules.scanner.zebraScannerService

import android.os.Handler
import android.os.Message
import com.styl.pa.entities.scanner.Barcode
import com.styl.pa.utils.LogManager
import com.zebra.scannercontrol.DCSScannerInfo

/**
 * Created by Ngatran on 09/17/2018.
 */
class DcssdkHandle {
    private var iDcssdkInit: DcssdkListener.DcssdkInit? = null
    private var iDcssdkConfig: DcssdkListener.DcssdkConfig? = null
    private var iInitEvent: DcssdkListener.Init? = null

    fun setDcssdkInit(listener: DcssdkListener.DcssdkInit?) {
        this.iDcssdkInit = listener
    }

    fun setDcssdkConfig(listener: DcssdkListener.DcssdkConfig) {
        this.iDcssdkConfig = listener
    }

    fun setInit(listener: DcssdkListener.Init) {
        this.iInitEvent = listener
    }

    private var dcssdkHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg!!.what) {
                DcsskApiTypes.SCANNER_APPEARED -> {
                    var usbDevice = msg.obj as DCSScannerInfo
                    if (iDcssdkInit != null) {
                        iDcssdkInit?.dcssdkAppeareEvent(usbDevice)
                    }
                }
                DcsskApiTypes.SCANNER_CONNECTED -> {
                    var isConnected = msg.obj as Boolean
                    if (iDcssdkInit != null) {
                        iDcssdkInit!!.dcssdkConnectEvent(isConnected)
                    }

                }
                DcsskApiTypes.SCANNER_DISCONNECTED -> {
                    var isDisconnected = msg.obj as Boolean
                    if (iDcssdkInit != null)
                        iDcssdkInit!!.dcssdkDisconnectEvent(isDisconnected)
                }

                DcsskApiTypes.SCANNER_DISAPPEARED -> {
                    if (iDcssdkInit != null)
                        iDcssdkInit!!.dcssdkDisappeareEvent()
                }

                DcsskApiTypes.SCANNER_BARCODE_RECEIVE -> {
                    var barcode = msg.obj as Barcode
                    if (iDcssdkConfig != null) {
                        iDcssdkConfig!!.dcssdkBarCodeResultEvent(barcode!!)
                    }
                }

                DcsskApiTypes.TRIGGER_CONFIGURATION -> {
                    var result = msg.obj as Boolean
                    if (iDcssdkConfig != null) {
                        iDcssdkConfig!!.dcssdkPullTriggerEvent(result)
                    }
                }

                DcsskApiTypes.AIM_CONFIGURATION -> {
                    var result = msg.obj as Boolean
                    if (iDcssdkConfig != null) {
                        iDcssdkConfig!!.dcssdkConfigAIM(result)
                    }
                }

                else -> {
                    LogManager.d("dcssdkHandler")
                }

            }
        }
    }

    fun getDcssdkHandler(): Handler {
        return dcssdkHandler
    }
}