package com.styl.pa.database

import android.os.Handler
import android.os.HandlerThread

/**
 * Created by trangpham on 10/13/2018
 */
class DbWorkerThread(threadName: String) : HandlerThread(threadName) {

    private var workerHandler: Handler? = null

    fun setWorkerHandler(handler: Handler) {
        this.workerHandler = handler
    }

    override fun onLooperPrepared() {
        super.onLooperPrepared()

        if (workerHandler == null) {
            workerHandler = Handler(looper)
        }
    }

    fun postTask(task: Runnable) {
        workerHandler?.post(task)
    }
}