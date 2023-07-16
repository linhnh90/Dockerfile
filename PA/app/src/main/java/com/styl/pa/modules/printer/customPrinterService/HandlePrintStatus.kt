package com.styl.pa.modules.printer.customPrinterService

import com.styl.pa.entities.GeneralException

/**
 * Created by Ngatran on 09/18/2018.
 */
interface HandlePrintStatus {
    fun getStatus(result: GeneralException)
    fun endCheckStatus()
}