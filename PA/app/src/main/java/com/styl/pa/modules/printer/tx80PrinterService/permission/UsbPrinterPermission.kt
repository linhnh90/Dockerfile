package com.styl.pa.modules.printer.tx80PrinterService.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by Nga Tran on 8/5/2020.
 */
class UsbPrinterPermission {
    private val REQUIRED_PERMISSION_LIST =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_CODE = 1405

    private var usbPermissionCallback: IUsbPermissionCallback? = null
    private var isForce = false
    private val missPermission: ArrayList<String> = ArrayList()

    constructor() {}

    constructor(usbPermissionCallback: IUsbPermissionCallback?) {
        this.usbPermissionCallback = usbPermissionCallback
    }

    constructor(
            isForce: Boolean,
            usbCameraPermissionCallback: IUsbPermissionCallback?
    ) : this(usbCameraPermissionCallback) {
        this.isForce = isForce
    }

    companion object {
        private var instances: UsbPrinterPermission? = null
        fun newInstances(
                isForce: Boolean,
                usbCameraPermissionCallback: IUsbPermissionCallback?
        ): UsbPrinterPermission? {
            if (instances == null) {
                instances = UsbPrinterPermission(isForce, usbCameraPermissionCallback)
            }
            return instances
        }

        fun newInstances(usbCameraPermissionCallback: IUsbPermissionCallback?): UsbPrinterPermission? {
            if (instances == null) {
                instances = UsbPrinterPermission(usbCameraPermissionCallback)
            }
            return instances
        }

        fun newInstances(): UsbPrinterPermission? {
            if (instances == null) {
                instances = UsbPrinterPermission()
            }
            return instances
        }
    }

    private var baseActivity: Activity? = null

    fun isVersionM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun checkPermission(activity: Activity): Boolean {
        missPermission.clear()
        if (isVersionM()) {
            for (permission in REQUIRED_PERMISSION_LIST) {
                val result = ContextCompat.checkSelfPermission(activity, permission)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    missPermission.add(permission)
                }
            }

            if (missPermission.isNotEmpty()) {
                return false
            }
        }

        return true
    }

    fun onRequestPermissionsResult(
            activity: Activity,
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ): Boolean? {
        if (requestCode == REQUEST_CODE) {
            for (itemIndex in (grantResults.size - 1) downTo 0 step 1) {
                if (grantResults[itemIndex] == PackageManager.PERMISSION_GRANTED) {
                    missPermission.remove(permissions[itemIndex])
                }
            }

            if (missPermission.isNotEmpty()) {
                if (isForce) {
                    requestPermission(activity)
                } else {
                    backBaseActivity(activity, false)
                }
                return false
            } else {
                backBaseActivity(activity, true)
                return true
            }
        }

        return true
    }

    fun checkStartPermissionActivity(activity: Activity) {
        if (!checkPermission(activity)) {
            this.baseActivity = activity
            val intent = Intent(activity, PermissionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        } else {
            usbPermissionCallback?.onUsbCameraPermissionResult(true)
        }
    }

    fun requestPermission(activity: Activity) {
        if (!checkPermission(activity)) {
            ActivityCompat.requestPermissions(
                    activity, missPermission.toTypedArray(), REQUEST_CODE
            )
        } else {
            backBaseActivity(activity, true)
        }
    }

    fun backBaseActivity(activity: Activity, isComplete: Boolean) {
        baseActivity?.let {
            val intent = Intent(activity, it::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
            activity.finish()
        }
        usbPermissionCallback?.onUsbCameraPermissionResult(isComplete)
    }

    fun destroyUsbPrinterPermission() {
        instances = null
    }

    interface IUsbPermissionCallback {
        fun onUsbCameraPermissionResult(isAllow: Boolean)
    }
}