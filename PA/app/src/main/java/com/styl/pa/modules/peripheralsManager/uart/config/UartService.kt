package com.styl.pa.modules.peripheralsManager.uart.config

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import com.styl.pa.MyApplication
import com.styl.pa.modules.peripheralsManager.GeneralUtils
import com.styl.pa.modules.peripheralsManager.uart.listener.IUartService
import com.styl.pa.modules.peripheralsManager.uart.listener.UartListener
import java.io.File

class UartService : IUartService, Runnable {

    private var fd: Int = -1
    private var blockModel = UartConfig.NON_BLOCK
    private var listener: UartListener? = null
    private val MESSAGE_LOG = 1
    private var mThread: Thread? = null
    private var timeoutRead = UartConfig.TIME_OUT_READ

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    companion object {
        init {
            val nativeLibPath = MyApplication.getAppInstance()?.applicationInfo?.nativeLibraryDir
            val nativeLibDir = File(nativeLibPath)
            if (nativeLibDir.exists()) {
                System.load("${nativeLibDir.absolutePath}/libstyl_terminal.so")
            }
        }
    }

    override fun setListener(listener: UartListener) {
        this.listener = listener
    }

    override fun setReadTimeout(timeout: Int) {
        timeoutRead = timeout
    }

    override fun openPort(uartName: String, baudrate: Int) {
        val mFd = open(uartName)
        if (mFd == -1) {
            listener?.onOpenPort("Open Failure!")
        } else {
            setSerialPortParams(baudrate, UartConfig.DATA_BITS, UartConfig.STOP_BITS, UartConfig.PARITY)
            mThread = Thread(this)
            mThread?.start()
            listener?.onOpenPort(null)
        }
    }

    private fun open(uartName: String): Int {
        fd = open_native("/dev/$uartName")
        blockModel = UartConfig.NON_BLOCK
        return if (fd > 0) 0 else -1
    }

    private fun setSerialPortParams(baudrate: Int, dataBits: Int, stopBits: Int, parity: Char): Int {
        return setSerialPortParams_native(baudrate, dataBits, stopBits, parity, fd)
    }

    override fun getComPorts(pid: String, vid: String): Array<String> {
        return stylGetComPorts(pid, vid)
    }

    override fun getComPort(pid: String, vid: String): String {
        return stylGetComport(pid, vid)
    }

    override fun closePort() {
        close_native(fd)
        fd = -1
        mThread?.interrupt()
    }

    override fun readData(timeout: Int): ByteArray {
        return read_data_native(timeout, fd)
    }

    override fun writeData(buf: ByteArray): Int {
        return write_native(buf, buf.size, fd)
    }

    override fun run() {
        while (fd != -1) {
            val mByte = readData(timeoutRead)
            if (mByte.isNotEmpty()) {
                Log.d("log_fuji", "receive: " + GeneralUtils.hexToString(mByte))
                val mes = Message.obtain(mHandler, MESSAGE_LOG)
                mes.obj = mByte
                mHandler.sendMessage(mes)
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MESSAGE_LOG -> {
                    val data = msg.obj as ByteArray
                    if (data.isNotEmpty()) {
                        listener?.onReceiveData(data)
                    }
                }
            }
        }
    }

    external fun setSerialPortParams_native(baudrate: Int, dataBits: Int, stopBits: Int, parity: Char, fd: Int): Int
    external fun open_native(uartName: String): Int
    external fun close_native(fd: Int)
    external fun read_data_native(timeout: Int, fd: Int): ByteArray
    external fun write_native(buf: ByteArray, writesize: Int, fd: Int): Int
    external fun stylGetComPorts(pid: String, vid: String): Array<String>
    external fun stylGetComport(pid: String, vid: String): String
}