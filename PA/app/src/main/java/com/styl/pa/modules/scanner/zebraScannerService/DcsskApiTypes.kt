package com.styl.pa.modules.scanner.zebraScannerService

class DcsskApiTypes {
    companion object {
        val ACTION_SCANNER_CONNECTED = "com.zebra.scannercontrol.connected"
        val ACTION_SCANNER_APPEARED = "com.zebra.scannercontrol.appeared"
        val ACTION_SCANNER_DISCONNECTED = "com.zebra.scannercontrol.disconnected"
        val ACTION_SCANNER_BARCODE_RECEIVED = "com.zebra.scannercontrol.barcode.received"
        val ACTION_SCANNER_IMAGE_RECEIVED = "com.zebra.scannercontrol.image.received"
        val ACTION_SCANNER_VIDEO_RECEIVED = "com.zebra.scannercontrol.video.received"

        //Type of data recieved
        val SCANNER_CONNECTED = 30
        val SCANNER_APPEARED = 31
        val SCANNER_DISCONNECTED = 32
        val SCANNER_BARCODE_RECEIVE = 33
        val SCANNER_IMAGE_RECEIVED = 34
        val SCANNER_VIDEO_RECEIVED = 35
        val SCANNER_DISAPPEARED = 36
        val SYMBOLOGIES_CONFIGURATION = 37
        val TRIGGER_CONFIGURATION = 38
        val AIM_CONFIGURATION = 39

        //Type of handle
        val INIT_EVENT = 100
        val CONFIG_EVENT = 101

    }
}