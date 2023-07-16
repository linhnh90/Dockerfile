package com.styl.pa.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Handler
import android.util.Log
import com.styl.pa.BuildConfig
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.database.AppDatabase
import com.styl.pa.database.DbWorkerThread
import com.styl.pa.entities.payment.PaymentRequest
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

/**
 * Created by trangpham on 10/11/2018
 */
@ExcludeFromJacocoGeneratedReport
class MyJobService : JobService() {

    companion object {

        const val EXTRA_WORK_DURATION = BuildConfig.APPLICATION_ID + ".extras.EXTRA_WORK_DURATION"

        private const val THRESHOLD = 6 * 7 * 24 * 60 * 60
    }

    private var kioskService = ServiceGenerator.createService(IKioskServices::class.java)
    private val compositeDisposable = CompositeDisposable()

    private var appDb: AppDatabase? = null
    private var dbWorkerThread: DbWorkerThread? = null

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Log.e("MyJobService", "onCreate")
        }

        LogManager.i("Start Job to submit pending txn")

        dbWorkerThread = DbWorkerThread("dbWorkThread")
        dbWorkerThread?.start()
        dbWorkerThread?.setWorkerHandler(Handler(dbWorkerThread?.looper))

        appDb = AppDatabase.getInstance(this)
    }

    @ExcludeFromJacocoGeneratedReport
    fun handleTask() {
        // delete old records which is submitted successful to server
        val time = System.currentTimeMillis() / 1000
        val txnLogs: MutableList<PaymentRequest> = ArrayList()
        val submittedTxn = appDb?.txnLogDao()?.getSubmittedLogs(time, THRESHOLD)
        if (submittedTxn != null) {
            txnLogs.addAll(submittedTxn)
        }
        val outdatedTxn = appDb?.txnLogDao()?.getOutdatedLogs(time, THRESHOLD)
        if (outdatedTxn != null) {
            txnLogs.addAll(outdatedTxn)
        }
        // delete all items in txn log
        for (txnLog in txnLogs) {
            appDb?.txnItemDao()?.deleteItems(txnLog.txnNo)
        }
        // delete all transaction
        appDb?.txnLogDao()?.deleteSubmittedLogs(time, THRESHOLD)
        appDb?.txnLogDao()?.deleteOutdatedLogs(time, THRESHOLD)

        // submit record which is not submitted
        val pendingLogs = appDb?.txnLogDao()?.getPendingLogs()
        if (pendingLogs != null) {
            for (log in pendingLogs) {
                // get all item
                val items = appDb?.txnItemDao()?.getItemsBy(log.txnNo)
                log.items = items

                // submit server
                val token = MySharedPref(this).eKioskHeader
                val disposable = kioskService.updatePayment(token, log)
                        .subscribeOn(Schedulers.io())
                        .subscribeBy(
                                onError = @ExcludeFromJacocoGeneratedReport {},
                                onNext = @ExcludeFromJacocoGeneratedReport {
                                    if (it.isSuccessful) {
                                        try {
                                            val errorCode =
                                                    Integer.parseInt(it.headers().get("errorCode"))
                                            // update status to SUBMITTED
                                            if (errorCode == 0 &&
                                                    (log.paymentStatus == PaymentRequest.STATUS_PAID ||
                                                            log.paymentStatus == PaymentRequest.STATUS_GENERATED ||
                                                            log.paymentStatus == PaymentRequest.STATUS_UPDATED ||
                                                            log.paymentStatus == PaymentRequest.STATUS_UNSUCCESSFUL)) {
                                                appDb?.txnLogDao()?.updateStatus(
                                                        PaymentRequest.STATUS_DONE,
                                                        log.txnNo
                                                )
                                            }
                                        } catch (e: NumberFormatException) {
                                            LogManager.d("Format error failed")
                                        }
                                    }
                                }
                        )
                compositeDisposable.add(disposable)
            }
        }
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {

        try {
            val duration = jobParameters.extras.getLong(EXTRA_WORK_DURATION, MainActivity.WORK_DURATION)
            val handler = Handler()
            handler.postDelayed(
                    @ExcludeFromJacocoGeneratedReport {
                        jobFinished(jobParameters, false)
                    }, duration
            )

            if (BuildConfig.DEBUG) {
                Log.e("MyJobService", "onStartJob")
            }
            val task = Runnable @ExcludeFromJacocoGeneratedReport {
                handleTask()
            }
            dbWorkerThread?.postTask(task)
        } catch (e: Exception) {
            LogManager.d("Handle task failed")
        }

        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        if (BuildConfig.DEBUG) {
            Log.e("MyJobService", "onStopJob")
        }
        compositeDisposable.clear()
        return false
    }

    override fun onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.e("MyJobService", "onDestroy")
        }
        LogManager.i("Stop Job to submit pending txn")
        compositeDisposable.clear()
        AppDatabase.destroyInstance()
        dbWorkerThread?.quit()
        dbWorkerThread = null
        super.onDestroy()
    }
}