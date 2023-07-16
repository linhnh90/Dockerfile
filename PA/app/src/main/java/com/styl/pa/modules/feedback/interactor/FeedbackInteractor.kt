package com.styl.pa.modules.feedback.interactor

import com.styl.pa.entities.feedback.FeedbackRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.feedback.IFeedbackContact
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ngatran on 09/27/2019.
 */
class FeedbackInteractor : IFeedbackContact.IInteractor, BaseInteractor() {
    private val compositeDisposable = CompositeDisposable()
    var services = ServiceGenerator.createService(IKioskServices::class.java)

    override fun reportFeedBack(token: String, request: FeedbackRequest, output: IBaseContract.IBaseInteractorOutput<SendMailResponse>?) {
        val d = services.reportFeedback(token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            onError(it, output)
                        },
                        onNext = {
                            onResponse(it, output)
                        }
                )

        compositeDisposable.add(d)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}