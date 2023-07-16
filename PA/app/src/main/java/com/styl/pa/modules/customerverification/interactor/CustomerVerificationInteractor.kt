package com.styl.pa.modules.customerverification.interactor

import androidx.annotation.VisibleForTesting
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.modules.base.BaseInteractor
import com.styl.pa.modules.customerverification.CustomerVerificationContract
import com.styl.pa.services.IKioskServices
import com.styl.pa.services.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class CustomerVerificationInteractor(var output: CustomerVerificationContract.CustomerVerificationOutput?) : CustomerVerificationContract.Interactor, BaseInteractor() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setKioskServices(services: IKioskServices) {
        this.service = services
    }

    private var service = ServiceGenerator.createService(IKioskServices::class.java)

    private val compositeDisposable = CompositeDisposable()

    override fun verifyCustomer(token: String?, request: ProxyRequest<EmptyRequest>) {
        val disposable = service.getCustomerDetail(request, token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = { e ->
                            onError(e, output)
                        },
                        onNext = { response ->
                            handleResponseProxy(response, output)
                        }
                )
        compositeDisposable.add(disposable)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        compositeDisposable.clear()
        output = null
    }
}