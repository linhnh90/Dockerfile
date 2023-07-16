package com.styl.pa.modules.scanner.zebraScannerService

import com.styl.pa.entities.scanner.Barcode
import com.zebra.scannercontrol.DCSScannerInfo

/**
 * Created by Ngatran on 09/17/2018.
 */
interface DcssdkListener {
    interface DcssdkInit {
        fun dcssdkAppeareEvent(usbDevice: DCSScannerInfo)

        fun dcssdkDisappeareEvent()

        fun dcssdkConnectEvent(result: Boolean)

        fun dcssdkDisconnectEvent(result: Boolean)

        fun notFoundScannerUsb()
    }

    interface DcssdkConfig {
        fun dcssdkBarCodeResultEvent(barcode: Barcode?)

        fun dcssdkPullTriggerEvent(result: Boolean)

        fun dcssdkConfigAIM(result: Boolean)
    }

    interface Init {
        fun setDcssdkInit(dcssdkInit: DcssdkInit)

        fun setDcssdkConfig(dcssdkConfig: DcssdkConfig)
    }
}