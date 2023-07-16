package com.styl.pa.modules.kioskactivation.interactor

import androidx.annotation.VisibleForTesting
import com.styl.pa.entities.kioskactivation.KioskActivationRequest
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.kioskactivation.IKioskActivationContract
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by trangpham on 9/26/2018
 */
class KioskActivationInteractor(var output: IKioskActivationContract.IInteractorOutput?) : IKioskActivationContract.IInteractor, BaseInteractor() {

    private val compositeDisposable = CompositeDisposable()

    private var kioskService = ServiceGenerator.createService(IKioskServices::class.java)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setKioskService(services: IKioskServices) {
        this.kioskService = services
    }

    override fun activateKiosk(request: KioskActivationRequest?) {
        val disposable = kioskService.activateKiosk(request)
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
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        output = null
        compositeDisposable.clear()
    }
}