package com.styl.pa

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * Created by trangpham on 9/22/2018
 */
@ExcludeFromJacocoGeneratedReport
class MyApplication : Application() {

    @ExcludeFromJacocoGeneratedReport
    companion object {

        var instance: MyApplication? = null

        fun getAppInstance(): MyApplication? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}