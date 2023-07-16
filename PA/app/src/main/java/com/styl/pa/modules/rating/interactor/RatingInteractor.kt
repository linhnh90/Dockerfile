package com.styl.pa.modules.rating.interactor

import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.rating.RatingRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.rating.IRatingContact
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ngatran on 03/22/2019.
 */
class RatingInteractor : IRatingContact.IInteractor, BaseInteractor() {
    private val compositeDisposable = CompositeDisposable()
    var services = ServiceGenerator.createService(IKioskServices::class.java)

    override fun reportRatingFeedBack(token: String, request: RatingRequest, output: IBaseContract.IBaseInteractorOutput<SendMailResponse>?) {
        val d = services.reportFeedback(token, request.convertFeedbackRequest())
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

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }
}