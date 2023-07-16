package com.styl.pa.utils

import android.annotation.SuppressLint
import android.util.Log
import com.styl.pa.BuildConfig
import com.styl.pa.modules.peripheralsManager.GeneralUtils
import com.styl.pa.ExcludeFromJacocoGeneratedReport


@ExcludeFromJacocoGeneratedReport
@SuppressLint("LogNotTimber")
class LogManager {

    @ExcludeFromJacocoGeneratedReport
    companion object {

        private const val TAG = "eKiosk.log"

        fun v(tag: String, message: String) {
            Log.v("${GeneralUtils.getCurrentTimeLog()} $tag", message)
        }

        fun d(message: String) {
            if (BuildConfig.DEBUG) {
                Log.d("${GeneralUtils.getCurrentTimeLog()} $TAG", message)
            }
        }

        fun d(tag: String, message: String) {
            if (BuildConfig.DEBUG) {
                Log.d("${GeneralUtils.getCurrentTimeLog()} $tag", message)
            }
        }

        fun i(message: String?) {
            Log.i("${GeneralUtils.getCurrentTimeLog()} $TAG", message)
        }

        fun i(tag: String, message: String?) {
            Log.i("${GeneralUtils.getCurrentTimeLog()} $tag", message)
        }

    }
}