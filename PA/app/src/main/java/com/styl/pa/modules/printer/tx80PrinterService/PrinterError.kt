package com.styl.pa.modules.printer.tx80PrinterService

import android.content.Context
import com.styl.pa.R
import com.tx.printlib.Const

/**
 * Created by Nga Tran on 8/5/2020.
 */
object PrinterError {
    val ERR_ARCV = Const.TX_STAT_ARCV_ERR  //Automatically recoverable error
    val ERR_URCV = Const.TX_STAT_URCV_ERR  //Unrecoverable error
    val ERR_RCV = Const.TX_STAT_RCV_ERR  //Recoverable error (need manual intervention)
    val ERR_CUT = Const.TX_STAT_CUT_ERR  //Cutter error
    val ERR_BUSY = Const.TX_STAT_BUSY  //Busy
    val ERR_COVER = Const.TX_STAT_COVER  //The cover of the printer movement is open
    val ERR_ERROR = Const.TX_STAT_ERROR  //Printer error
    val NOERROR = Const.TX_STAT_NOERROR  //No fault
    val ERR_PAPEREND = Const.TX_STAT_PAPEREND  //Out of paper
    val ERR_NE = Const.TX_STAT_PAPER_NE  //There is almost no paper

    val ERR_NULL = -1
    val ERR_CALL_STATUS_FAIL = -2

    val printerStatus = hashMapOf(
            Const.TX_STAT_ARCV_ERR to PrinterStatus(ERR_ARCV, R.string.err_arcv),
            Const.TX_STAT_URCV_ERR to PrinterStatus(ERR_URCV, R.string.err_urcv),
            Const.TX_STAT_RCV_ERR to PrinterStatus(ERR_RCV, R.string.err_rcv),
            Const.TX_STAT_CUT_ERR to PrinterStatus(ERR_CUT, R.string.err_cut),
            Const.TX_STAT_BUSY to PrinterStatus(ERR_BUSY, R.string.err_busy),
            Const.TX_STAT_COVER to PrinterStatus(ERR_COVER, R.string.err_cover),
            Const.TX_STAT_ERROR to PrinterStatus(ERR_ERROR, R.string.err_error),
            Const.TX_STAT_NOERROR to PrinterStatus(NOERROR, R.string.noerror),
            Const.TX_STAT_PAPEREND to PrinterStatus(ERR_PAPEREND, R.string.err_paperend),
            Const.TX_STAT_PAPER_NE to PrinterStatus(ERR_NE, R.string.err_ne)
    )

    class PrinterStatus(var errorCode: Int, var errorMessage: Int) {
        fun getErrorMessage(context: Context?): String? {
            return context?.getString(errorMessage)
        }
    }

    // Printer status
    val ERR_PRINTER_TURN_OFF = 0x00

    // Printer status 2
    val NORMAL_STATUS : Long = 0x0018
    val ERR_LOW_PAPER : Long = 0x8018
    val ERR_NO_PAPER  : Long = 0x8038
    val ERR_PAPER_NOT_PROPERLY_INSERTED : Long = 0x0038
}