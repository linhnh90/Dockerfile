package com.styl.pa.modules.peripheralsManager.terminalManager

import com.styl.pa.entities.wirecard.TransactionResponse

/**
 * Created by NgaTran on 10/9/2020.
 */
interface IPaymentResultListener {
    fun onPaymentSuccess(msgHeaderError: String, txnResponse: TransactionResponse?, bytes: ByteArray?)
    fun onPaymentFail(msgHeaderError: String, txnResponse: TransactionResponse?, bytes: ByteArray?)
    fun onUnablePayment(libError: Int, msgHeaderError: Int, resErrorMessage: Int?)
    fun extendSessionTimeout(extendTime: Int)
    fun changePaymentProgressStatus(status: Int)
}