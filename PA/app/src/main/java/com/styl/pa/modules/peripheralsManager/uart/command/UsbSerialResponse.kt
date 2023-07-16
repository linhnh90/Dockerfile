package com.styl.pa.modules.peripheralsManager.uart.command

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.styl.pa.modules.peripheralsManager.uart.config.UartConfig
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.collections.ArrayList

class UsbSerialResponse {

    private var timer: CountDownTimer? = null
    private var buffers = ArrayList<Byte>()
    private var listenerError: PublishSubject<String>? = null
    private var listener: PublishSubject<ByteArray>? = null

    fun setListener(listenerError: PublishSubject<String>, listener: PublishSubject<ByteArray>) {
        this.listenerError = listenerError
        this.listener = listener
    }

    @Throws(ConcurrentModificationException::class)
    fun onHandleUsbResponse(data: ByteArray?): ByteArray? {
        var mData: ByteArray? = null
        if (data != null) {
            if (buffers.size == 0) {
                startTimer()
            }

            buffers.addAll(data.toList())

            if (checkReceiveEnoughData()) {
                mData = buffers.toByteArray()
                resetData()
            }
        }

        return mData
    }

    private fun checkReceiveEnoughData(): Boolean {
        if (buffers.size > 2) {
//            val lengthData = (buffers.get(1).toInt() shl 8) + buffers.get(2).and(0xFF.toByte()) - 4
            val lengthData = (buffers.get(1).toInt() shl 8) + (buffers.get(2).toInt() and 0xff) - 4
            if (buffers.size == (7 + lengthData)) {
                return true
            }
        }

        return false
    }

    private fun resetData() {
        stopTimer()
        buffers.clear()
    }

    private fun startTimer() {

        Handler(Looper.getMainLooper()).post {
            timer = object : CountDownTimer(UartConfig.DELAY_TIME_RECEIVER, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    // Do nothing
                    return
                }

                override fun onFinish() {
                    if (!checkReceiveEnoughData()) {
                        listenerError?.onNext("Error: time out")
                        Log.d("log_fuji", "onError : time out")
                    } else {
                        var data = buffers.toByteArray()
                        listener?.onNext(data)
                    }

                    resetData()
                }

            }
            timer?.start()
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }
}