package com.styl.pa.modules.setting.txnLog.interactor

import android.content.Context
import com.styl.pa.database.AppDatabase
import com.styl.pa.database.DbWorkerThread
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.modules.setting.txnLog.ITransactionLogContact
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TransactionLogInteractor(
        private val context: Context,
        private val callback: ITransactionLogContact.ITransactionLogOutput
): ITransactionLogContact.IInteractor {

    private val compositeDisposable = CompositeDisposable()
    private var appDb: AppDatabase? = null
    private var dbWorkerThread: DbWorkerThread? = null

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        compositeDisposable.clear()
        dbWorkerThread?.quit()
    }

    override fun onViewCreated() {
        dbWorkerThread = DbWorkerThread("dbWorkThread")
        dbWorkerThread?.start()

        appDb = AppDatabase.getInstance(context)
    }

    override fun getTxnLogs(currentOffset: Int, logsPerRequest: Int) {
        val disposable = Observable.fromCallable {
            return@fromCallable appDb?.txnLogDao()?.getPendingLogs(currentOffset, logsPerRequest)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    callback.getTxnLogsResult(it)
                }
        compositeDisposable.add(disposable)
    }

    override fun deleteTxn(log: PaymentRequest) {
        val task = Runnable {
            appDb?.receiptDao()?.deleteByTxn(log.txnNo)
            appDb?.txnItemDao()?.deleteItems(log.txnNo)
            appDb?.txnLogDao()?.deleteLog(log.txnNo)
        }
        dbWorkerThread?.postTask(task)
    }
}