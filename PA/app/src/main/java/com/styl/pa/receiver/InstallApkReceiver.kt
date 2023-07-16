package com.styl.pa.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.styl.kioskcore.SystemManager
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.utils.MySharedPref

/**
 * Created by NguyenHang on 9/8/2020.
 */

@ExcludeFromJacocoGeneratedReport
class InstallApkReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_PACKAGE_REPLACE = "android.intent.action.PACKAGE_REPLACED"
        const val ACTION_MY_PACKAGE_REPLACE = "android.intent.action.MY_PACKAGE_REPLACED"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let {
            val systemManager = SystemManager.init(it.applicationContext)
            if (p1?.action == ACTION_PACKAGE_REPLACE) {
                val packageName = p1.data
                if (packageName?.toString().equals("package:" + p0.packageName)) {
                    MySharedPref(p0).isUpgradeAppSuccess = true
                    Handler().postDelayed({
                        systemManager.reboot("")
                    }, 700L)
                }
            } else if (p1?.action == ACTION_MY_PACKAGE_REPLACE) {
                MySharedPref(p0).isUpgradeAppSuccess = true
                Handler().postDelayed({
                    systemManager.reboot("")
                }, 700L)
            }
        }
    }

}
