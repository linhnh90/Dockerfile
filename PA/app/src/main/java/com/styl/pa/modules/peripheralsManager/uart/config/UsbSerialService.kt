package com.styl.pa.modules.peripheralsManager.uart.config

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
import com.styl.pa.modules.peripheralsManager.uart.listener.IUartService
import com.styl.pa.modules.peripheralsManager.uart.listener.UartListener
import com.styl.pa.modules.peripheralsManager.usbSerial.config.UsbConfig
import com.styl.pa.modules.peripheralsManager.usbSerial.listener.ScanUsbDeviceListener
import com.styl.pa.modules.peripheralsManager.usbSerial.utils.UsbConnectError
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager

class UsbSerialService : IUartService {

    private var listener: UartListener? = null
    private var usbManager: UsbManager? = null
    private var context: Context? = null
    private var connection: UsbDeviceConnection? = null
    private var serialPort: UsbSerialDevice? = null
    private var device: UsbDevice? = null

    constructor(context: Context?) {
        this.context = context
        if (context?.getSystemService(Context.USB_SERVICE) != null) {
            usbManager = context?.getSystemService(Context.USB_SERVICE) as UsbManager
        }

        LogManager.i("usbManage " + usbManager)
    }

    override fun setListener(listener: UartListener) {
        this.listener = listener
    }

    override fun setReadTimeout(timeout: Int) {
        LogManager.d("setReadTimeout")
    }

    override fun getComPorts(pid: String, vid: String): Array<String> {
        return emptyArray()
    }

    override fun getComPort(pid: String, vid: String): String {
        return ""
    }

    override fun openPort(uartName: String, baudrate: Int) {
        scanUsbDevices()
        findSerialPortDevice()
    }

    override fun closePort() {
        serialPort?.close()
    }

    override fun readData(timeout: Int): ByteArray {
        return ByteArray(0)
    }

    override fun writeData(buf: ByteArray): Int {
        serialPort?.write(buf)
        return 1
    }

    fun scanUsbDevices() {
        val filter = IntentFilter()
        filter.addAction(UsbConfig.ACTION_USB_PERMISSION)
        filter.addAction(UsbConfig.ACTION_USB_DETACHED)
        filter.addAction(UsbConfig.ACTION_USB_ATTACHED)
        context?.registerReceiver(usbReceiver, filter, GeneralUtils.COMMON_PERMISSION, null)
    }

    fun stopScanDevices() {
        context?.unregisterReceiver(usbReceiver)
    }

    private val usbReceiver = object : BroadcastReceiver() {
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
                        listener?.onOpenPort(UsbConnectError.USB_PERMISSION_NOT_GRANTED)
                    }
                }

                UsbConfig.ACTION_USB_DETACHED -> {
                    serialPort?.close()
                }
            }
        }
    }

    private fun findSerialPortDevice() {
        var devices = usbManager?.deviceList
        LogManager.i("findSerialPortDevice " + devices?.size)
        if (devices != null) {
            for (device in devices) {
                if (device != null) {
                    val vidDevice = device?.value?.vendorId ?: ""
                    val pidDevice = device?.value?.productId ?: ""
                    LogManager.i("usb serial: " + device?.value?.deviceName + "vid: " + vidDevice + " pid: " + pidDevice)

                    if (vidDevice.equals(UsbConfig.VID) && pidDevice.equals(UsbConfig.PID)) {
//                    if (vidDevice.equals(UsbConfig.VID) && pidDevice.equals(UsbConfig.PID)) {
                        LogManager.i("scan device: " + device?.value?.deviceName)
                        scanUsbDeviceListener.onReceiveUsbDevice(device.value)
                        break
                    }
                }
            }
        }
    }


    private val scanUsbDeviceListener = object : ScanUsbDeviceListener {
        override fun onReceiveUsbDevice(d: UsbDevice) {
            if (device == null) {
                connectDevice(d)
            }
        }
    }

    fun connectDevice(device: UsbDevice) {
        this.device = device
        if (UsbSerialDevice.isSupported(device)) {
            if (usbManager?.hasPermission(device)!!) {
                acceptUsbConnect()
            } else {
                requestUserPermission(device)
            }
        }
    }

    private fun acceptUsbConnect() {
        if (device != null) {
            connection = usbManager?.openDevice(device)
            ConnectionThread().start()
        }
    }

    /*
   * Request user permission. The response will be received in the BroadcastReceiver
   */
    private fun requestUserPermission(device: UsbDevice) {
        var intent =
                PendingIntent.getBroadcast(context, 0, Intent(UsbConfig.ACTION_USB_PERMISSION), 0)
        usbManager?.requestPermission(device, intent)
    }

    /*
     * A simple thread to open a serial port.
     * Although it should be a fast operation. moving usb operations away from UI thread is a good thing.
     */
    private inner class ConnectionThread : Thread() {
        override fun run() {
            serialPort =
                    UsbSerialDevice.createUsbSerialDevice(UsbSerialDevice.FTDI, device, connection, 0)
            if (serialPort != null) {
                if (serialPort!!.open()) {
                    serialPort!!.setBaudRate(UsbConfig.BAUD_RATE)
                    serialPort!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
                    serialPort!!.setStopBits(UsbSerialInterface.STOP_BITS_1)
                    serialPort!!.setParity(UsbSerialInterface.PARITY_NONE)
                    serialPort!!.read(usbReadCallback)

                    //stop scan usb serial devices
                    stopScanDevices()

                    listener?.onOpenPort(null)
                } else {
                    if (serialPort is CDCSerialDevice) {
                        listener?.onOpenPort(UsbConnectError.ACTION_CDC_DRIVER_NOT_WORKING)
                    } else {
                        listener?.onOpenPort(UsbConnectError.ACTION_USB_DEVICE_NOT_WORKING)
                    }
                }
            } else {
                listener?.onOpenPort(UsbConnectError.ACTION_USB_NOT_SUPPORTED)
            }
        }
    }

    /*
     *  Data received from serial port will be received here. Just populate onReceivedData with your code
     *  In this particular example. byte stream is converted to String and send to UI thread to
     *  be treated there.
     */
    private var usbReadCallback = object : UsbSerialInterface.UsbReadCallback {
        override fun onReceivedData(data: ByteArray?) {
            if (data != null && data.size > 0) {
                listener?.onReceiveData(data)
            }
        }
    }

}