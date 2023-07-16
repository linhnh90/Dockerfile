package com.styl.pa.modules.paymentSuccessful.interactor

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.sendEmail.SendEmailRequest
import com.styl.pa.entities.sendEmail.SendEmailResponse
import com.styl.pa.entities.sendmail.SendMailRequest
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.paymentSuccessful.IPaymentSuccessfulContract
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by trangpham on 9/30/2018
 */
class PaymentSuccessfulInteractor(var output: IPaymentSuccessfulContract.IInteractorOutput?) : IPaymentSuccessfulContract.IInteractor, BaseInteractor() {

    private val compositeDisposable = CompositeDisposable()
    private val kioskService = ServiceGenerator.createService(IKioskServices::class.java)

    @ExcludeFromJacocoGeneratedReport
    override fun sendMail(token: String?, request: SendMailRequest?) {
        val disposable = kioskService.sendMail(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = @ExcludeFromJacocoGeneratedReport {
                            onError(it, output)
                        },
                        onNext = @ExcludeFromJacocoGeneratedReport {
                            onResponse(it, output)
                        }
                )
        compositeDisposable.add(disposable)
    }

    @ExcludeFromJacocoGeneratedReport
   override fun sendEmail(token: String?, request: SendEmailRequest?, callBack: IBaseContract.IBaseInteractorOutput<SendEmailResponse>) {
        val disposable = kioskService.sendEmail(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = @ExcludeFromJacocoGeneratedReport {
                            onError(it, callBack)
                        },
                        onNext = @ExcludeFromJacocoGeneratedReport {
                            onResponse(it, callBack)
                        }
                )
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        output = null
    }
}