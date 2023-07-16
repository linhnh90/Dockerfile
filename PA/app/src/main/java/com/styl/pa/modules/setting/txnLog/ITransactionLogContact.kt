package com.styl.pa.modules.setting.txnLog

import com.styl.pa.entities.payment.PaymentRequest

interface ITransactionLogContact {
    interface IView {
        fun showLoading()
        fun dismissLoading()
        fun getTxnLogsResponse(txnLogs: List<PaymentRequest>?)
    }

    interface IPresenter {
        fun onViewCreated()
        fun onDestroy()
        fun getTxnLogs(currentOffset: Int, logsPerRequest: Int)
        fun deleteTxn(log: PaymentRequest)
    }

    interface IInteractor {
        fun onDestroy()
        fun onViewCreated()
        fun getTxnLogs(currentOffset: Int, logsPerRequest: Int)
        fun deleteTxn(log: PaymentRequest)
    }

    interface ITransactionLogOutput {
        fun getTxnLogsResult(txnLogs: List<PaymentRequest>?)
    }
}