package com.styl.pa.modules.printer.tx80PrinterService.permission

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager

/**
 * Created by Nga Tran on 8/5/2020.
 */
class PermissionActivity : Activity() {
    private var usbPrinterPermission: UsbPrinterPermission? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        usbPrinterPermission = UsbPrinterPermission.newInstances()
        usbPrinterPermission?.requestPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        usbPrinterPermission?.onRequestPermissionsResult(
            this,
            requestCode,
            permissions,
            grantResults
        )
    }
}