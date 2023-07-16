package com.styl.pa.modules.peripheralsManager.usbSerial.config

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.felhr.usbserial.CDCSerialDevice
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import com.styl.pa.modules.peripheralsManager.GeneralUtils
import com.styl.pa.modules.peripheralsManager.uart.command.BaseCommand
import com.styl.pa.modules.peripheralsManager.uart.command.CommandRequest
import com.styl.pa.modules.peripheralsManager.uart.command.CommandResponse
import com.styl.pa.modules.peripheralsManager.uart.command.UsbSerialResponse
import com.styl.pa.modules.peripheralsManager.usbSerial.listener.ScanUsbDeviceListener
import com.styl.pa.modules.peripheralsManager.usbSerial.listener.UsbDeviceConnectListener
import com.styl.pa.modules.peripheralsManager.usbSerial.listener.UsbSerialListener
import com.styl.pa.modules.peripheralsManager.usbSerial.utils.UsbConnectError
import com.styl.pa.utils.LogManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class UsbSerial : UsbSerialListener {

    private var activity: Activity? = null
    private var usbManager: UsbManager? = null
    private var scanUsbDeviceListener: ScanUsbDeviceListener? = null
    private var usbDeviceConnectListener: UsbDeviceConnectListener? = null
    private var device: UsbDevice? = null
    private var connection: UsbDeviceConnection? = null
    private var serialPort: UsbSerialDevice? = null
    private var usbSerialHandle: UsbSerialResponse? = null
    private var isRequestWrite: Boolean = false
    private var dataRequestWrite: ByteArray? = null
    private var command: Byte? = null
    private var serialNumber: Byte? = null
    private var listener: PublishSubject<ByteArray> = PublishSubject.create()
    private var listenerError: PublishSubject<String> = PublishSubject.create()
    private var listenerSendData: PublishSubject<ByteArray> = PublishSubject.create()
    private var compositeManager: CompositeDisposable? = CompositeDisposable()

    val mListener = listener
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                    onError = {
                        usbDeviceConnectListener?.onUsbReadDataFail(it.message)
                    },
                    onNext = {
                        usbDeviceConnectListener?.onUsbReadCallback(it)
                    }
            )

    val mListenerError = listenerError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                    onError = {
                        usbDeviceConnectListener?.onUsbReadDataFail(it.message)
                    },
                    onNext = {
                        usbDeviceConnectListener?.onUsbReadDataFail(it)
                    }
            )

    val mListenerSendData = listenerSendData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                    onError = {
                    },
                    onNext = {
                        usbDeviceConnectListener?.onUsbSendDataSuccess(it)
                    }
            )

    constructor(context: Context?) {
        this.activity = context as Activity
        usbManager = activity?.getSystemService(Context.USB_SERVICE) as UsbManager
        usbSerialHandle = UsbSerialResponse()
        usbSerialHandle?.setListener(listenerError, listener)
        compositeManager?.add(mListener)
        compositeManager?.add(mListenerError)
        compositeManager?.add(mListenerSendData)
    }

    override fun stopScanDevices() {
        activity?.unregisterReceiver(usbReceiver)
        compositeManager?.dispose()
        compositeManager?.clear()
    }

    private fun acceptUsbConnect() {
        if (device != null) {
            connection = usbManager?.openDevice(device)
            ConnectionThread().start()
        }
    }

    private var usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UsbConfig.ACTION_USB_ATTACHED -> {
                    findSerialPortDevice()
                }

                UsbConfig.ACTION_USB_PERMISSION -> {
                    if (intent.extras?.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED) == true) {
                        // User accepted our USB connection
                        acceptUsbConnect()
                    } else {
                        // User not accepted our USB connection.
                        usbDeviceConnectListener?.onUsbDeviceConnect(UsbConnectError.USB_PERMISSION_NOT_GRANTED)
                    }
                }

                UsbConfig.ACTION_USB_DETACHED -> {
                    serialPort?.close()
                    usbDeviceConnectListener?.onUsbDeviceDisconnect()
                }
            }
        }
    }

    private fun findSerialPortDevice() {
        var devices = usbManager?.deviceList
        if (devices != null) {
            for (device in devices) {
                if (device != null) {
                    val vidDevice = device?.value?.vendorId ?: ""
                    val pidDevice = device?.value?.productId ?: ""
                    if (vidDevice.equals(UsbConfig.VID) && pidDevice.equals(UsbConfig.PID)) {
                        scanUsbDeviceListener?.onReceiveUsbDevice(device.value)
                        break
                    }
                }
            }
        }
    }

    override fun scanUsbDevices(scanUsbDeviceListener: ScanUsbDeviceListener) {
        this.scanUsbDeviceListener = scanUsbDeviceListener
        val filter = IntentFilter()
        filter.addAction(UsbConfig.ACTION_USB_PERMISSION)
        filter.addAction(UsbConfig.ACTION_USB_DETACHED)
        filter.addAction(UsbConfig.ACTION_USB_ATTACHED)
        activity?.registerReceiver(usbReceiver, filter, com.styl.pa.utils.GeneralUtils.COMMON_PERMISSION, null)
    }

    override fun connectDevice(device: UsbDevice, listener: UsbDeviceConnectListener) {
        this.usbDeviceConnectListener = listener
        this.device = device
        if (UsbSerialDevice.isSupported(device)) {
            if (usbManager?.hasPermission(device)!!) {
                acceptUsbConnect()
            } else {
                requestUserPermission(device)
            }
        }
    }

    /*
    * Request user permission. The response will be received in the BroadcastReceiver
    */
    private fun requestUserPermission(device: UsbDevice) {
        var intent = PendingIntent.getBroadcast(activity, 0, Intent(UsbConfig.ACTION_USB_PERMISSION), 0)
        usbManager?.requestPermission(device, intent)
    }

    /*
     * A simple thread to open a serial port.
     * Although it should be a fast operation. moving usb operations away from UI thread is a good thing.
     */
    private inner class ConnectionThread : Thread() {
        override fun run() {
            serialPort = UsbSerialDevice.createUsbSerialDevice(UsbSerialDevice.FTDI, device, connection, 0)
            if (serialPort != null) {
                if (serialPort!!.open()) {
                    serialPort!!.setBaudRate(UsbConfig.BAUD_RATE)
                    serialPort!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
                    serialPort!!.setStopBits(UsbSerialInterface.STOP_BITS_1)
                    serialPort!!.setParity(UsbSerialInterface.PARITY_EVEN)
                    serialPort!!.read(usbReadCallback)

                    usbDeviceConnectListener?.onUsbDeviceConnect(null)
                } else {
                    if (serialPort is CDCSerialDevice) {
                        usbDeviceConnectListener?.onUsbDeviceConnect(UsbConnectError.ACTION_CDC_DRIVER_NOT_WORKING)
                    } else {
                        usbDeviceConnectListener?.onUsbDeviceConnect(UsbConnectError.ACTION_USB_DEVICE_NOT_WORKING)
                    }
                }
            } else {
                usbDeviceConnectListener?.onUsbDeviceConnect(UsbConnectError.ACTION_USB_NOT_SUPPORTED)
            }
        }
    }

    override fun writeData(data: ByteArray, command: Byte) {
        this.isRequestWrite = true
        this.command = command
        this.dataRequestWrite = data
    }

    private fun writeCommand() {
        if (this.dataRequestWrite != null && this.serialNumber != null && this.command != null) {
            val request = CommandRequest(this.command!!, this.serialNumber!!, this.dataRequestWrite!!)
            var data = request.build()
            serialPort?.write(data)

            listenerSendData.onNext(data)

            this.isRequestWrite = false
            this.dataRequestWrite = null
            this.command = null
        }
    }

    fun sendACKCommand(): ByteArray? {
        var data: ByteArray? = null
        if (this.serialNumber != null) {
            var request = CommandRequest(BaseCommand.ACK, this.serialNumber!!, ByteArray(0))
            data = request.build()
            serialPort?.write(data)
        }
        return data
    }

    /*
     *  Data received from serial port will be received here. Just populate onReceivedData with your code
     *  In this particular example. byte stream is converted to String and send to UI thread to
     *  be treated there.
     */
    private var usbReadCallback = object : UsbSerialInterface.UsbReadCallback {
        override fun onReceivedData(data: ByteArray?) {
            var mData = usbSerialHandle?.onHandleUsbResponse(data)
            if (mData != null) {
                var response = CommandResponse(mData)
                when (response.command) {
                    BaseCommand.POLL -> {
                        serialNumber = response.serialNumber
                        if (this@UsbSerial.isRequestWrite) {
                            writeCommand()
                        }
                    }
                    BaseCommand.NACK -> {
                        listenerError.onNext("NACK")
                    }
                    BaseCommand.ACK -> {
                        LogManager.d("usbReadCallback - ACK")
                    }
                    else -> {
                        listener.onNext(mData)

                        //PC send ACK to VMC
                        var data = sendACKCommand()
                        listenerError.onNext("Send ACK: " + GeneralUtils.hexToString(data))
                    }
                }
            }
        }
    }
}