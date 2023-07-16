package com.styl.pa.modules.peripheralsManager.terminalManager

import com.styl.castle_terminal_upt1000_api.connector.PaymentCallback
import com.styl.castle_terminal_upt1000_api.define.BaseMessage

/**
 * Created by NgaTran on 10/12/2020.
 */
interface ICustomPaymentCallback : PaymentCallback {
    fun onACKTimeout()
    fun onUnablePayment(libError: Int, resErrorMessage: Int?)
    fun writePaymentData(data: BaseMessage?)
}