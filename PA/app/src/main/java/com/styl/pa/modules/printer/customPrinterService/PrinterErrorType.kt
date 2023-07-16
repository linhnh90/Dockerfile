package com.styl.pa.modules.printer.customPrinterService

/**
 * Created by Ngatran on 09/18/2018.
 */
class PrinterErrorType {
    companion object {
        val ERROR_CODE = 399
        val VALID_CODE = 0

        val PRINTER_CONNECTED = 300
        val PRINTER_DISCONECT = 301

        val LOW_PAPER = 302    //"LOW_PAPER"
        val ERROR_NO_PAPER = 303     //"NO_PAPER"
        val ERROR_NO_COVER = 304     //"PRINTER_OPEN"
        val ERROR_NO_HEAD = 305      //"PRINTER_OPEN"
        val ERROR_SPOOLING = 306     //"JAMMED_PAPER"
        val ERROR_PAPER_JAM = 307    //"JAMMED_PAPER"
        val ERROR_CUTTER_ERROR = 308    //"ERROR_CUTTER"
        val LOST_CONNECT_PRINTER = 309  //"LOST_CONNECT_PRINTER"

        val ERR_NULL = -1
        val ERR_CALL_STATUS_FAIL = -2

        val errorHash: HashMap<Int, String> = hashMapOf(
                LOW_PAPER to "Low paper",
                ERROR_NO_PAPER to "No paper",
                ERROR_NO_COVER to "Printer open",
                ERROR_NO_HEAD to "Printer open",
                ERROR_SPOOLING to "Jammed paper",
                ERROR_PAPER_JAM to "Jammed paper",
                ERROR_CUTTER_ERROR to "Cut error ",
                ERROR_CODE to "Error")
    }
}