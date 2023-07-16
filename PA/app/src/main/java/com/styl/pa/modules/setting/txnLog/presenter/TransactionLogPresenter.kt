package com.styl.pa.modules.setting.txnLog.presenter

import android.content.Context
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.modules.setting.txnLog.ITransactionLogContact
import com.styl.pa.modules.setting.txnLog.interactor.TransactionLogInteractor

class TransactionLogPresenter(
        private var context: Context,
        private var view: ITransactionLogContact.IView
): ITransactionLogContact.IPresenter, ITransactionLogContact.ITransactionLogOutput{

    private val interactor = TransactionLogInteractor(context, this)

    override fun onViewCreated() {
        interactor.onViewCreated()
    }

    override fun onDestroy() {
        interactor.onDestroy()
    }

    override fun getTxnLogs(currentOffset: Int, logsPerRequest: Int) {
        view.showLoading()
        interactor.getTxnLogs(currentOffset, logsPerRequest)
    }

    override fun deleteTxn(log: PaymentRequest) {
        interactor.deleteTxn(log)
    }

    override fun getTxnLogsResult(txnLogs: List<PaymentRequest>?) {
        view.dismissLoading()
        view.getTxnLogsResponse(txnLogs)
    }
}